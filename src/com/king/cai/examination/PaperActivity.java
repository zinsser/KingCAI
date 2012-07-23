package com.king.cai.examination;

import java.io.DataOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.king.cai.R;
import com.king.cai.common.ComunicableActivity;
import com.king.cai.examination.PaperViewAdapter;
import com.king.cai.messageservice.AnswerMessage;
import com.king.cai.messageservice.LogoutRequestMessage;
import com.king.cai.messageservice.RequestImageMessage;
import com.king.cai.messageservice.RequestPaperMessage;
import com.king.cai.platform.KingCAIConfig;

public class PaperActivity  extends ComunicableActivity {
	private final static int GROUP_NORMAL = 0;
	private final static int MENU_FONT_SIZE = 0;
	private final static int MENU_WIFI_MANAGER = 2;
	private final static int MENU_ABOUT = 3;
	
	private final static int SET_FONT_SIZE_DIALOG = 0;
	private final static int DIALOG_ABOUT = 2;
	
	private int mCurrentFontSize = 1;//normal
	
	private final static String s_ConfigFileName = "KingCAI_Config";
	private final static String s_CfgTag_FontSize = "fontsize";
	private final static String s_CfgTag_ExitStatus = "exitstatus"; //true:normal false:exception
	
	private final static String s_PaperTag_ID = "id";
	private final static String s_PaperTag_Question = "question";
	private final static String s_PaperTag_Type = "type";	
	private final static String s_PaperTag_Reference = "reference";
	private final static String s_PaperTag_Answer = "answer";
	
	private final static int s_PaperIdx_ID = 0;
	private final static int s_PaperIdx_Question = 1;
	private final static int s_PaperIdx_Type = 2;
	private final static int s_PaperIdx_Reference = 3;
	private final static int s_PaperIdx_Answer = 4;
	
	public TextView mTextViewTitle = null;
	private ListView mListView = null;
	private Button mBtnCommit = null;
	private Button mBtnFilter = null;
	
	private boolean mOffline = false;
	private String mStudentInfo = null;
	private String mServerIP = null;
	private String mSSID = null;
	private PaperViewAdapter mFullAdapter = null;
	private PaperStatus mPaperStatus = null;	
	
	private QuestionManager mQuestionMgr = new QuestionManager();;
	private AnswerManager mAnswerMgr = null;	
	private View mWrapperView = null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  
        
		mTextViewTitle = (TextView)findViewById(R.id.textViewTitle);        
		ParseIntentExtraParam();
		
		mAnswerMgr = new AnswerManager(mQuestionMgr);
		mPaperStatus = new AnswerStatus(this, mQuestionMgr);
		mPaperStatus.EnterStatus();
		
        mListView = (ListView)findViewById(R.id.lstQuestions);
        mFullAdapter = new PaperViewAdapter(this, mQuestionMgr, mAnswerMgr);
        QuestionListListener listener = new QuestionListListener();
        mListView.setOnScrollListener(listener);

        mListView.setCacheColorHint(0);
        mListView.setAdapter(mFullAdapter);
        
        mBtnCommit = (Button)findViewById(R.id.btnCommit);
        mBtnCommit.setOnClickListener(new CommitClickListener());
       
        mBtnFilter = (Button)findViewById(R.id.btnFilter);
        mBtnFilter.setOnClickListener(new FilterClickListener());
        
		((EditText)findViewById(R.id.txtGoto)).setOnEditorActionListener(new SearchEditorListener());
		

    }
	
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_HOME:
                    return true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_HOME:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onDestroy (){
    	mServiceChannel.sendMessage(new LogoutRequestMessage(), 0);
    	mPaperStatus.LeaveStatus();
    	mPaperStatus = null;
    	super.onDestroy();        
    }
    @Override
    public void onStart(){
    	super.onStart();
   		GetConfig();
   		ResetAdapterFontSize(mFullAdapter);    	
    }
    @Override
    public void onResume(){
    	super.onResume();

    }
    @Override
	protected void doServiceReady(){
        mServiceChannel.sendMessage(new RequestPaperMessage(), 0);		
	}
    
    @Override
    public void onStop(){
    	SaveConfig(true);
   		super.onStop();    	
    }    
    
	public void SaveConfig(boolean bStatus){
		//写入
		SharedPreferences sp = getSharedPreferences(s_ConfigFileName, 0);
		SharedPreferences.Editor editor = sp.edit();

		editor.putInt(s_CfgTag_FontSize, mCurrentFontSize);
		editor.putBoolean(s_CfgTag_ExitStatus, bStatus);
		editor.commit();
    }
    
	private void GetConfig(){
		SharedPreferences sp = getSharedPreferences(s_ConfigFileName, 0);
		//需要指定默认值，如果配置文件没有或者里面没有这个key时的值
		mCurrentFontSize = sp.getInt(s_CfgTag_FontSize, 1);//default is normal
		boolean exitstatus = sp.getBoolean(s_CfgTag_ExitStatus, true);
		if (!exitstatus && !mOffline){
			ClearDatabase();
			ReadPaper();
		}else if (mOffline){//offline
			mQuestionMgr.ImportLocalQuestions();
			mAnswerMgr.AddAnswer(mQuestionMgr);
		}else {
			ClearDatabase();
		}
	}
	
	private void ClearDatabase(){

		PaperDBHelper helper = new PaperDBHelper(getApplicationContext());	
		Cursor c = helper.Query();
		if (c.moveToFirst()){
			do{
				helper.Delete(c.getInt(0));
			}while(c.moveToNext());
		}
		helper.close();		
	}	
	
	public void SavePaper(){
		ArrayList<Integer> ids = mQuestionMgr.GetIDs();
		Answer answer = null;
		ContentValues values = new ContentValues();  		
		PaperDBHelper helper = new PaperDBHelper(getApplicationContext());
		for (Integer id : ids){
			values.put(s_PaperTag_ID, id);  
			values.put(s_PaperTag_Question, mQuestionMgr.GetQuestionItem(id).mDetail);  
			values.put(s_PaperTag_Type, mQuestionMgr.GetQuestionItem(id).mType); 
			values.put(s_PaperTag_Reference, mQuestionMgr.GetQuestionItem(id).mReference);

			if ((answer = mAnswerMgr.GetAnswer(id)) != null){
				values.put(s_PaperTag_Answer, answer.toString());
			}
			
			helper.Insert(values, id);			
		}
		helper.close();
	}
	
	private void ReadPaper(){
		mQuestionMgr.Clear();
		
		PaperDBHelper helper = new PaperDBHelper(getApplicationContext());
		Cursor c = helper.Query();
		c.moveToFirst();
		while (!c.isAfterLast()){
			int id = c.getInt(s_PaperIdx_ID);
			String detail = c.getString(s_PaperIdx_Question);
			int type = c.getInt(s_PaperIdx_Type);
			String reference = c.getString(s_PaperIdx_Reference);
			String answer = c.getString(s_PaperIdx_Answer);
			mQuestionMgr.AddQuestion(id, type, reference, detail, false);
			mAnswerMgr.AddAnswer(id, mQuestionMgr.GetQuestionItem(id), answer);
			c.moveToNext();
		}
		mQuestionMgr.NotifyQuestionArrayChanged();
		c.close();
		helper.close();
	}
    
    
    private void ResetAdapterFontSize(PaperViewAdapter adapter){
    	if (mCurrentFontSize == 0){
    		adapter.SetSmallFontSize();
    	}else if (mCurrentFontSize == 1){
    		adapter.SetNormalFontSize();    	
    	}else{
    		adapter.SetLargeFontSize();
    	}
    }

    @Override
    public void onAttachedToWindow() {  
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);  
        super.onAttachedToWindow();  
    }
    
    public class SearchEditorListener implements TextView.OnEditorActionListener{

		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED 
					|| actionId == EditorInfo.IME_ACTION_DONE){
				if (v.getText().toString().length() != 0){
					Integer gotoItem = Integer.parseInt(v.getText().toString());
					Integer offset = mQuestionMgr.GetUnQuestionCount(gotoItem);
					
					mListView.setAdapter(mFullAdapter);
					ChangeFilterButtonText(R.string.AllQuestions);					
					mPaperStatus.onSearchFinished();
					
					if (gotoItem > 0 && gotoItem <= mListView.getCount() - offset){
						mListView.setSelection(gotoItem + offset - 1);
						HiddenKeyBoard(v);
	//					return true;//返回true时，收到两次actionId回调
					}else{
						showToast(R.string.SearchErrorTip);
					}
				}
			}
			
			return false;
		}
    }
    
    public void HiddenKeyBoard(View v){
		InputMethodManager im =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		im.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add(GROUP_NORMAL, MENU_FONT_SIZE, 0, R.string.FontSize);
    	menu.add(GROUP_NORMAL, MENU_WIFI_MANAGER, 0, R.string.ManagerWifi);
    	menu.add(GROUP_NORMAL, MENU_ABOUT, 0, R.string.About);
    	
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
    	case MENU_FONT_SIZE:
    		showDialog(SET_FONT_SIZE_DIALOG);
    		break;
    	case MENU_WIFI_MANAGER:
    		break;
    	case MENU_ABOUT:
    		showDialog(DIALOG_ABOUT);
    		break;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected Dialog onCreateDialog(int id){
    	if (id == SET_FONT_SIZE_DIALOG){
    		AlertDialog.Builder builder = new AlertDialog.Builder(this)
    			.setTitle(R.string.FontSize)
    			.setNegativeButton(android.R.string.cancel, null)
    			.setSingleChoiceItems(R.array.font_size_tokens, mCurrentFontSize, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						mCurrentFontSize = which;
						PaperViewAdapter curAdapter = (PaperViewAdapter)mListView.getAdapter();
						ResetAdapterFontSize(mFullAdapter);
						ResetAdapterFontSize(curAdapter);
						mListView.setAdapter(curAdapter);
						dismissDialog(SET_FONT_SIZE_DIALOG);
					}
				});
    		return builder.create();
    	}else if (id == DIALOG_ABOUT){
    		AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setTitle(R.string.About)
			.setMessage(GenAboutString())
			.setNegativeButton(android.R.string.ok, null);
    		return builder.create();
    	}
    	
    	return super.onCreateDialog(id);
    } 
    
    private String GenAboutString(){
        List<PackageInfo> pkgInfos = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
        PackageInfo kingPackage = null;
        for(PackageInfo info : pkgInfos) {
        	if ("com.jczhou.kingcai".equals(info.packageName)){
        		kingPackage = info;
        		break;
        	}
        }
        
        
    	String about = "";
        if (kingPackage == null){
        	about = "Software Name：\n" + getResources().getString(R.string.app_name);
        }else{
        	about = String.format(getResources().getString(R.string.AboutDetail), 
    				kingPackage.applicationInfo.loadLabel(getPackageManager()), 
    				kingPackage.versionName + "." + kingPackage.versionCode,
    				kingPackage.applicationInfo.targetSdkVersion == 8 ? "2.2" : 
						kingPackage.applicationInfo.targetSdkVersion == 10 ? "2.3" :
						   kingPackage.applicationInfo.targetSdkVersion == 11 ? "4.0" : "unknown",
					new Date(new File(kingPackage.applicationInfo.sourceDir).lastModified()).toLocaleString());
        }

    	return about;
    }
    
	@Override
	protected EventProcessListener doGetEventProcessListener() {
		return new PaperEventProcessListener();
	}
	
	private class PaperEventProcessListener implements EventProcessListener {
		public void  onTalkingFinished(final String serverip){
		}


		public void onLoginSuccess(String studentinfo) {
		}


		public void onLoginFail() {
		}


		public void onPaperTitleReceived(String title) {
			mQuestionMgr.AddQuestion(new QuestionInfo(QuestionInfo.QUESTION_TYPE_TITLE, null, title, false));
		}


		public void onNewQuestion(String answer, int type, String content, boolean bHasImage) {
			Message msg = mNewQuestionHandler.obtainMessage(NEW_QUESTION);
    		Bundle bundle = new Bundle();
    		bundle.putString("Answer", answer);
    		bundle.putInt("Type", type);
    		bundle.putString("Content", content);
    		bundle.putBoolean("HasImage", bHasImage);
    		msg.setData(bundle);
    		msg.sendToTarget();
		}


		public void onCleanPaper() {
			finish();
		}


		public void onNewImage(Integer id, ByteBuffer buf) {
			Message msg = mNewQuestionHandler.obtainMessage(NEW_IMAGE);
    		Bundle bundle = new Bundle();
    		bundle.putInt("ID", id);
    		bundle.putByteArray("Data", buf.array());
    		msg.setData(bundle);
    		msg.sendToTarget();
		}
	}

    private static int NEW_QUESTION = 20;
	private static int NEW_IMAGE = 21;
    private Handler mNewQuestionHandler = new Handler(){
		
    	@Override
    	public void handleMessage(Message msg){
    		if (msg.what == NEW_QUESTION){
    			Bundle bundle = msg.getData();
    			Integer type = bundle.getInt("Type");
    			String answer = bundle.getString("Answer");
    			String content = bundle.getString("Content");
    			boolean bHasImage = bundle.getBoolean("HasImage");
    			mQuestionMgr.AddQuestion(new QuestionInfo(type, answer, content, bHasImage));
    			if (bHasImage)mServiceChannel.sendMessage(new RequestImageMessage(0), 0);
    		}else if (msg.what == NEW_IMAGE){
    			Bundle bundle = msg.getData();
    			Integer id = bundle.getInt("ID");
    			byte[] data = bundle.getByteArray("Data");

    			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
    			mQuestionMgr.AddQuestionImage(id, bmp);
    		}
    	}
	};
    private void ParseIntentExtraParam(){
        if (getIntent().hasExtra(KingCAIConfig.StudentInfo)){
            Bundle extra = getIntent().getExtras();
            mStudentInfo = extra.getString(KingCAIConfig.StudentInfo);
            mTextViewTitle.setText(mStudentInfo + "  - " + getTitle());
        }
        
        if (getIntent().hasExtra(KingCAIConfig.ServerIP)){
            Bundle extra = getIntent().getExtras();
            String servip = extra.getString(KingCAIConfig.ServerIP);
            mServerIP = new String(servip != null ? servip : "");
        }
        
        if (getIntent().hasExtra(KingCAIConfig.SSID)){
            Bundle extra = getIntent().getExtras();
            String ssid = extra.getString(KingCAIConfig.SSID);
            mSSID = new String(ssid != null ? ssid : "");
        }
        
        if (getIntent().hasExtra(KingCAIConfig.Offline)){
        	Bundle extra = getIntent().getExtras();
        	mOffline = extra.getBoolean(KingCAIConfig.Offline);
        }
    }
    
    public class FilterClickListener implements View.OnClickListener{

		public void onClick(View v) {
    		HiddenKeyBoard(findViewById(R.id.txtGoto));
    		mPaperStatus.onFilterClick(mListView, mFullAdapter);
		}
    }
    
    public class CommitClickListener implements View.OnClickListener{


		public void onClick(View v) {
			mPaperStatus.onCommitClick();
		}
    } 
    
    public class QuestionListListener implements OnScrollListener{

		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		}

		public void onScrollStateChanged(AbsListView arg0, int arg1) {
			HiddenKeyBoard(findViewById(R.id.txtGoto));
		}
	}
 	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//按下键盘上返回按钮
		showToast("keyCode:"+keyCode);
		if(keyCode == KeyEvent.KEYCODE_BACK ){
			AlertDialog dlg = new AlertDialog.Builder(this)
				.setTitle(R.string.ExitPromptTitle)
				.setMessage(R.string.ExitPromptMsg)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.setCancelable(false)
				.create();
			dlg.show();
			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);			
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_HOME){
			showToast("you should commit the paper first then exit");
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}	

	public void CommitAnswers(){
		HiddenKeyBoard(findViewById(R.id.txtGoto));
		mServiceChannel.sendMessage(new AnswerMessage(mAnswerMgr.toString()), 0);
	}
	
	public void SwitchPaperStatus(){
		CommitAnswers();
		mPaperStatus.LeaveStatus();
		mPaperStatus = null;
		mPaperStatus = new CommitedStatus(this);
		mPaperStatus.EnterStatus();
	}
	
	public void ShowDoneInfo(int undoneCount){
		int cnt = mQuestionMgr.GetQuestionCount();
		((TextView)findViewById(R.id.txtAnswerInfo)).setText(cnt + "/" + (cnt - undoneCount));
	}

	public void ShowCorrectInfo(int correctCount){
		int uncorrect = mQuestionMgr.GetQuestionCount() - correctCount;
		String info = String.format("<font color=\"#8ACE1A\">%s</font>/<font color=\"#ff0000\">%s</font>", correctCount, uncorrect);
		((TextView)findViewById(R.id.txtAnswerInfo)).setText(Html.fromHtml(info));

		mListView.setAdapter(mFullAdapter);
		ChangeFilterButtonText(R.string.AllQuestions);
	}	
	 	
	public void InitUncorrectList(ArrayList<Integer> correctList, 
									ArrayList<Integer> uncorrectList){
		Answer aAnswer = null;
    	for (Integer id : mQuestionMgr.GetIDs()){
    		aAnswer = mAnswerMgr.GetAnswer(id);
    		if (aAnswer != null && aAnswer.IsCorrect()){
    			correctList.add(id);
    		}else if (aAnswer != null && !aAnswer.IsCorrect()){
    			uncorrectList.add(id);
    		}
    	}
	}
	
	public void ChangeFilterButtonText(int resID){
		((Button)findViewById(R.id.btnFilter)).setText(resID);
	}

	public PaperStatus getPaperStatus(){
		return mPaperStatus;
	}
	
	public AnswerManager getAnswerManager(){
		return mAnswerMgr;
	}
}

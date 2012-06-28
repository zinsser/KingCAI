package com.jczhou.kingcai.examination;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import android.os.Parcel;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jczhou.kingcai.R;
import com.jczhou.kingcai.common.ComunicableActivity;
import com.jczhou.kingcai.examination.QuestionDetailViewAdapter.ViewHolder;
import com.jczhou.kingcai.examination.QuestionDetailViewAdapter;
import com.jczhou.kingcai.LoginActivity.SpinnerItemClickListener;
import com.jczhou.kingcai.messageservice.AnswerMessage;
import com.jczhou.kingcai.messageservice.LogoutRequestMessage;
import com.jczhou.kingcai.messageservice.RequestPaperMessage;
import com.jczhou.kingcai.SocketService;
import com.jczhou.platform.KingCAIConfig;

public class PaperActivity  extends ComunicableActivity {
	private final static int GROUP_NORMAL = 0;
	private final static int MENU_FONT_SIZE = 0;
	private final static int MENU_COLOR_SCHEME = 1;	
	private final static int MENU_WIFI_MANAGER = 2;
	private final static int MENU_ABOUT = 3;
	
	private final static int SET_FONT_SIZE_DIALOG = 0;
	private final static int SET_COLOR_SCHEME_DIALOG = 1;
	private final static int DIALOG_ABOUT = 2;
	
	private int mCurrentFontSize = 1;//normal
	
	private final static String s_ConfigFileName = "KingCAI_Config";
	private final static String s_CfgTag_FontSize = "fontsize";
	private final static String s_CfgTag_ColorSchem = "colorschem";
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
	
	private ListView mListView = null;
	private Button mBtnCommit = null;
	private Button mBtnFilter = null;
	
	private boolean mOffline = false;
	private String mStudentInfo = null;
	private String mServerIP = null;
	private String mSSID = null;
	private QuestionDetailViewAdapter mFullAdapter = null;
	private PaperStatus mPaperStatus = null;	
	
	private QuestionManager mQuestionMgr = new QuestionManager();;
	private AnswerManager mAnswerMgr = null;	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        
        mListView = (ListView)findViewById(R.id.lstQuestions);
        mFullAdapter = new QuestionDetailViewAdapter(this, new OptionPanelListener());
        mListView.setOnScrollListener(new QuestionListScrollListener());
		mListView.setCacheColorHint(0);
        mListView.setAdapter(mFullAdapter);
        
        mBtnCommit = (Button)findViewById(R.id.btnCommit);
        mBtnCommit.setOnClickListener(new CommitClickListener());
        findViewById(R.id.btnDown).setOnClickListener(new DownClickListener());
        findViewById(R.id.btnUP).setOnClickListener(new UPClickListener());  
        
        mBtnFilter = (Button)findViewById(R.id.btnFilter);
        mBtnFilter.setOnClickListener(new FilterClickListener());
        
		findViewById(R.id.tableInput).setVisibility(View.GONE);
		findViewById(R.id.tableReference).setVisibility(View.GONE);
		((EditText)findViewById(R.id.txtOption)).setOnEditorActionListener(new BlankInputListener());

		((EditText)findViewById(R.id.txtGoto)).setOnEditorActionListener(new SearchEditorListener());
		ParseIntentExtraParam();

		mPaperStatus = new AnswerStatus(this, mQuestionMgr);
		mAnswerMgr = new AnswerManager(mQuestionMgr);
		mQuestionMgr.AddListener(mFullAdapter.mQuestionListener);
		
        SocketService.SendMessage(new RequestPaperMessage(), 0/*mServerIP*/);		
    }
	
    @Override
    public void onDestroy (){
        SocketService.SendMessage(new LogoutRequestMessage(), 0 /*mServerIP*/);
    	mPaperStatus = null;
    	super.onDestroy();        
    }
    
    @Override
    public void onResume(){
   		super.onResume();
   		GetConfig();
   		ResetAdapterFontSize(mFullAdapter);
    }

    @Override
    public void onPause(){
    	SaveConfig(true);
   		super.onPause();    	
    }    

    @Override
    public void onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
//TODO:   		finish();	
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
    	super.onRestoreInstanceState(savedInstanceState);
//TODO:    	GetConfig();
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
			mQuestionMgr.AddQuestion(id, type, reference, detail);
			mAnswerMgr.AddAnswer(id, mQuestionMgr.GetQuestionItem(id), answer);
			c.moveToNext();
		}
		mQuestionMgr.NotifyQuestionArrayChanged();
		c.close();
		helper.close();
	}
    
    
    private void ResetAdapterFontSize(QuestionDetailViewAdapter adapter){
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
						Toast.makeText(PaperActivity.this, R.string.SearchErrorTip, 2000).show();
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
    	menu.add(GROUP_NORMAL, MENU_COLOR_SCHEME, 0, R.string.ColorScheme);
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
    	case MENU_COLOR_SCHEME:
    		showDialog(SET_COLOR_SCHEME_DIALOG);
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
						QuestionDetailViewAdapter curAdapter = (QuestionDetailViewAdapter)mListView.getAdapter();
						ResetAdapterFontSize(mFullAdapter);
						ResetAdapterFontSize(curAdapter);
						mListView.setAdapter(curAdapter);
						dismissDialog(SET_FONT_SIZE_DIALOG);
					}
				});
    		return builder.create();
    	}else if (id == SET_COLOR_SCHEME_DIALOG){
    		LayoutInflater inflater = LayoutInflater.from(this);
    		View v = inflater.inflate(R.layout.colorscheme, null);
    		AlertDialog.Builder builder = new AlertDialog.Builder(this)
    			.setTitle(R.string.ColorScheme)
    			.setNegativeButton(android.R.string.cancel, null)
    			.setView(v);
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
			Log.d("PaperActivity", title);
			mQuestionMgr.AddQuestion(new QuestionInfo(QuestionInfo.QUESTION_TYPE_TITLE, null, title));
		}


		public void onNewQuestion(String answer, int type, String content) {
			Log.d("PaperActivity", content);
			mQuestionMgr.AddQuestion(new QuestionInfo(type, answer, content));
		}


		public void onCleanPaper() {
			finish();
		}


		public void onNewImage(Integer id, ByteBuffer buf) {
			byte[] data = buf.array();
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

			//TODO: item add an imageView to dispaly the image
//			mQuestionImg.setImageBitmap(bmp);
		}
	}
    
    private void ParseIntentExtraParam(){
        if (getIntent().hasExtra(KingCAIConfig.StudentInfo)){
            Bundle extra = getIntent().getExtras();
            mStudentInfo = extra.getString(KingCAIConfig.StudentInfo);
        	setTitle(mStudentInfo + "  - " + getTitle());
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
    		HiddenKeyBoard(findViewById(R.id.tableInput));
    		mPaperStatus.onFilterClick(mListView, mFullAdapter);
		}
    }
    
    public class CommitClickListener implements View.OnClickListener{


		public void onClick(View v) {
			mPaperStatus.onCommitClick();
		}
    }

    private HashMap<Integer, String> mMultiBlankAnswer = new HashMap<Integer, String>();    
    private HashMap<Integer, String> mMultiBlankRefAnswer = new HashMap<Integer, String>();
    
    public class BlankInputListener implements TextView.OnEditorActionListener{

		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED 
					|| actionId == EditorInfo.IME_ACTION_DONE){
				if (v.getText().toString().length() != 0){
					Integer id = (Integer)v.getTag();
					Integer subID = (Integer)findViewById(R.id.txtCurrent).getTag();

					mMultiBlankAnswer.put(subID, new String(v.getText().toString()));

					DoSaveToAnswer(id);

					mPaperStatus.onBlankInputDone(id, mAnswerMgr.GetAnswer(id));
				}
			}else if (actionId == EditorInfo.IME_ACTION_NEXT){
				EditText et = ((EditText)findViewById(R.id.txtOption));
				Integer questionID = (Integer)et.getTag();
				QuestionInfo info = mQuestionMgr.GetQuestionItem(questionID);
				if (info != null && info.mType == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
					Integer subID = (Integer)findViewById(R.id.txtCurrent).getTag();
					mMultiBlankAnswer.put(subID, new String(et.getText().toString()));

					Integer cnt = (Integer)findViewById(R.id.btnDown).getTag();						
					if (++subID == cnt){
						((EditText)findViewById(R.id.txtOption)).setImeOptions(EditorInfo.IME_ACTION_DONE);					
					}
					findViewById(R.id.txtCurrent).setTag(subID);
					ChangeCurrentBlankParam(mMultiBlankAnswer.get(subID), subID);					
				}
			}
			return false;
		}
		
		private void DoSaveToAnswer(Integer id){
			Parcel answer = Parcel.obtain();
			answer.writeInt(mMultiBlankAnswer.size());
			for (Iterator<Integer> iter = mMultiBlankAnswer.keySet().iterator(); 
					iter.hasNext(); ){
				Integer subid = iter.next();
				answer.writeInt(subid);
				answer.writeString(mMultiBlankAnswer.get(subid));			
			}
			answer.setDataPosition(0);
			mAnswerMgr.GetAnswer(id).AddAnswer(answer);
		}
    }      
 
    public class DownClickListener implements View.OnClickListener{


		public void onClick(View v) {
			Integer questionID = (Integer)(findViewById(R.id.txtOption).getTag());
			QuestionInfo info = mQuestionMgr.GetQuestionItem(questionID);
			Integer subID = (Integer)findViewById(R.id.txtCurrent).getTag();			
			if (info != null && info.mType == QuestionInfo.QUESTION_TYPE_MULTIBLANK
					&& subID < (Integer)v.getTag()){

				ChangeCurrentBlankParam(mMultiBlankAnswer.get(++subID), subID);
				if (!mMultiBlankRefAnswer.isEmpty()){
					((TextView)findViewById(R.id.txtReference)).setText(mMultiBlankRefAnswer.get(subID));
				}
				
				if (subID == (Integer)v.getTag()){
					((EditText)findViewById(R.id.txtOption)).setImeOptions(EditorInfo.IME_ACTION_DONE);					
				}else{
					((EditText)findViewById(R.id.txtOption)).setImeOptions(EditorInfo.IME_ACTION_NEXT);
				}
				findViewById(R.id.txtCurrent).setTag(subID);
			}
		}
    }
    
    public class UPClickListener implements View.OnClickListener{

		public void onClick(View v) {
			Integer questionID = (Integer)(findViewById(R.id.txtOption).getTag());
			QuestionInfo info = mQuestionMgr.GetQuestionItem(questionID);
			Integer subID = (Integer)findViewById(R.id.txtCurrent).getTag();			
			if (info != null && info.mType == QuestionInfo.QUESTION_TYPE_MULTIBLANK
					&& subID > 1){
				if (--subID == 0){
					subID = 1;
				}
				ChangeCurrentBlankParam(mMultiBlankAnswer.get(subID), subID);
				if (!mMultiBlankRefAnswer.isEmpty()){
					((TextView)findViewById(R.id.txtReference)).setText(mMultiBlankRefAnswer.get(subID));
				}
				
				if (subID == (Integer)v.getTag()){
					((EditText)findViewById(R.id.txtOption)).setImeOptions(EditorInfo.IME_ACTION_DONE);					
				}else{
					((EditText)findViewById(R.id.txtOption)).setImeOptions(EditorInfo.IME_ACTION_NEXT);
				}

				findViewById(R.id.txtCurrent).setTag(subID);
			}
		}
    }
    
    public void OnQuestionDetailClick(Integer id){
		QuestionInfo info  = mQuestionMgr.GetQuestionItem(id);
		if (info != null && (info.mType == QuestionInfo.QUESTION_TYPE_BLANK
				|| info.mType == QuestionInfo.QUESTION_TYPE_MULTIBLANK)
				&& !findViewById(R.id.tableInput).isShown()){ 
			findViewById(R.id.tableInput).setVisibility(View.VISIBLE);
			findViewById(R.id.tableReference).setVisibility(View.VISIBLE);
			findViewById(R.id.txtOption).setTag(id);
			findViewById(R.id.txtCurrent).setTag(1);//第一空

			Parcel parcelValues  = mAnswerMgr.GetAnswer(id).GetRefAnswer();
			Integer cnt = (Integer)parcelValues.readInt();		
			findViewById(R.id.btnUP).setTag(cnt);
			findViewById(R.id.btnDown).setTag(cnt);
			
			if (info.mType == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
				((EditText)findViewById(R.id.txtOption)).setImeOptions(EditorInfo.IME_ACTION_NEXT);
			}else {
				((EditText)findViewById(R.id.txtOption)).setImeOptions(EditorInfo.IME_ACTION_DONE);
			}
			
			mMultiBlankAnswer.clear();
			mMultiBlankRefAnswer.clear();
			
			mPaperStatus.onBlankInputShow(id, mAnswerMgr.GetAnswer(id));
        }else{
			findViewById(R.id.tableInput).setVisibility(View.GONE);
			findViewById(R.id.tableReference).setVisibility(View.GONE);
			HiddenKeyBoard(findViewById(R.id.tableInput));
		} 		
	}
  
	public class QuestionListScrollListener implements OnScrollListener {

		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		}

		public void onScrollStateChanged(AbsListView arg0, int arg1) {
			findViewById(R.id.tableInput).setVisibility(View.GONE);
			findViewById(R.id.tableReference).setVisibility(View.GONE);
			HiddenKeyBoard(findViewById(R.id.tableInput));			
		}
		
	}
	public class OptionPanelListener 
					extends QuestionDetailViewAdapter.AdapterListener 
					implements View.OnClickListener{
		private int mCurRow = 0;
		public void onClick(View v) {
			Integer id = (Integer)v.getTag();
    		mPaperStatus.onOptionPanelClick(v, mAnswerMgr.GetAnswer(id));
		}

		@Override
		public void OnAdapterLayoutView(ViewHolder holder, Integer id) {
	        do {
				QuestionInfo question = mQuestionMgr.GetQuestionItem(id);
		        Answer answer = mAnswerMgr.GetAnswer(id);
		        boolean bTitle = question.mReference == null || question.IsPaperTitle();
		        mCurRow++;
		        
		        //TODO:difference background color
		        //if (mCurRow % 2 == 0){}else{}
		        	
	        	holder.text.setText(question.mDetail);
		    	holder.text.setTag(id);
		    	
	        	holder.tableLayout.setVisibility(bTitle ? View.GONE : View.VISIBLE);
		        
		        if (bTitle){
		        	break;
		        }
		        
	        	mPaperStatus.onLayoutMarkButton(holder, answer);
		       
		    	holder.radioBtnA.setVisibility(question.IsOption() ? View.VISIBLE : View.GONE);
		        holder.radioBtnB.setVisibility(question.IsOption() ? View.VISIBLE : View.GONE);    	
		        holder.radioBtnC.setVisibility(question.IsOption() ? View.VISIBLE : View.GONE);
		        holder.radioBtnD.setVisibility(question.IsOption() ? View.VISIBLE : View.GONE);
		        
		        if (question.IsOption()){
		        	mPaperStatus.onLayoutOptionRadioButton(holder, answer);
		        }
	        	holder.radioBtnA.setTag(id);
	        	holder.radioBtnB.setTag(id);
	        	holder.radioBtnC.setTag(id);
	        	holder.radioBtnD.setTag(id);	        
		    	holder.mark.setTag(id);

	        }while (false);	    	
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//按下键盘上返回按钮
		if(keyCode == KeyEvent.KEYCODE_BACK ){
			new AlertDialog.Builder(this)
				.setTitle(R.string.ExitPromptTitle)
				.setMessage(R.string.ExitPromptMsg)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_HOME){
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}	

	public void CommitAnswers(){
		SocketService.SendMessage(new AnswerMessage(mAnswerMgr.toString()), 0);
	}
	
	public void SwitchPaperStatus(){
		CommitAnswers();
		mPaperStatus = null;
		mPaperStatus = new CommitedStatus(this);
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
	 
	public void FetchUndoneList(ArrayList<Integer> undonelist){
    	for (Integer id : mQuestionMgr.GetIDs()){
    		if (!mQuestionMgr.GetQuestionItem(id).IsPaperTitle()){
    			undonelist.add(id);
    		}
    	}
    	mMultiBlankRefAnswer.clear();
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
    	mMultiBlankAnswer.clear(); 
	}
	
	public void ShowAnswerContent(Integer id){
		Parcel answerContent = mAnswerMgr.GetAnswer(id).GetAnswer();
		Integer cnt = answerContent.readInt();

		mMultiBlankAnswer.clear();
		for (int i = 0; i < cnt; ++i){
			Integer subid = answerContent.readInt();
			String answer = answerContent.readString();
			mMultiBlankAnswer.put(subid, answer);
		}

		ChangeCurrentBlankParam(mMultiBlankAnswer.get(1), 1);		
	}
	
	public void ShowReferenceContent(Integer id){
		Parcel refContent = mAnswerMgr.GetAnswer(id).GetRefAnswer();
		Integer cnt = refContent.readInt();

		mMultiBlankRefAnswer.clear();
		for (int i = 0; i < cnt; ++i){
			Integer subid = refContent.readInt();
			String answer = refContent.readString();
			mMultiBlankRefAnswer.put(subid, answer);
		}

		((TextView)findViewById(R.id.txtReference)).setText(mMultiBlankRefAnswer.get(1));

		findViewById(R.id.txtOption).setEnabled(false);
	}
	
	public void ChangeFilterButtonText(int resID){
		((Button)findViewById(R.id.btnFilter)).setText(resID);
	}
	
	public void ChangeCurrentBlankParam(String answer, Integer subid){
		Integer id = (Integer)findViewById(R.id.txtOption).getTag();
		String curTitle = String.format(getResources().getString(R.string.CurrentBlankQuestion), id, subid);
		((TextView)findViewById(R.id.txtCurrent)).setText(curTitle);
		if (answer != null){
			((EditText)findViewById(R.id.txtOption)).setText(answer);
		}else{
			((EditText)findViewById(R.id.txtOption)).setText("");
		}
	}
	
	public void ChangeCurrentBlankParam(String answer){
		Integer id = (Integer)findViewById(R.id.txtOption).getTag();
		String curTitle = String.format(getResources().getString(R.string.CurrentQuestion), id);		
		((TextView)findViewById(R.id.txtCurrent)).setText(curTitle);
		if (answer != null){
			((EditText)findViewById(R.id.txtOption)).setText(answer);
		}else{
			((EditText)findViewById(R.id.txtOption)).setText("");
		}
	}	
}

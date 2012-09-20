package com.king.cai.examination;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Html;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.king.cai.KingCAIConfig;
import com.king.cai.R;
import com.king.cai.WorkspaceActivity;
import com.king.cai.common.ComunicableActivity;
import com.king.cai.examination.PaperViewAdapter;
import com.king.cai.message.RequestMessage_Answer;
import com.king.cai.message.RequestMessage_Image;
import com.king.cai.message.RequestMessage_ImageData;
import com.king.cai.message.RequestMessage_LastAnswer;
import com.king.cai.message.RequestMessage_Logout;
import com.king.cai.message.RequestMessage_Paper;
import com.king.cai.message.RequestMessage_PaperSize;
import com.king.cai.message.RequestMessage_ResetPassword;

public class PaperActivity  extends ComunicableActivity {
	private final static int GROUP_NORMAL = 0;
	private final static int MENU_FONT_SIZE = 0;
	private final static int MENU_MODIFY_PASSWORD = 1;	
	private final static int MENU_WIFI_MANAGER = 2;
	private final static int MENU_ABOUT = 3;
	
	private final static int SET_FONT_SIZE_DIALOG = 0;
	private final static int DIALOG_ABOUT = 2;
	private final static int DIALOG_PROGRESS_LOAD_PAPER = 3;
	
	private int mCurrentFontSize = 1;//normal
		
	private final static String s_ConfigFileName = "KingCAI_Config";
	private final static String s_CfgTag_FontSize = "fontsize";
	private final static String s_CfgTag_ExitStatus = "exitstatus"; //true:normal false:exception
	
	private int mSysBacklightTimeout = 0;
	public TextView mTextViewTitle = null;
	private ListView mListView = null;
	private Button mBtnCommit = null;
	private Button mBtnFilter = null;
	private TextView mTextViewStatus = null;
	private boolean mOffline = false;
	private boolean mExceptionExit = false;
	private ProgressDialog mProgressDialog = null;
	private String mStudentID = null;
	private String mStudentInfo = null;
	private String mServerIP = null;
	private String mSSID = null;
	
	private String mQuestionCount = null;
	
	private PaperViewAdapter mFullAdapter = null;
	private PaperStatus mPaperStatus = null;	
	
	private QuestionManager mQuestionMgr = new QuestionManager();;
	private AnswerManager mAnswerMgr = null;	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  
        
		mTextViewTitle = (TextView)findViewById(R.id.textViewTitle);        
		ParseIntentExtraParam();
		
		mAnswerMgr = new AnswerManager(mQuestionMgr);
		mPaperStatus = new AnswerStatus(this,  mQuestionMgr);
		
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
		
		mTextViewStatus = (TextView)findViewById(R.id.textViewPaperStatus);
		mTextViewStatus.setVisibility(View.GONE);
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
    public void onStart(){
    	super.onStart();
    	setBacklightNoneOff();
   		GetConfig();
   		ResetAdapterFontSize(mFullAdapter);
    }

    protected void emitSimulatorEvent(int event){
    	emitSimulatorEventDelayed(event, 0);
    }
    
    protected void emitSimulatorEventDelayed(int event, int delayMillis){
    	mInnerMessageHandler.sendMessageDelayed(mInnerMessageHandler.obtainMessage(event), delayMillis);
    }
    
    @Override
    public void onStop(){
    	setScreenOffTime(mSysBacklightTimeout);
    	SaveConfig(true);
    	mServiceChannel.sendMessage(new RequestMessage_Logout(), 0);
    	if (mPaperStatus != null){
    		mPaperStatus.LeaveStatus();
        	mPaperStatus = null;
    	}
   		super.onStop();
    }    

	private void  setBacklightNoneOff(){    
		try{  
			mSysBacklightTimeout = Settings.System.getInt(getContentResolver(), 
		  		  					Settings.System.SCREEN_OFF_TIMEOUT);
			setScreenOffTime(3*60*60*1000);
		}  
		catch (Exception localException){        
		}  
	}  

	private void setScreenOffTime(int backlightTimeout){  
		try{  
		    Settings.System.putInt(getContentResolver(), 
			    					Settings.System.SCREEN_OFF_TIMEOUT, backlightTimeout);  
		}catch (Exception localException){  
		    localException.printStackTrace();  
		}  
	}
   
    @Override
	protected void onServiceReady(){
        mServiceChannel.sendMessage(new RequestMessage_PaperSize(), 0);		
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
			mQuestionMgr.importQuestionsFromLocalFile();
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
		mQuestionMgr.exportQuestionsToDB(getApplicationContext(), mAnswerMgr);
	}
	
	private void ReadPaper(){
		mQuestionMgr.importQuestionsFromDB(getApplicationContext());
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
        getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);  
        super.onAttachedToWindow();  
    }
    
    public class SearchEditorListener implements TextView.OnEditorActionListener{

		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED 
					|| actionId == EditorInfo.IME_ACTION_DONE){
				if (v.getText().toString().length() != 0){
					String rawGotoText = v.getText().toString();
					Integer gotoItem = Integer.parseInt(rawGotoText);
					Integer offset = mQuestionMgr.getUnQuestionCount(gotoItem);
					
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
//    	menu.add(GROUP_NORMAL, MENU_WIFI_MANAGER, 0, R.string.ManagerWifi);
    	menu.add(GROUP_NORMAL, MENU_MODIFY_PASSWORD, 0, R.string.ModifyPassword);
    	menu.add(GROUP_NORMAL, MENU_ABOUT, 0, R.string.About);
    	
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
    	case MENU_FONT_SIZE:
    		showDialog(SET_FONT_SIZE_DIALOG);
    		break;
    	case MENU_MODIFY_PASSWORD:
    		showChangePasswordDialog();
    		break;
    	case MENU_WIFI_MANAGER:
    		break;
    	case MENU_ABOUT:
    		showDialog(DIALOG_ABOUT);
    		break;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }

    private void showChangePasswordDialog(){
		LayoutInflater inflater = LayoutInflater.from(getApplication());
		final View modifyView = inflater.inflate(R.layout.modifypassword, null);
		final Dialog dlg = new Dialog(this, R.style.NoTitleDialog);
		dlg.setContentView(modifyView);
		((TextView)modifyView.findViewById(R.id.textViewTitle)).setText(R.string.PasswordTitle);
		Button buttonOK = (Button)modifyView.findViewById(R.id.buttonOK);
		buttonOK.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String pwdFirst = ((EditText)modifyView.findViewById(R.id.editTextFirstPassword)).getText().toString();
				String pwdSecond = ((EditText)modifyView.findViewById(R.id.editTextSecond)).getText().toString();
				String pwdOld = ((EditText)modifyView.findViewById(R.id.editTextOldPassword)).getText().toString();

				if (pwdFirst != null && pwdFirst.equals(pwdSecond)){
					if (!pwdFirst.equals(pwdOld)){
						mServiceChannel.sendMessage(new RequestMessage_ResetPassword(mStudentID, pwdOld, pwdFirst), 0);
						hiddenKeyboard((EditText)modifyView.findViewById(R.id.editTextFirstPassword));
						dlg.dismiss();
					}else{
						showToast(R.string.ErrOldNewSame);
					}
				}else{
					showToast(R.string.ErrNotSame);
				}
			}
		});
		Button buttonCancel = (Button)modifyView.findViewById(R.id.buttonCancel);    		
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		
		dlg.show();
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
    	}else if (id == DIALOG_PROGRESS_LOAD_PAPER){
    		mProgressDialog = null;
    		mProgressDialog  = new ProgressDialog(this);
    		mProgressDialog.setMessage(getResources().getString(R.string.ProgressTipPaper));    		
    		mProgressDialog.setIndeterminate(true);
    		mProgressDialog.setCancelable(false);
            return mProgressDialog;    		
    	}
    	
    	return super.onCreateDialog(id);
    } 
    
    private String GenAboutString(){
        List<PackageInfo> pkgInfos = getPackageManager().getInstalledPackages(0/*PackageManager.GET_ACTIVITIES*/);
        PackageInfo kingPackage = null;
        String packageName =getPackageName(); 
        for(PackageInfo info : pkgInfos) {
        	if (packageName.equals(info.packageName)){
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
        				//new Date(new File("PaperActivity.java").lastModified()).toLocaleString());
        }

    	return about;
    }

	@Override
	protected void doHandleInnerMessage(Message innerMessage){
		Bundle bundle = innerMessage.getData();
		switch (innerMessage.what){
	
		case KingCAIConfig.EVENT_PAPER_READY:{
			Integer size = bundle.getInt("Size");
			mQuestionCount = bundle.getString("Count");
			
			mServiceChannel.updatePaperSize(size);
			mServiceChannel.sendMessage(new RequestMessage_Paper(), 0);
			mTextViewStatus.setText("Paper Size Ready:"+size+"\n");
			Integer count = 0;
			try {
				count = Integer.parseInt(mQuestionCount);
			}catch (NumberFormatException e){
				e.printStackTrace();
			}finally{
				mTimeoutHandler.sendMessageDelayed(mTimeoutHandler.obtainMessage(EVENT_PAPER_RECEIVE_TIMEOUT), count * 4 * 1000);
			}
			showDialog(DIALOG_PROGRESS_LOAD_PAPER);
			break;
		}
		case KingCAIConfig.EVENT_NEW_PAPER:{
			String paper = bundle.getString("Paper");
			mTextViewStatus.append("new Paper:"+paper+"\n");
			break;
		}
		case KingCAIConfig.EVENT_NEW_QUESTION:{
			String id = bundle.getString("ID");
			Integer type = bundle.getInt("Type");
			String answer = bundle.getString("Reference");
			String content = bundle.getString("Content");
			Integer imageCount = bundle.getInt("ImageCount");
			mQuestionMgr.addQuestion(id, type, answer, content, imageCount);
			for (int i = 1; i < imageCount + 1; ++i){
				DownloadManager.getInstance().addTask(id, String.valueOf(i), mInnerMessageHandler);	
			}
			mServiceChannel.updatePaperSize(0);
			
			if (mProgressDialog != null && mQuestionCount != null){ 
				String tips = String.format(getResources().getString(R.string.ProgressTipQuestion),
						mQuestionCount);
				mProgressDialog.setMessage(tips);
			}
			break;
		}
		case KingCAIConfig.EVENT_NEW_QUESTION_COMPLETE:{
			if (mExceptionExit){
				mServiceChannel.sendMessage(new RequestMessage_LastAnswer(), 0);
			}else{
				DownloadManager.getInstance().dispatchTask();
				
				if (DownloadManager.getInstance().getCurrentTask() == null){
					postReceivePaper();
				}				
			}
			
			break;
		}
		case KingCAIConfig.EVENT_LAST_ANSWER_COMPLETE:{
			String lastAnswers = bundle.getString("LastAnswer");
			if (mAnswerMgr != null){
				mAnswerMgr.fromString(lastAnswers);
			}

			DownloadManager.getInstance().dispatchTask();
			if (DownloadManager.getInstance().getCurrentTask() == null){
				postReceivePaper();
			}			
			break;
		}
		case KingCAIConfig.EVENT_REQUEST_IMAGE:{
			String qid = bundle.getString("ID");
			String imageIndex = bundle.getString("Index");
			mServiceChannel.sendMessage(new RequestMessage_Image(qid, imageIndex), 0);
			break;
		}		
		case KingCAIConfig.EVENT_IMAGE_READY:{
			String qid = bundle.getString("ID");
			String imageIndex = bundle.getString("Index");
			Integer size = bundle.getInt("Size");
			mServiceChannel.updateDownloadInfo(size);
			mServiceChannel.sendMessage(new RequestMessage_ImageData(qid, imageIndex), 0);
			break;
		}	
		case KingCAIConfig.EVENT_NEW_IMAGE:{
			if (DownloadManager.getInstance().getCurrentTask() != null){
				String id = DownloadManager.getInstance().getCurrentTask().getQuestionID();
				String imageIndex = DownloadManager.getInstance().getCurrentTask().getImageIndex();
				byte[] data = bundle.getByteArray("Content");
				mQuestionMgr.addQuestionImage(id, imageIndex, ByteBuffer.wrap(data));
				
				mServiceChannel.updateDownloadInfo(0);
				DownloadManager.getInstance().finishCurrentTask();
			}
			if (DownloadManager.getInstance().getCurrentTask() == null){
				postReceivePaper();
			}
			break;
		}
		case KingCAIConfig.EVENT_RESET_PWDACK:{
			boolean result = bundle.getBoolean("Result");
			showToast(result ? R.string.PwdOK : R.string.PwdFail);
			break;
		}
		case KingCAIConfig.EVENT_COMMIT_ACK:{
			boolean ack = bundle.getBoolean("Result");
			switch2CommitStatus(ack);
			break;
		}
		case KingCAIConfig.EVENT_TEST_TIMEOUT:{
			String lastTime = bundle.getString("LastTime");
			String tips = String.format(getResources().getString(R.string.EnforcedCommitTips), 
								lastTime);
			showToast(tips);
			int iLastTime = Integer.valueOf(lastTime);
			mTimeoutHandler.sendMessageDelayed(mTimeoutHandler.obtainMessage(EVENT_TEST_TIMEOUT), 
									iLastTime * 60 * 1000);
			break;
		}
		case KingCAIConfig.EVENT_AUTOSAVE_ACK:{
			mTimeoutHandler.removeMessages(EVENT_AUTOSAVEACK_TIMEOUT);
			break;
		}
		default:
			break;
		}
 	}
	
	private void postReceivePaper(){
		if (mProgressDialog != null){
			mTimeoutHandler.removeMessages(EVENT_PAPER_RECEIVE_TIMEOUT);
			mProgressDialog.dismiss();
			mPaperStatus.EnterStatus();
		}
	}
	
	public static final int EVENT_TEST_TIMEOUT = 0;
	public static final int EVENT_AUTOSAVEACK_TIMEOUT = 1;
	public static final int EVENT_PAPER_RECEIVE_TIMEOUT = 2;
	public static final int AUTOSAVE_ACK_TIME = 20 * 1000;
	private Handler mTimeoutHandler = new Handler(){

		@Override
		public void handleMessage(Message msg){
			switch (msg.what){
			case EVENT_TEST_TIMEOUT:{
				CommitAnswers(RequestMessage_Answer.s_NormalCommitMsgTag);
				break;
			}
			case EVENT_AUTOSAVEACK_TIMEOUT:{
				showPCCrashNotifyDialog();
				break;
			}
			case EVENT_PAPER_RECEIVE_TIMEOUT:{
				if (mProgressDialog != null){
					mProgressDialog.dismiss();					
				}
				int loadedCount = mQuestionMgr != null ? mQuestionMgr.getQuestionCount() : 0;
				String tips = String.format(getResources().getString(R.string.DownloadInfo), 
						mQuestionCount, loadedCount);
	    		new AlertDialog.Builder(PaperActivity.this)
		    			.setTitle(R.string.DownloadComplete)
		    			.setMessage(tips)
		    			.setNegativeButton(android.R.string.ok, null)
		    			.show();
				mPaperStatus.EnterStatus();    	
				break;
			}
			default:
				break;
			}

		}
	};

    private void showPCCrashNotifyDialog(){
		LayoutInflater inflater = LayoutInflater.from(getApplication());
		final View crashView = inflater.inflate(R.layout.crash, null);
		final Dialog dlg = new Dialog(this, R.style.NoTitleDialog);
		dlg.setContentView(crashView);
		Button buttonOK = (Button)crashView.findViewById(R.id.buttonCrash);
		buttonOK.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				CheckBox ckbLogin = (CheckBox)crashView.findViewById(R.id.checkBoxRelogin);
				if (ckbLogin != null && ckbLogin.isChecked()){
					startWorkspaceActivity(CAUSE_RELOGIN);
				}
				
				dlg.dismiss();
			}
		});
		
		dlg.show();
	}
	
    public static final String CAUSE_NORMAL = "Normal";
    public static final String CAUSE_RELOGIN = "Relogin";
    private void startWorkspaceActivity(String cause){
    	Intent intent = new Intent(this, WorkspaceActivity.class);
    	intent.putExtra("Cause", cause);
    	startActivity(intent);
    }
	
    private void ParseIntentExtraParam(){
    	Bundle extra = getIntent().getExtras();
    	if (getIntent().hasExtra(KingCAIConfig.StudentID)){
    		mStudentID = extra.getString(KingCAIConfig.StudentID);
    	}
    	
        if (getIntent().hasExtra(KingCAIConfig.StudentInfo)){
            mStudentInfo = extra.getString(KingCAIConfig.StudentInfo);
            mTextViewTitle.setText(mStudentInfo.trim() + "  - " + getTitle());
        }
        
        if (getIntent().hasExtra(KingCAIConfig.ServerIP)){
            String servip = extra.getString(KingCAIConfig.ServerIP);
            mServerIP = new String(servip != null ? servip : "");
        }
        
        if (getIntent().hasExtra(KingCAIConfig.SSID)){
            String ssid = extra.getString(KingCAIConfig.SSID);
            mSSID = new String(ssid != null ? ssid : "");
        }
        
        if (getIntent().hasExtra(KingCAIConfig.Offline)){
        	mOffline = extra.getBoolean(KingCAIConfig.Offline);
        }
        
        if (getIntent().hasExtra(KingCAIConfig.ExceptionExit)){
        	mExceptionExit = extra.getBoolean(KingCAIConfig.ExceptionExit);
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
			((EditText)findViewById(R.id.txtGoto)).requestFocus();//使得试卷界面的其余文本框失去焦点，从而保存答案到内存中
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
		if(keyCode == KeyEvent.KEYCODE_BACK ){
			((EditText)findViewById(R.id.txtGoto)).requestFocus();
			return mPaperStatus != null ? mPaperStatus.onBackkeyDown() : false;
		}else if (keyCode == KeyEvent.KEYCODE_HOME){
			showToast("you should commit the paper first then exit");
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}	

	public void CommitAnswers(String tag){
		HiddenKeyBoard(findViewById(R.id.txtGoto));
		mServiceChannel.sendMessage(new RequestMessage_Answer(tag, mAnswerMgr.toString()), 0);
		if (tag == RequestMessage_Answer.s_ExceptionCommitMsgTag){
			mTimeoutHandler.sendMessageDelayed(mTimeoutHandler.obtainMessage(EVENT_AUTOSAVEACK_TIMEOUT), AUTOSAVE_ACK_TIME);
		}
		//写入
		SharedPreferences sp = getSharedPreferences(KingCAIConfig.s_ExtraInfoFileName, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(KingCAIConfig.LastLoginID, mStudentID);
		editor.putString(KingCAIConfig.LastLoginName, mStudentInfo);
		editor.putString(KingCAIConfig.Grade, mSSID);
		editor.putBoolean(KingCAIConfig.ExtraStudentInfo, true);
		editor.commit();
	}
	
	public void switch2WaitingStatus(){
		mPaperStatus.LeaveStatus();
		mPaperStatus = null;
		mPaperStatus = new WaitingStatus(this);
		mPaperStatus.EnterStatus();
		PaperViewAdapter adapter = (PaperViewAdapter)mListView.getAdapter();
		adapter.notifyDataSetChanged();
	}
	
	public void switch2CommitStatus(boolean showAnswer){
		mPaperStatus.LeaveStatus();
		mPaperStatus = null;
		mPaperStatus = new CommitedStatus(this, showAnswer);
		mPaperStatus.EnterStatus();		
	}
	
	public void ShowDoneInfo(int undoneCount){
		int cnt = mQuestionMgr.getQuestionCount();
		((TextView)findViewById(R.id.txtAnswerInfo)).setText(cnt + "/" + (cnt - undoneCount));
	}

	public void ShowCorrectInfo(int correctCount){
		int uncorrect = mQuestionMgr.getQuestionCount() - correctCount;
		String info = String.format("<font color=\"#8ACE1A\">%s</font>/<font color=\"#ff0000\">%s</font>", correctCount, uncorrect);
		((TextView)findViewById(R.id.txtAnswerInfo)).setText(Html.fromHtml(info));

		mListView.setAdapter(mFullAdapter);
		ChangeFilterButtonText(R.string.AllQuestions);
	}	
	 	
	public void InitUncorrectList(ArrayList<Integer> correctList, 
									ArrayList<Integer> uncorrectList){
		Answer aAnswer = null;
    	for (Integer index : mQuestionMgr.getIndexes()){
    		aAnswer = mAnswerMgr.getAnswer(mQuestionMgr.getIdByIndex(index));
    		if (aAnswer != null && aAnswer.isCorrect()){
    			correctList.add(index);
    		}else if (aAnswer != null && !aAnswer.isCorrect()){
    			uncorrectList.add(index);
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

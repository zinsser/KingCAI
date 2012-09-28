package com.king.cai;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


public class DisconnectedStatus extends WorkspaceStatus{
	
	private final static int EVENT_QUERY_TIME_OUT = 1;
	private final static int EVENT_LOGIN_TIME_OUT = 2;
	private final static int EVENT_REMOVE_WAITING_DIALOG = 3;
	private final static int DELAY_QUERY_TIME = 10 * 1000;
	private final static int DELAY_LOGIN_TIME = 10 * 1000;	
	private final static int DELAY_REMOVE_DIALOG = 3 * 1000;
	
	private EditText mTextviewStudentID = null;
	private EditText mTextviewPassword = null;
	
	protected DisconnectedStatus(WorkspaceActivity statusOwner) {
		super(statusOwner);
	}

	@Override
	public void enterStatus() {
		showStatusPanel();
		mTextviewStudentID = (EditText)findViewById(R.id.textViewIDOnWorkspace);
		mTextviewPassword = (EditText)findViewById(R.id.textViewPasswordOnWorkspace);

		findViewById(R.id.buttonLoginOnWorkspace).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String password = mTextviewPassword.getText().toString();
				if (isDebugPassword(password)){
					
				}else{
					String id = mTextviewStudentID.getText().toString();
					if (id == null || id.length() <= 0){
						mStatusOwner.showToast(R.string.ErrNotInputID);
						mTextviewStudentID.requestFocus();
					}else{
						boolean bOffline = ((CheckBox)findViewById(R.id.checkBoxOfflineOnWorkspace)).isChecked();						
						InputMethodManager im =(InputMethodManager)(v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)); 
						im.hideSoftInputFromWindow(mTextviewStudentID.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
						mTimeoutHandler.sendMessageDelayed(mTimeoutHandler.obtainMessage(EVENT_QUERY_TIME_OUT), DELAY_QUERY_TIME);
						mStatusOwner.queryServer(bOffline);
					}
				}
			}
		});
	}

	@Override
	public void leaveStatus() {
		mStatusOwner.dimissWaitingDialog();
		disableClickListener();
	}

	private void disableClickListener(){
		findViewById(R.id.textViewAbout).setOnClickListener(null);		
		findViewById(R.id.buttonAboutOK).setOnClickListener(null);				
	}
	
	private void showInitPanel(){
		findViewById(R.id.scrollViewSettings).setVisibility(View.GONE);
	}
	
	private void showSettingPanel(){
		findViewById(R.id.scrollViewSettings).setVisibility(View.VISIBLE);

		findViewById(R.id.linearLayoutResetPassword).setVisibility(View.GONE);
		findViewById(R.id.linearLayoutAbout).setVisibility(View.VISIBLE);
		findViewById(R.id.linearLayoutAboutDetail).setVisibility(View.GONE);
	}
	
	public void showStatusPanel(){
		findViewById(R.id.linearLayoutConnected).setVisibility(View.GONE);
		findViewById(R.id.linearLayoutDisconnected).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.textViewAboutDetail)).setText(mStatusOwner.genAboutString());
		findViewById(R.id.textViewAbout).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if (!findViewById(R.id.linearLayoutAboutDetail).isShown()){
					findViewById(R.id.linearLayoutAboutDetail).setVisibility(View.VISIBLE);
				}
			}
		});
		findViewById(R.id.buttonAboutOK).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				findViewById(R.id.linearLayoutAboutDetail).setVisibility(View.GONE);
			}
		});
		
		showInitPanel();
	}	
	
	private boolean isDebugPassword(String password){
		boolean bRet = false;
		if (password != null){
			bRet = password.equals("*#debug#");
		}
		return bRet;
	}
    
    public Handler mTimeoutHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
    		case EVENT_QUERY_TIME_OUT:
    			mStatusOwner.updateWaitingDialogTips(R.string.FailQueryServerStatus);
    			mTimeoutHandler.sendMessageDelayed(mTimeoutHandler.obtainMessage(), DELAY_REMOVE_DIALOG);
    			break;
    		case EVENT_LOGIN_TIME_OUT:
    			mStatusOwner.updateWaitingDialogTips(R.string.LoginTimeOut);
    			mTimeoutHandler.sendMessageDelayed(mTimeoutHandler.obtainMessage(), DELAY_REMOVE_DIALOG);	
    			break;
    		case EVENT_REMOVE_WAITING_DIALOG:
    			mStatusOwner.dimissWaitingDialog();
    			break;
    		}
    	}
    };	
	
	@Override
	public void doHandleInnerMessage(Message innerMessage) {
		Bundle bundle = innerMessage.getData();    	
    	switch (innerMessage.what){
		case KingCAIConfig.EVENT_QUERY_COMPLETE:
			mTimeoutHandler.removeMessages(EVENT_QUERY_TIME_OUT);
			String mServerIP = bundle.getString("Peer");
			String mSSID = "һ�꼶";//(String)mSpinnerSSID.getAdapter().getItem(mSpinnerSSID.getSelectedItemPosition());
			mStatusOwner.updateWaitingDialogTips(R.string.FoundServerStatus);
			mStatusOwner.updateServerInfo(mServerIP, mSSID);
			if (!mStatusOwner.isAutoLogin()){
				mStatusOwner.loginToServer(mTextviewStudentID.getText().toString(), 
 									   mTextviewPassword.getText().toString());
			}else{
				mStatusOwner.loginToServer();
			}
			mTimeoutHandler.sendMessageDelayed(mTimeoutHandler.obtainMessage(EVENT_LOGIN_TIME_OUT), DELAY_LOGIN_TIME);
			break;
		case KingCAIConfig.EVENT_LOGIN_COMPLETE:
			mTimeoutHandler.removeMessages(EVENT_LOGIN_TIME_OUT);
			Boolean bResult = bundle.getBoolean("Result");
			mStatusOwner.dimissWaitingDialog();
			if (bResult){
				String errCause = bundle.getString("ErrCause");
				String studentInfo = bundle.getString("Info");

				mStatusOwner.onLoginSuccess(studentInfo, errCause.equals("[autocommit]"));
			}else{
				String errCause = bundle.getString("ErrCause");
				if (errCause != null){
					if (errCause.contains("[id]")){
						mStatusOwner.showToast(R.string.FailLoginErrID);					
					}else if (errCause.contains("[pwd]")){
						mStatusOwner.showToast(R.string.FailLoginErrPwd);
					}else if (errCause.contains("[normalcommit]")){
						mStatusOwner.showToast(R.string.FailLoginErrReenter);
					}else if (errCause.contains("[id_exist]")){
						mStatusOwner.showToast(R.string.FailLoginErrDoubleEnter);
					}
				}
				clearPanel();
			}			
			break;
    	}		
	}

	private void clearPanel(){
		mTextviewPassword.setText("");
		mTextviewStudentID.setText("");
	}

	@Override
	public void onBookmarkClick() {
		mStatusOwner.showToast(R.string.NotLogin);
	}

	@Override
	public void onPaperClick() {
		mStatusOwner.showToast(R.string.NotLogin);		
	}

	@Override
	public void onWrongQuestionsClick() {
		mStatusOwner.showToast(R.string.NotLogin);		
	}

	@Override
	public void onAppmenuClick() {
		mStatusOwner.showToast(R.string.NotLogin);		
	}

	@Override
	public void onSettingsClick() {
		showSettingPanel();
	}
}

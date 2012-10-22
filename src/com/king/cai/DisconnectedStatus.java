package com.king.cai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.king.cai.common.KingCAIUtils;
import com.king.cai.examination.DownloadManager;
import com.king.cai.message.RequestMessage_UpdateApk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
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
    			mTimeoutHandler.sendMessageDelayed(mTimeoutHandler.obtainMessage(EVENT_REMOVE_WAITING_DIALOG), DELAY_REMOVE_DIALOG);
    			break;
    		case EVENT_LOGIN_TIME_OUT:
    			mStatusOwner.updateWaitingDialogTips(R.string.LoginTimeOut);
    			mTimeoutHandler.sendMessageDelayed(mTimeoutHandler.obtainMessage(EVENT_REMOVE_WAITING_DIALOG), DELAY_REMOVE_DIALOG);	
    			break;
    		case EVENT_REMOVE_WAITING_DIALOG:
    			mStatusOwner.dimissWaitingDialog();
    			break;
    		}
    	}
    };	
	
    private boolean isNeedUpdataApp(String serverVersion){
    	PackageInfo kingPackage = mStatusOwner.getKingPackageInfo();
    	boolean needUpdate = false;
    	
    	if (!serverVersion.equals(kingPackage.versionName+"."+kingPackage.versionCode)){
    		needUpdate = true;
    	}
    	
    	return needUpdate;
    } 
    
    private void login2Server(String serverAddr){
		if (!mStatusOwner.isAutoLogin()){
			mStatusOwner.loginToServer(mTextviewStudentID.getText().toString(), 
									   mTextviewPassword.getText().toString());
			clearPanel();
		}else{
			mStatusOwner.loginToServer();
		}
		mTimeoutHandler.sendMessageDelayed(mTimeoutHandler.obtainMessage(EVENT_LOGIN_TIME_OUT), DELAY_LOGIN_TIME);    	
    }
    
    
    private void promptUpdateApp(final String size){
    	mStatusOwner.dimissWaitingDialog();
    	new AlertDialog.Builder(mStatusOwner)
    		.setTitle("软件更新")
    		.setMessage("终端软件和教师服务器软件版本不匹配，需要更新软件，确定现在下载更新软件吗？")
    		.setNegativeButton("现在下载", new OnClickListener(){

				public void onClick(DialogInterface arg0, int arg1) {
					mStatusOwner.requestUpdateApk(size);
				}
    		})
    		.setPositiveButton("以后再说", null)
    		.create()
    		.show();
    }
    
	@Override
	public void doHandleInnerMessage(Message innerMessage) {
		Bundle bundle = innerMessage.getData();    	
    	switch (innerMessage.what){
		case KingCAIConfig.EVENT_QUERY_COMPLETE:
			mTimeoutHandler.removeMessages(EVENT_QUERY_TIME_OUT);
			String serverAddr = bundle.getString("Peer");
			String version = bundle.getString("Version");
			String size = bundle.getString("Size");
			String mSSID = "一年级";//(String)mSpinnerSSID.getAdapter().getItem(mSpinnerSSID.getSelectedItemPosition());
			mStatusOwner.updateWaitingDialogTips(R.string.FoundServerStatus);
			mStatusOwner.updateServerInfo(serverAddr, mSSID);			
			if (!isNeedUpdataApp(version)){
				login2Server(serverAddr);
			}else{
				promptUpdateApp(size);
			}
			break;
		case KingCAIConfig.EVENT_LOGIN_COMPLETE:{
			mTimeoutHandler.removeMessages(EVENT_LOGIN_TIME_OUT);
			Boolean bResult = bundle.getBoolean("Result");
			mStatusOwner.dimissWaitingDialog();
			if (bResult){
				String errCause = bundle.getString("ErrCause");
				String studentInfo = bundle.getString("Info");
				String photoSize = bundle.getString("PhotoSize");
				mStatusOwner.onLoginSuccess(studentInfo, photoSize, errCause.equals("[autocommit]"));
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
		case KingCAIConfig.EVENT_NEW_APK:{
			byte[] datas = bundle.getByteArray("Content");
			String apkName = "KingCAI_updated.apk";
			try {
				File file = new File(KingCAIUtils.getRootPath(), apkName);
				FileOutputStream outStream = new FileOutputStream(file);
				outStream.write(datas);
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mStatusOwner.showToast("成功接收:" + apkName);
			break;
		}
    	}    	
	}

	/**
	 *  
     * 安装apk
     * @param url
		private void installApk(){
			File apkfile = new File(saveFileName);
			if (!apkfile.exists()) {
			    return;
			}    
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
			mContext.startActivity(i);
		}
	 * */
	
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

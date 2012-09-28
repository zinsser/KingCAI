package com.king.cai;


import com.king.cai.examination.PaperActivity;
import com.king.cai.service.KingService.LoginInfo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ConnectedStatus extends WorkspaceStatus {
	private LoginInfo mLoginInfo = null;
	protected ConnectedStatus(WorkspaceActivity statusOwner, LoginInfo info) {
		super(statusOwner);
		mLoginInfo = info;
	}

	@Override
	public void enterStatus() {
		showStatusPanel();
        
        findViewById(R.id.buttonLogoutOnWorkspace).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				onLogout();
			}
		});
	}

	@Override
	public void leaveStatus() {        
		disableClickListener();
	}

	private void disableClickListener(){
		findViewById(R.id.textViewResetPasword).setOnClickListener(null);
		findViewById(R.id.buttonResetOK).setOnClickListener(null);
		findViewById(R.id.buttonResetCancel).setOnClickListener(null);
		findViewById(R.id.textViewAbout).setOnClickListener(null);
		findViewById(R.id.buttonAboutOK).setOnClickListener(null);
	}
	
	private void showInitPanel(){
		findViewById(R.id.scrollViewSettings).setVisibility(View.GONE);
	}
	
	private void showSettingPanel(){
		findViewById(R.id.scrollViewSettings).setVisibility(View.VISIBLE);

		findViewById(R.id.linearLayoutResetPassword).setVisibility(View.VISIBLE);
		findViewById(R.id.linearLayoutAbout).setVisibility(View.VISIBLE);
		findViewById(R.id.linearLayoutResetPasswordDialog).setVisibility(View.GONE);
		findViewById(R.id.linearLayoutAboutDetail).setVisibility(View.GONE);
	}
	
	
	private void showStatusPanel(){		
		findViewById(R.id.linearLayoutConnected).setVisibility(View.VISIBLE);
		findViewById(R.id.linearLayoutDisconnected).setVisibility(View.GONE);
		
	    ((TextView)findViewById(R.id.textViewStudentBaseInfo)).setText(mLoginInfo.mInfo);
	    ((TextView)findViewById(R.id.textViewStudentAdvancedInfo)).setText(R.string.ExtraStudentInfo);
	    
		((TextView)findViewById(R.id.textViewAboutDetail)).setText(mStatusOwner.genAboutString());	    
		findViewById(R.id.textViewResetPasword).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if (!findViewById(R.id.linearLayoutResetPasswordDialog).isShown()){
					findViewById(R.id.linearLayoutResetPasswordDialog).setVisibility(View.VISIBLE);
				}
			}
		});
		
		findViewById(R.id.buttonResetOK).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {

				String pwdFirst = ((EditText)findViewById(R.id.editTextFirstPasswordOnWorkspace)).getText().toString();
				String pwdSecond = ((EditText)findViewById(R.id.editTextSecondOnWorkspace)).getText().toString();
				String pwdOld = ((EditText)findViewById(R.id.editTextOldPasswordOnWorkspace)).getText().toString();

				if (pwdFirst != null && pwdFirst.equals(pwdSecond)){
					if (!pwdFirst.equals(pwdOld)){
						mStatusOwner.resetPassword(pwdOld, pwdFirst);
						findViewById(R.id.linearLayoutResetPasswordDialog).setVisibility(View.GONE);
					}else{
						mStatusOwner.showToast(R.string.ErrOldNewSame);
					}
				}else{
					mStatusOwner.showToast(R.string.ErrNotSame);
				}
			}
		});

		findViewById(R.id.buttonResetCancel).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				((EditText)findViewById(R.id.editTextFirstPasswordOnWorkspace)).setText("");
				((EditText)findViewById(R.id.editTextSecondOnWorkspace)).setText("");
				((EditText)findViewById(R.id.editTextOldPasswordOnWorkspace)).setText("");
				mStatusOwner.hiddenKeyboard((EditText)findViewById(R.id.editTextFirstPasswordOnWorkspace));
				
				findViewById(R.id.linearLayoutResetPasswordDialog).setVisibility(View.GONE);
			}
		});

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
	
	private void startPaperActivity(){
		if (mLoginInfo != null){
			Intent openPaperActivity = new Intent(mStatusOwner, PaperActivity.class);			
			openPaperActivity.putExtra(KingCAIConfig.StudentID, mLoginInfo.mID);
			openPaperActivity.putExtra(KingCAIConfig.StudentInfo, mLoginInfo.mInfo);
			openPaperActivity.putExtra(KingCAIConfig.ServerIP, mLoginInfo.mServerAddr);
			openPaperActivity.putExtra(KingCAIConfig.SSID, mLoginInfo.mSSID);
			openPaperActivity.putExtra(KingCAIConfig.Offline, mLoginInfo.mOffline);
			openPaperActivity.putExtra(KingCAIConfig.ExceptionExit, mLoginInfo.mExceptionExit);		
			startActivity(openPaperActivity);
		}	
	}

	private void onLogout(){
		mStatusOwner.logoutFromServer();
	}



	private void showNoPrivDialog(){
		AlertDialog dlg = new AlertDialog.Builder(mStatusOwner)
		.setTitle(R.string.NoPrivilegeTitle)
		.setMessage(R.string.NoPrivilege)
		.setPositiveButton(android.R.string.ok, null)
		.setCancelable(false)
		.create();
		dlg.show();		
	}

	@Override
	public void doHandleInnerMessage(Message innerMessage) {		
	}

	@Override
	public void onBookmarkClick() {
		showNoPrivDialog();
	}

	@Override
	public void onPaperClick() {
		startPaperActivity();
	}

	@Override
	public void onWrongQuestionsClick() {
		showNoPrivDialog();
	}

	@Override
	public void onAppmenuClick() {
		showNoPrivDialog();		
	}

	@Override
	public void onSettingsClick() {
		showSettingPanel();
	}	
}

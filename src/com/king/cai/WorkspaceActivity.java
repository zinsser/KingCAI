package com.king.cai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.cai.common.ComunicableActivity;
import com.king.cai.examination.DownloadManager;
import com.king.cai.message.RequestMessage;
import com.king.cai.message.RequestMessage_ExplorerDirectory;
import com.king.cai.message.RequestMessage_ExplorerFile;
import com.king.cai.message.RequestMessage_HeadPhoto;
import com.king.cai.message.RequestMessage_ResetPassword;
import com.king.cai.message.RequestMessage_UpdateApk;
import com.king.cai.service.KingService.LoginInfo;

public class WorkspaceActivity extends ComunicableActivity  {	
	public static final int DIALOG_LOGIN_PROGRESS = 0;
	
	private ProgressDialog mWaitingDialog = null;
	private List<PackageInfo> mInstalledApks = new ArrayList<PackageInfo>(); 
	private PackageManager mPackageMgr = null;
	private GridView mAppmenuView = null;
	private final ComponentName mAppDetailInfoComponent = new  ComponentName("com.android.settings", 
							"com.android.settings.applications.InstalledAppDetails");
	private final String mBuiltinLauncherPackage = "com.android.launcher";
	
	private WorkspaceStatus mWorkspaceStatus = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workspace);

		mPackageMgr = getPackageManager();
		new AppEnumTask().execute(mPackageMgr);
/*		
		mPackageMgr.setComponentEnabledSetting(mAppDetailInfoComponent, 
										PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
										PackageManager.DONT_KILL_APP);
		mPackageMgr.setApplicationEnabledSetting(mBuiltinLauncherPackage, 
										PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
										PackageManager.DONT_KILL_APP);
	*/	
		mAppmenuView = (GridView)findViewById(R.id.gridViewAppmenu);
        AppmenuAdapter adapter = new AppmenuAdapter();
        mAppmenuView.setAdapter(adapter);	

        ButtonClickListener listener = new ButtonClickListener();
        findViewById(R.id.buttonStudyOnWorkspace).setOnClickListener(listener);
        findViewById(R.id.buttonTestOnWorkspace).setOnClickListener(listener);
        findViewById(R.id.buttonAppsmenu).setOnClickListener(listener);
        findViewById(R.id.buttonBookmark).setOnClickListener(listener);        
        findViewById(R.id.buttonWrongQuestions).setOnClickListener(listener);
        findViewById(R.id.buttonSettings).setOnClickListener(listener);
        parseIntent();        
	}

	private class ButtonClickListener implements View.OnClickListener{

		public void onClick(View v) {
			switch (v.getId()){
			case R.id.buttonBookmark:
				mWorkspaceStatus.onBookmarkClick();
				break;
			case R.id.buttonStudyOnWorkspace:
				startExplorerActivity();
				break;
			case R.id.buttonTestOnWorkspace:
				mWorkspaceStatus.onPaperClick();
				break;
			case R.id.buttonWrongQuestions:
				mWorkspaceStatus.onWrongQuestionsClick();
				break;
			case R.id.buttonAppsmenu:
				mWorkspaceStatus.onAppmenuClick();
				break;
			case R.id.buttonSettings:
				mWorkspaceStatus.onSettingsClick();
				break;
			}
		}
	};
	
	private void parseIntent(){
		Bundle bundle = getIntent().getExtras();
		if (getIntent().hasExtra("Cause") && bundle != null){
			String cause = bundle.getString("Cause");
			if (cause.equals("Autologin")){
				switch2DisconnectedStatus();
			}
		}
	}

	public LoginInfo getLoginInfo(){
		return mServiceChannel.getLoginInfo();
	}
	
	public void queryServer(boolean bLocal){
		showWaitingDialog();		
		mServiceChannel.queryServer(bLocal);
	}
	
	public void updateServerInfo(String serverAddr, String activeSSID){
		mServiceChannel.updateServerInfo(serverAddr, activeSSID);
	}
	
	public void onLoginSuccess(String studentInfo, String strPhotosize, boolean bExceptionExit){
		mServiceChannel.updateLoginInfo(studentInfo, bExceptionExit);
		switch2ConnectedStatus();
		Integer photoSize = Integer.valueOf(strPhotosize); 
		mServiceChannel.updateDownloadInfo(photoSize);
		LoginInfo loginInfo = mServiceChannel.getLoginInfo();
		if (loginInfo != null){
			mServiceChannel.sendMessage(new RequestMessage_HeadPhoto(loginInfo.mID), 0);
		}
	}
	
	public void loginToServer(String number, String password){
		mServiceChannel.loginToServer(number, password);
	}
	
	public void loginToServer(){
		if (mServiceChannel.getLastLoginInfo() != null){
			mServiceChannel.loginToServer(mServiceChannel.getLastLoginInfo().mID,
										  mServiceChannel.getLastLoginInfo().mPassword);
		}
	}
	
	public void logoutFromServer(){
		mServiceChannel.logoutFromServer();
		switch2DisconnectedStatus();
	}

	public boolean isAutoLogin(){
		return mServiceChannel.getLastLoginInfo() != null;
	}
	
	public void resetPassword(String pwdOld, String pwdNew){
		if (getLoginInfo() != null){
			mServiceChannel.sendMessage(new RequestMessage_ResetPassword(getLoginInfo().mID, pwdOld, pwdNew), 0);
			hiddenKeyboard((EditText)findViewById(R.id.editTextFirstPasswordOnWorkspace));
		}
	}
	
	public void requestUpdateApk(String strSize){
		Integer size = Integer.valueOf(strSize);
		mServiceChannel.updateDownloadInfo(size);
		mServiceChannel.sendMessage(new RequestMessage_UpdateApk(), 0);		
	}
	
	public void sendMessage(RequestMessage msg){
		if (mServiceChannel != null){
			mServiceChannel.sendMessage(msg, 0);
		}
	}
	
	private void startExplorerActivity(){
		Intent openExplorerIntent = new Intent(this, ExplorerActivity.class);
		startActivity(openExplorerIntent);		
	}
	
	@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_HOME:
                    return true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_HOME:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }
		
	@Override
	protected void doHandleInnerMessage(Message innerMessage) {
		switch (innerMessage.what){
		case KingCAIConfig.EVENT_HEAD_PHOTO:{
			Bundle bundle = innerMessage.getData();
			byte[] bmpBytes = bundle.getByteArray("Content");
			Bitmap headPhoto = BitmapFactory.decodeByteArray(bmpBytes, 0, bmpBytes.length);
			ImageView headerIcon = (ImageView)findViewById(R.id.imageViewHeaderIcon);
			headerIcon.setImageBitmap(headPhoto);
			
			mServiceChannel.updateDownloadInfo(0);			
			break;
		}
		default:
			if (mWorkspaceStatus != null){
				mWorkspaceStatus.doHandleInnerMessage(innerMessage);
			}
			break;
		}
	}
    
	private void showWaitingDialog(){
		showDialog(DIALOG_LOGIN_PROGRESS);
	}
	
    public void updateWaitingDialogTips(int resId){
    	if (mWaitingDialog != null){
    		mWaitingDialog.setMessage(getResources().getString(resId));
    	}
    }	
    
	public void dimissWaitingDialog(){
		if (mWaitingDialog != null){
			mWaitingDialog.dismiss();
		}
	}
	
    @Override
    protected Dialog onCreateDialog(int id){
    	switch (id){
    	case DIALOG_LOGIN_PROGRESS:{
    		dimissWaitingDialog();
    		mWaitingDialog  = new ProgressDialog(this);
    		updateWaitingDialogTips(R.string.QueryServerStatus);    		
    		mWaitingDialog.setIndeterminate(true);
    		mWaitingDialog.setCancelable(false);
            return mWaitingDialog;
    	}
    	}
    	return super.onCreateDialog(id);    	
    }
    
	@Override
	protected void onServiceReady() {
		if (mServiceChannel.getLoginInfo() == null){
			if (mServiceChannel.getLastLoginInfo() == null){
				switch2DisconnectedStatus();
			}else{
				queryServer(mServiceChannel.getLastLoginInfo().mOffline);
			}
		}else{
			switch2ConnectedStatus();
		}
	}
	
	public void switch2DisconnectedStatus(){
		if (mWorkspaceStatus != null){
			mWorkspaceStatus.leaveStatus();
			mWorkspaceStatus = null;
		}
		
		mWorkspaceStatus = new DisconnectedStatus(this);
		mWorkspaceStatus.enterStatus();		
	}
	
	public void switch2ConnectedStatus(){
		if (mWorkspaceStatus != null){
			mWorkspaceStatus.leaveStatus();
			mWorkspaceStatus = null;
		}
		
		mWorkspaceStatus = new ConnectedStatus(this, getLoginInfo());
		mWorkspaceStatus.enterStatus();		
	}

	public PackageInfo getKingPackageInfo(){
        List<PackageInfo> pkgInfos = getPackageManager().getInstalledPackages(0/*PackageManager.GET_ACTIVITIES*/);
        PackageInfo kingPackage = null;
        String packageName =getPackageName();

        for(PackageInfo info : pkgInfos) {
        	if (packageName.equals(info.packageName)){
        		kingPackage = info;
        		break;
        	}
        }		
        return kingPackage;
	}
	
    public String genAboutString(){
        PackageInfo kingPackage = getKingPackageInfo();
    	String about = "";
        if (kingPackage == null){
        	about = "软件名称：\n" + getResources().getString(R.string.app_name);
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

	
	
    public class AppEnumTask extends  AsyncTask<PackageManager, PackageInfo, String> {
    	private boolean isHiddenApp(String packagename){
    		boolean bRet = false;
    		if (packagename.equals(getPackageName())){
    			bRet = true;
    		}
    		return bRet;
    	}
    	
    	@Override
    	protected String doInBackground(PackageManager... params) {
    		PackageManager pm = params[0];
    		List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);

    		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
    		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    		for (PackageInfo info : packages){
    			if (info.applicationInfo.packageName != null 
    					&& !isHiddenApp(info.applicationInfo.packageName)){
    				mainIntent.setPackage(info.applicationInfo.packageName);
    				List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
    				for (ResolveInfo app : apps){
    					if (info.applicationInfo.packageName.equals(app.activityInfo.applicationInfo.packageName)){
    						publishProgress(info);
    					}
    				}
    			}
    		} 
    		
    		return null;
    	} 
		@Override  
		protected void onPostExecute(String result) {
			((BaseAdapter)mAppmenuView.getAdapter()).notifyDataSetChanged();
		}  
		
		@Override  
		protected void onPreExecute() {  
		}  
		
		@Override  
		protected void onProgressUpdate(PackageInfo... values) {  
			if (!mInstalledApks.contains(values[0])){
				mInstalledApks.add(values[0]);
			}
		}
    };	
	public View.OnClickListener mAppItemClickListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			PackageInfo info = (PackageInfo)v.getTag();
			if (info != null && info.activities.length > 0){
				Intent intent = new Intent(Intent.ACTION_MAIN, null);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				String className = info.activities[0].name;
				ComponentName cn = new ComponentName(info.applicationInfo.packageName, className);
				intent.setComponent(cn);
				startActivity(intent);
			}else if (info != null){
				try{
					Intent intent = new Intent(info.applicationInfo.packageName);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);				
					startActivity(intent);
				}catch (ActivityNotFoundException e){
					e.printStackTrace();
					showToast("该应用已被卸载，无法启动！");
				}
			}
		}
	};

    public class AppmenuAdapter extends BaseAdapter{    	
		public int getCount() {
			return mInstalledApks.size();
		}

		public Object getItem(int index) {
			return mInstalledApks.get(index);
		}

		public long getItemId(int position) {
			return position;
		}

		public class MenuItemView{
			public ImageView mIcon;
			public TextView mName;

			public MenuItemView(ImageView icon, TextView name){
				mIcon = icon;
				mName = name;
			}
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			MenuItemView menuItem = null;

			if (convertView == null){
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.appitem, null);				

				menuItem = new MenuItemView(
						(ImageView)convertView.findViewById(R.id.imageViewAppIcon),
						(TextView)convertView.findViewById(R.id.textViewAppName));
				menuItem.mIcon.setOnClickListener(mAppItemClickListener);
				menuItem.mName.setOnClickListener(mAppItemClickListener);
				convertView.setTag(menuItem);
			}else{
				menuItem = (MenuItemView)convertView.getTag();				
			}

			PackageInfo info = mInstalledApks.get(position);
			if (info != null){
				menuItem.mIcon.setBackgroundDrawable(info.applicationInfo.loadIcon(mPackageMgr));
				menuItem.mIcon.setTag(info);
				menuItem.mName.setText(info.applicationInfo.loadLabel(mPackageMgr).toString());
				menuItem.mName.setTag(info);
			}
			
			return convertView;
		}
    };	
}

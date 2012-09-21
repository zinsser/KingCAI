package com.king.cai;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.king.cai.common.ComunicableActivity;
import com.king.cai.examination.PaperActivity;
import com.king.cai.message.RequestMessage_Logout;
import com.king.cai.service.KingService.LoginInfo;

public class WorkspaceActivity extends ComunicableActivity  {
	private Button mButtonPaper = null;	
	private LinearLayout mLinearLayoutInfo = null;
	private LoginInfo mLoginInfo = null;
	
	private List<PackageInfo> mInstalledApks = new ArrayList<PackageInfo>(); 
	private PackageManager mPackageMgr = null;
	private GridView mAppmenuView = null;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workspace);

		mPackageMgr = getPackageManager();
//		new AppEnumTask().execute(mPackageMgr);
		
		parseIntent();

		mLinearLayoutInfo = (LinearLayout)findViewById(R.id.linearLayoutInfo);

		findViewById(R.id.buttonLogoutOnWorkspace).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				onLogout();
			}
		});
		mAppmenuView = (GridView)findViewById(R.id.gridViewAppmenu);
        AppmenuAdapter adapter = new AppmenuAdapter();
        mAppmenuView.setAdapter(adapter);	

        findViewById(R.id.buttonStudyOnWorkspace).setOnClickListener(new View.OnClickListener() {
    		
    		public void onClick(View v) {
    			startExplorerActivity();
    		}
    	});	
        mButtonPaper = (Button)findViewById(R.id.buttonTestOnWorkspace);
        mButtonPaper.setTextColor(Color.rgb(128, 128, 128));
        mButtonPaper.setEnabled(false);
        mButtonPaper.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				startPaperActivity();
			}
		});
	}
	
	private boolean isHiddenApp(String packagename){
		boolean bRet = false;
		if (packagename.equals(getPackageName())){
			bRet = true;
		}
		return bRet;
	}
	
	private void parseIntent(){
		Bundle bundle = getIntent().getExtras();
		if (getIntent().hasExtra("Cause") && bundle != null){
			String cause = bundle.getString("Cause");
			if (cause.equals("Paper")){

			}else if (cause.equals("Relogin")){
				startLoginActivity();
			}
		}
	}

	private void startExplorerActivity(){
		
		String rootDir = Environment.getExternalStorageDirectory().getPath()+"/KingCAI"; //"/";// 
		File destDir = new File(rootDir);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		Intent openExplorerIntent = new Intent(WorkspaceActivity.this, ExplorerActivity.class);
		openExplorerIntent.putExtra("RootDir", rootDir);
		startActivity(openExplorerIntent);		
	}
	
	private void startLoginActivity(){
		Intent openLoginActivity = new Intent(WorkspaceActivity.this, LoginActivity.class);
		startActivity(openLoginActivity);
	}
	
	private void startPaperActivity(){
		Intent openPaperActivity = new Intent(WorkspaceActivity.this, PaperActivity.class);
		if (mLoginInfo != null){
			openPaperActivity.putExtra(KingCAIConfig.StudentID, mLoginInfo.mID);
			openPaperActivity.putExtra(KingCAIConfig.StudentInfo, mLoginInfo.mInfo);
			openPaperActivity.putExtra(KingCAIConfig.ServerIP, "127.0.0.1");
			openPaperActivity.putExtra(KingCAIConfig.SSID, "h3c");
			openPaperActivity.putExtra(KingCAIConfig.Offline, mLoginInfo.mOffline);
			openPaperActivity.putExtra(KingCAIConfig.ExceptionExit, mLoginInfo.mExceptionExit);		
		}
		startActivity(openPaperActivity);		
	}

	private void onLogout(){
		mServiceChannel.sendMessage(new RequestMessage_Logout(), 0);
		startLoginActivity();
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
	}
    
	@Override
	protected void onServiceReady() {
		mLoginInfo = mServiceChannel.getLoginInfo();

		mLinearLayoutInfo.setVisibility(View.VISIBLE);
        mButtonPaper.setTextColor(Color.rgb(255, 255, 255));
        mButtonPaper.setEnabled(true);
	}

    public class AppEnumTask extends  AsyncTask<PackageManager, PackageInfo, String> {
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

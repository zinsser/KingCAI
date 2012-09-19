package com.king.cai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
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
import com.king.cai.message.ActiveMessage_NewQuestion.ProgressObject;
import com.king.cai.message.ActiveMessage_NewQuestion.QuestionParseTask;
import com.king.cai.service.KingService.LoginInfo;

public class WorkspaceActivity extends ComunicableActivity  {
	private Button mButtonLogin = null;
	private LinearLayout mLinearLayoutInfo = null;
	private LoginInfo mLoginInfo = null;
	
	private List<PackageInfo> mInstalledApks = new ArrayList<PackageInfo>(); 
	private PackageManager mPackageMgr = null;
	private GridView mAppmenuView = null;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workspace);

		initPackageInfo();
		parseIntent();

		mLinearLayoutInfo = (LinearLayout)findViewById(R.id.linearLayoutInfo);
		mButtonLogin = (Button)findViewById(R.id.buttonLoginOnWorkspace);
		mButtonLogin.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				startLoginActivity();
			}
		});
		
		mAppmenuView = (GridView)findViewById(R.id.gridViewAppmenu);
        AppmenuAdapter adapter = new AppmenuAdapter();
        mAppmenuView.setAdapter(adapter);		
	}
	
	private void initPackageInfo(){
		mPackageMgr = getPackageManager();
		new AppEnumTask().execute(mPackageMgr);
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
		if (getIntent().hasExtra("From") && bundle != null){
			String from = bundle.getString("From");
			if (from.equals("Login")){
				
			}else if (from.equals("Paper")){
				startLoginActivity();				
			}
		}else{
			//TODO: 需要根据是否已存在登录信息来显示或隐藏”登录“按钮
			
		}
	}

	private void startLoginActivity(){
		Intent openLoginActivity = new Intent(WorkspaceActivity.this, LoginActivity.class);
		startActivity(openLoginActivity);
	}
	
	@Override
	protected void doHandleInnerMessage(Message innerMessage) {		
	}
    
	@Override
	protected void onServiceReady() {
		mLoginInfo = mServiceChannel.getLoginInfo();
		if (mLoginInfo != null){
			mLinearLayoutInfo.setVisibility(View.VISIBLE);
			mButtonLogin.setVisibility(View.GONE);
		}else {
			mLinearLayoutInfo.setVisibility(View.GONE);
			mButtonLogin.setVisibility(View.VISIBLE);			
		}
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
		
		@Override
		public void onClick(View v) {
			PackageInfo info = (PackageInfo)v.getTag();
			if (info.activities.length > 0){
				Intent intent = new Intent(Intent.ACTION_MAIN, null);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				String className = info.activities[0].name;
				ComponentName cn = new ComponentName(info.applicationInfo.packageName, className);
				intent.setComponent(cn);
				startActivity(intent);
			}else{
				Intent intent = new Intent(info.applicationInfo.packageName);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);				
				startActivity(intent);
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

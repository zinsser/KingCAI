package com.king.cai;

import com.king.cai.R;
import com.king.cai.examination.PaperActivity;
import com.king.cai.platform.KingCAIConfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AppEnumActivity extends Activity {
	private String mStudentInfo = null;
	private String mServerIP = null;
	private String mSSID = null;
	private boolean mOffline = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.applist);
        findViewById(R.id.btnTestOnLine).setOnClickListener(mAppClickListener);
		ParseIntentExtraParam();
    	setTitle("应用列表");
	}
	
	public View.OnClickListener mAppClickListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			if (v.getId() == R.id.btnTestOnLine){
				StartPaperActivity(mStudentInfo, mOffline);
			}
		}
	};

	public void StartPaperActivity(String studentInfo, boolean bOffline){
		Intent openSheetActivity = new Intent(this, PaperActivity.class);
		openSheetActivity.putExtra(KingCAIConfig.StudentInfo, studentInfo);
		openSheetActivity.putExtra(KingCAIConfig.ServerIP, mServerIP);
		openSheetActivity.putExtra(KingCAIConfig.SSID, "一年级");
		openSheetActivity.putExtra(KingCAIConfig.Offline, bOffline);
		startActivity(openSheetActivity);    
		finish();
    }
	
    private void ParseIntentExtraParam(){
        if (getIntent().hasExtra(KingCAIConfig.StudentInfo)){
            Bundle extra = getIntent().getExtras();
            mStudentInfo = extra.getString(KingCAIConfig.StudentInfo);
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
	
}

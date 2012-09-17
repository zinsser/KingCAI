package com.king.cai;

import android.os.Bundle;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

import com.king.cai.common.ComunicableActivity;
import com.king.cai.service.KingService.LoginInfo;

public class InfoActivity extends ComunicableActivity  {
	private LoginInfo mLoginInfo = null;
	private TextView mTextviewID = null;
	private TextView mTextviewName = null;
	private TextView mTextviewClass = null;
	private Button mLogin = null;
	private Button mStudy = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
 //       mTextviewID = (TextView)findViewById(R.id.text);
    }
    
	@Override
	protected void onServiceReady() {
		mLoginInfo = mServiceChannel.getLoginInfo();
	}	
	
	@Override
	protected void doHandleInnerMessage(Message innerMessage) {
		
	}
	
	private void showLoginInfo(){
		
	}	
}

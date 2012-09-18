package com.king.cai;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.king.cai.common.ComunicableActivity;

public class WorkspaceActivity extends ComunicableActivity  {
	private Button mButtonLogin = null;
	private LinearLayout mLinearLayoutInfo = null;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workspace);
		parseIntent();

		mLinearLayoutInfo = (LinearLayout)findViewById(R.id.linearLayoutInfo);
		mButtonLogin = (Button)findViewById(R.id.buttonLoginOnWorkspace);
		mButtonLogin.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				startLoginActivity();
			}
		});
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
	}
}

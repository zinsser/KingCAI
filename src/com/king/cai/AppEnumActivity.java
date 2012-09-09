package com.king.cai;

import com.king.cai.R;
import com.king.cai.examination.PaperActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AppEnumActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.applist);
	}
	
	public View.OnClickListener mAppClickListener = new View.OnClickListener() {
		
		public void onClick(View v) {
		}
	};

}

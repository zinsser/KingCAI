package com.king.cai;

import com.king.cai.platform.CommonDefine;

import android.app.Application;
import android.content.Intent;

public class TestOnLineApp extends Application{
	@Override
	public void onCreate(){
		//TODO£ººÎ´¦ stop service ÄØ£¿
		startService(new Intent(CommonDefine.START_PLATFORM_ACTION));
	}
}

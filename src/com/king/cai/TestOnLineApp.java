package com.king.cai;

import com.king.cai.platform.CommonDefine;

import android.app.Application;
import android.content.Intent;

public class TestOnLineApp extends Application{
	@Override
	public void onCreate(){
		//TODO：何处 stop service 呢？
		//TODO:增加试用时间限制条件
		startService(new Intent(CommonDefine.START_PLATFORM_ACTION));
	}
}

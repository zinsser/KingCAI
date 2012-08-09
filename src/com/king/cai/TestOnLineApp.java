package com.king.cai;

import android.app.Application;
import android.content.Intent;

public class TestOnLineApp extends Application{
	@Override
	public void onCreate(){
		//TODO：何处 stop service 呢？
		//TODO:增加试用时间限制条件
		startService(new Intent(KingCAIConfig.START_SERVICE_ACTION));
	}
}

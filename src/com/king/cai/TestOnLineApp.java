package com.king.cai;

import android.app.Application;
import android.content.Intent;

public class TestOnLineApp extends Application{
	@Override
	public void onCreate(){
		//TODO���δ� stop service �أ�
		//TODO:��������ʱ����������
		startService(new Intent(KingCAIConfig.START_SERVICE_ACTION));
	}
}

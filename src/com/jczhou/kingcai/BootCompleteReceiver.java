package com.jczhou.kingcai;

import com.jczhou.platform.CommonDefine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(CommonDefine.START_PLATFORM_ACTION));
	}

}

package com.king.cai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(KingCAIConfig.START_SERVICE_ACTION));
	}

}

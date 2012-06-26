package com.jczhou.platform.internal;

import com.jczhou.platform.CommonDefine;

import android.app.Service;
import android.content.Intent;


public abstract class FireMessageReceiver implements Runnable{
	private Service mHostService = null;
	public FireMessageReceiver(Service s){
		mHostService = s;
	}

	protected void FireMessage(String peerip, String msgData){	
    	Intent intent = new Intent(CommonDefine.TEXT_MESSAG_FROM_SERVICE_ACTION);
    	intent.putExtra("PEER", peerip);
    	intent.putExtra("CONTENT", msgData);
    	mHostService.sendBroadcast(intent);
	}
	
	protected void FireMessage(String peerip, byte[] msgData){
    	Intent intent = new Intent(CommonDefine.BINARY_MESSAG_FROM_SERVICE_ACTION);
    	intent.putExtra("PEER", peerip);
    	intent.putExtra("CONTENT", msgData);
    	mHostService.sendBroadcast(intent);
	}	
};
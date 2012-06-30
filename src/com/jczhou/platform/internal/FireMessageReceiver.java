package com.jczhou.platform.internal;

import java.nio.ByteBuffer;

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
    	mHostService.getApplication().sendBroadcast(intent);
	}
	
	protected void FireMessage(String peerip, byte[] msgData){
    	Intent intent = new Intent(CommonDefine.BINARY_MESSAG_FROM_SERVICE_ACTION);
    	intent.putExtra("PEER", peerip);
		ByteBuffer buf = ByteBuffer.allocate(msgData.length);
		buf.put(msgData);
		intent.putExtra("CONTENT", buf.array());
    	mHostService.getApplication().sendBroadcast(intent);
	}	
};
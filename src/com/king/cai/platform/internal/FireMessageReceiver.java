package com.king.cai.platform.internal;

import java.nio.ByteBuffer;

import com.king.cai.platform.CommonDefine;

import android.content.Intent;


public abstract class FireMessageReceiver implements Runnable{
	protected KingService mHostService = null;
	public FireMessageReceiver(KingService s){
		mHostService = s;
	}

	protected void FireMessage(String peerip, String msgData){	
    	Intent intent = new Intent(CommonDefine.TEXT_MESSAG_FROM_SERVICE_ACTION);
    	intent.putExtra("PEER", peerip);
    	intent.putExtra("CONTENT", msgData);
    	mHostService.getApplication().sendBroadcast(intent);
	}
	
	protected void FireMessage(String peerip, String id, byte[] msgData){
    	Intent intent = new Intent(CommonDefine.BINARY_MESSAG_FROM_SERVICE_ACTION);
    	intent.putExtra("PEER", peerip);
    	intent.putExtra("ID", id);
		ByteBuffer buf = ByteBuffer.allocate(msgData.length);
		buf.put(msgData);
		intent.putExtra("CONTENT", buf.array());
    	mHostService.getApplication().sendBroadcast(intent);
	}	
};
package com.king.cai.platform.internal;

import java.nio.ByteBuffer;
import android.os.Bundle;
import android.os.Message;


public abstract class FirableThread implements Runnable{
	protected Message mMessage = null;
	public FirableThread(Message msg){
		mMessage = msg;
	}

	protected void FireMessage(String peerip, String id, byte[] msgData){
		Bundle bundle = new Bundle();
		ByteBuffer buf = ByteBuffer.allocate(msgData.length);
		buf.put(msgData);
		bundle.putByteArray("CONTENT", buf.array());
    	bundle.putString("PEER", peerip);
    	bundle.putString("ID", id);
    	mMessage.setData(bundle);
    	mMessage.sendToTarget();
	}	
};
package com.king.cai.service;

import java.nio.ByteBuffer;
import android.os.Bundle;
import android.os.Message;


public abstract class FirableRunner implements Runnable{
	protected Message mInnerMessage = null;
	protected boolean mRunning = false;
	public FirableRunner(Message innerMessage){
		mInnerMessage = innerMessage;
	}

	protected void FireMessage(String peerip, ByteBuffer msgData, int size, boolean bTextType){
		Bundle bundle = new Bundle();
    	bundle.putString("PEER", peerip);
		bundle.putByteArray("CONTENT", msgData.array());
		bundle.putInt("SIZE", size);
		bundle.putBoolean("TYPE", bTextType);
    	mInnerMessage.setData(bundle);
    	mInnerMessage.sendToTarget();
	}
	
	public boolean isRunning(){
		return mRunning;
	}
	
	public void stopRunner(){
		mRunning = false;
	} 
	
	public void run(){
		mRunning = true;
		do{
			doRun();
		}while (mRunning);
		onExit();
	}
	
	protected abstract void doRun();
	protected abstract void onExit();
};
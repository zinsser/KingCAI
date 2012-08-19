package com.king.cai.service;

import java.nio.ByteBuffer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public abstract class FirableRunner implements Runnable{
	protected Handler mInnerHandler = null;
	protected boolean mRunning = false;
	public FirableRunner(Handler innerHandler){
		mInnerHandler = innerHandler;
	}

	protected Bundle contructTextBundle(String peerAddr, ByteBuffer msgData){
		Bundle bundle = new Bundle();
		bundle.putBoolean("Type", true);
		bundle.putString("Peer", peerAddr);
		bundle.putByteArray("Content", msgData.array());
		
		return bundle;
	}
	
	protected Bundle contructBinaryBundle(ByteBuffer data){
		Bundle bundle = new Bundle();
		bundle.putBoolean("Type", false);
		bundle.putByteArray("Content", data.array());
		
		return bundle;
	}
	
	protected void fireMessage(Bundle bundle){
		Message innerMessage = mInnerHandler.obtainMessage(KingService.SOCKET_EVENT);
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();
	}
	
	public boolean isRunning(){
		return mRunning;
	}
	
	public void stopRunner(){
		mRunning = false;
	} 
	
	public void run(){
		mRunning = true;
		onStart();
		do{
			doRun();
		}while (mRunning);
		onExit();
	}
	
	protected abstract void onStart();	
	protected abstract void doRun();
	protected abstract void onExit();
};
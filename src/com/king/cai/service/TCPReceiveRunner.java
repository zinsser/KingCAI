package com.king.cai.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.king.cai.KingCAIConfig;

import android.os.Bundle;
import android.os.Handler;

public class TCPReceiveRunner extends FirableRunner{
	private final int mDefaultBufferSize = 64 * 1024;
	private InputStream mInputStream = null;
	private ByteBuffer mReceiveBuf = null;//ByteBuffer.allocate(128 * 1024);
	private int mPort = 0;
	private String mPeerAddr = null;
	private int mExpectSize = 0;
	public TCPReceiveRunner(Handler innerHandler, 
						InputStream is, String peer, int port) {
		super(innerHandler);
		mInputStream = is;
		mPeerAddr = peer;
		mPort = port;
	}

	@Override
	protected void doRun() {
		try {
			int subReadSize = -1;
			int leftSize = mExpectSize > 0 ? mExpectSize : mDefaultBufferSize;
			mReceiveBuf = null;
			mReceiveBuf = ByteBuffer.allocate(leftSize);
			do {
				if (mExpectSize != 0){
					byte[] readBuffer = new byte[leftSize];
					subReadSize = mInputStream.read(readBuffer, 0, leftSize); 
					if (subReadSize > 0){
						mReceiveBuf.put(readBuffer, 0, subReadSize);
						leftSize -= subReadSize;
					}	
					readBuffer = null;
				}else{
					subReadSize = mInputStream.read(mReceiveBuf.array());
				}
			}while (mExpectSize > 0 && subReadSize > 0 && leftSize > 0);

			if (subReadSize > 0){
				if (mPort != KingCAIConfig.mTcpPort){
					Bundle bundle = contructBinaryBundle(mReceiveBuf);
					fireMessage(bundle);
				}else{
					String str = new String(mReceiveBuf.array(), KingCAIConfig.mCharterSet);
					Bundle bundle = contructTextBundle(mPeerAddr, mReceiveBuf.array());
					fireMessage(bundle);
				}
			}
		} catch (IOException e) {
			KingService.getLogService().addLog(e.toString());
			e.printStackTrace();
		}
	}

	@Override
	protected void onExit() {
		mReceiveBuf = null;
	}

	@Override
	protected void onStart() {		
	}
	
	public void updateExpectSize(Integer size){
		mExpectSize = size;
	}
}

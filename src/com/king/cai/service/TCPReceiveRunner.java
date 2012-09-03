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
	private int mLeftSize = mDefaultBufferSize;
	private int mTotalSize = 0;
	
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
			if (mReceiveBuf != null) mReceiveBuf.clear();
			mReceiveBuf = null;
			mReceiveBuf = ByteBuffer.allocate(mDefaultBufferSize);
			do {
				subReadSize = mInputStream.read(mReceiveBuf.array(), mTotalSize, mReceiveBuf.capacity() - mTotalSize);
				
				if (subReadSize > 0 && mExpectSize > 0){
					mTotalSize += subReadSize;
				}
			}while (mExpectSize > 0 && subReadSize > 0 && mTotalSize < mExpectSize);

			if (subReadSize > 0){
				String str = new String(mReceiveBuf.array(), KingCAIConfig.mCharterSet);
				Bundle bundle = contructTextBundle(mPeerAddr, mReceiveBuf.array());
				fireMessage(bundle);
			}
		} catch (IOException e) {
			KingService.getLogService().addLog(e.toString());
			e.printStackTrace();
		}
		
		mTotalSize = 0;
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
		mLeftSize = mExpectSize;
		mTotalSize = 0;
	}
}

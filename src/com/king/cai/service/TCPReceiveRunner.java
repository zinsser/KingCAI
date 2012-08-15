package com.king.cai.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import com.king.cai.KingCAIConfig;

import android.os.Bundle;
import android.os.Message;

public class TCPReceiveRunner extends FirableRunner{
	private InputStream mInputStream = null;
	private ByteBuffer mReceiveBuf = ByteBuffer.allocate(128 * 1024);
	private int mPort = 0;
	private String mPeerAddr = null;
	private DownloadCache mDownloadCacher = null;
	private BufferedReader mReader = null;
	private int mExpectSize = 0;
	
	public TCPReceiveRunner(Message innerMessage, 
						InputStream is, String peer, int port, boolean bCache) {
		super(innerMessage);
		mInputStream = is;
		mPeerAddr = peer;
		mPort = port;
		mReader = new BufferedReader(new InputStreamReader(mInputStream));
		if (bCache){
			mDownloadCacher = new DownloadCache();
		}
	}

	public DownloadCache getCacher(){
		return mDownloadCacher;
	} 
	
	@Override
	protected void doRun() {
		try {
			mReceiveBuf.clear();
			int totalReadSize = 0;
			int subReadSize = -1;
			do {
				subReadSize = mInputStream.read(mReceiveBuf.array(), totalReadSize, mExpectSize); 
				if (subReadSize > 0){
					totalReadSize += subReadSize;
				}
			}while (subReadSize > 0 && totalReadSize < mExpectSize);

			if (subReadSize > 0){
				if (mDownloadCacher != null 
						&& mPort == KingCAIConfig.mTextReceivePort){
					if (mDownloadCacher.getRemain() != 0){
						mDownloadCacher.receiveData(mReceiveBuf, subReadSize);
					}else{
						Bundle bundle = contructBinaryBundle(mDownloadCacher.getQuestionID(), 
															 mDownloadCacher.getImageIndex(), 
															 mDownloadCacher.getDataBuffer(),
															 mDownloadCacher.getDataBuffer().capacity());
						fireMessage(bundle);  
						mDownloadCacher.dispatchTask();
					}	
				}else{
					Bundle bundle = contructTextBundle(mPeerAddr, mReceiveBuf, totalReadSize);
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
		if (mDownloadCacher != null){
			mDownloadCacher.exitDownload();
			mDownloadCacher = null;
		}
		mReceiveBuf = null;
	}

	@Override
	protected void onStart() {		
	}
	
	public void updateExpectSize(Integer size){
		mExpectSize = size;
	}
}

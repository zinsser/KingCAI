package com.king.cai.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.king.cai.KingCAIConfig;

import android.os.Message;

public class TCPReceiveRunner extends FirableRunner{
	private InputStream mInputStream = null;
	private ByteBuffer mReceiveBuf = ByteBuffer.allocate(8192);
	private int mPort = 0;
	private String mPeerAddr = null;
	private DownloadCache mDownloadCacher = null;
	
	public TCPReceiveRunner(Message innerMessage, 
						InputStream is, String peer, int port, boolean bCache) {
		super(innerMessage);
		mInputStream = is;
		mPeerAddr = peer;
		mPort = port;
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
			int readSize = mInputStream.read(mReceiveBuf.array()); 
			if (readSize > 0){
				if (mDownloadCacher != null){
					if (mDownloadCacher.getRemain() != 0){
						mDownloadCacher.receiveData(mReceiveBuf.array(), mReceiveBuf.array().length);
					}else{
						FireMessage(mPeerAddr, mDownloadCacher.getDataBuffer(), 
										mDownloadCacher.getDataBuffer().capacity(), 
										mPort == KingCAIConfig.mTextReceivePort);  
						mDownloadCacher.dispatchTask();
					}				
				}else{
					FireMessage(mPeerAddr, mReceiveBuf, readSize, 
							mPort == KingCAIConfig.mTextReceivePort);
				}
			}
		} catch (IOException e) {
			KingService.addLog(e.toString());
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
}

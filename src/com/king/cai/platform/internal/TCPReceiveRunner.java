package com.king.cai.platform.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.king.cai.platform.KingCAIConfig;

import android.os.Message;

public class TCPReceiveRunner extends FirableRunner{
	private InputStream mInputStream = null;
	private ByteBuffer mReceiveBuf = ByteBuffer.allocate(8192);
	private int mPort = 0;
	private String mPeerAddr = null;
	
	public TCPReceiveRunner(Message innerMessage, 
						InputStream is, String peer, int port) {
		super(innerMessage);
		mInputStream = is;
		mPeerAddr = peer;
		mPort = port;
	}
	
	@Override
	protected void doRun() {
		try {
			if (mInputStream.read(mReceiveBuf.array()) > 0){
				FireMessage(mPeerAddr, mReceiveBuf, 
						mPort == KingCAIConfig.mTextReceivePort);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onExit() {		
	}
}

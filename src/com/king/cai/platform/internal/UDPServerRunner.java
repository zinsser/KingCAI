package com.king.cai.platform.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import android.os.Message;
import android.util.Log;

import com.king.cai.platform.KingCAIConfig;
import com.king.cai.platform.internal.FirableRunner;


public class UDPServerRunner extends FirableRunner{
	private DatagramSocket mDatagramSocket = null;
	private DatagramPacket mDatagramPacket = null;
	private ByteBuffer mMsgBuffer = ByteBuffer.allocate(8192);
	private int mPort;
	
	public UDPServerRunner(Message innerMessage, int port){
		super(innerMessage);
		mPort = port;
		try {
			mDatagramSocket = null;
			mDatagramSocket = new DatagramSocket(mPort);
			
			mDatagramPacket = null;
			mDatagramPacket = new DatagramPacket(mMsgBuffer.array(), mMsgBuffer.array().length);
		}catch (SocketException e){
			e.printStackTrace();
		}
	}

	@Override
	protected void doRun(){
		try {
			mMsgBuffer.clear();
			mDatagramSocket.receive(mDatagramPacket);
			String peerAddr = mDatagramPacket.getAddress().getHostAddress().toString();
			Log.d("UDPReceiver", mDatagramPacket.getAddress().getHostAddress().toString()
					+ ":"+ new String(mDatagramPacket.getData(), KingCAIConfig.mCharterSet));
			
			String rawmsg = new String(mDatagramPacket.getData(), mDatagramPacket.getOffset(), 
					mDatagramPacket.getLength(), KingCAIConfig.mCharterSet);
			
			FireMessage(peerAddr, ByteBuffer.wrap(rawmsg.trim().getBytes()), true);
		}catch(IOException e) {
			e.printStackTrace();
		}
		mRunning = false;
	}

	@Override
	protected void onExit() {
		if (mDatagramSocket != null) {
			mDatagramSocket.close();
			mDatagramSocket = null;
		}		
		mDatagramPacket = null;
		mMsgBuffer = null;
	}
}

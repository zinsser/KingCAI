package com.king.cai.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

import android.os.Handler;
import android.util.Log;

public class MulticastReceiverRunner  extends FirableRunner{
    private MulticastSocket mMulticastSocket = null;
    private DatagramPacket mDatagramPacket = null;
	private ByteBuffer mMsgBuffer = ByteBuffer.allocate(8192);
	private int mPort;
	private String mGroupIp;
	
	public MulticastReceiverRunner(Handler innerHandler, String groupip, int port) {
		super(innerHandler);
		mGroupIp = groupip;
		mPort = port;
		try{
			if (mMulticastSocket != null){
				mMulticastSocket.close();
				mMulticastSocket = null;
			}
			mMulticastSocket = new MulticastSocket(mPort/*KingCAIConfig.mMulticastClientCommonPort*/);  
	    	mMulticastSocket.joinGroup(InetAddress.getByName(mGroupIp/*KingCAIConfig.mMulticastClientGroupIP*/));
	    	mDatagramPacket = new DatagramPacket(mMsgBuffer.array(), mMsgBuffer.array().length);
	    }catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {		
	}

	@Override
	protected void doRun() {
		try{
			if (mMsgBuffer != null) mMsgBuffer.clear();
			mMulticastSocket.receive(mDatagramPacket);
			String peerAddr = mDatagramPacket.getAddress().getHostAddress().toString();

			Log.d("MulticastReceiver", mDatagramPacket.getAddress().getHostAddress().toString()
					+ ":"+ new String(mDatagramPacket.getData()));
			String rawmsg = new String(mDatagramPacket.getData(), mDatagramPacket.getOffset(), 
					mDatagramPacket.getLength());
			
			fireMessage(contructTextBundle(peerAddr, rawmsg.trim().getBytes()));
		}catch (IOException e){
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onExit() {
		if (mMulticastSocket != null){
			mMulticastSocket.close();
			mMulticastSocket = null;
		}

		mMsgBuffer = null;
	}
}

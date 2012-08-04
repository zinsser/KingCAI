package com.king.cai.platform.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

import android.os.Message;
import android.util.Log;

import com.king.cai.platform.internal.FirableRunner;

public class MulticastSocketReceiver extends FirableRunner{
    private MulticastSocket mMulticastSocket = null;
    private DatagramPacket mDatagramPacket = null;
	private ByteBuffer mMsgBuffer = ByteBuffer.allocate(8192);

	public MulticastSocketReceiver(Message innerMessage, String groupip, int port){
		super(innerMessage);
        try{
        	mMulticastSocket = new MulticastSocket(port/*KingCAIConfig.mMulticastClientCommonPort*/);  
        	mMulticastSocket.joinGroup(InetAddress.getByName(groupip/*KingCAIConfig.mMulticastClientGroupIP*/));
        	mDatagramPacket = new DatagramPacket(mMsgBuffer.array(), mMsgBuffer.array().length);
        }catch (IOException e){
    		e.printStackTrace();
    	}
	}

	@Override
	protected void doRun() {
		try{
			if (mMsgBuffer != null) mMsgBuffer.clear();
			mMulticastSocket.receive(mDatagramPacket);
			String remoteip = mDatagramPacket.getAddress().getHostAddress().toString();

			Log.d("MulticastReceiver", mDatagramPacket.getAddress().getHostAddress().toString()
					+ ":"+ new String(mDatagramPacket.getData()));
			String rawmsg = new String(mDatagramPacket.getData(), mDatagramPacket.getOffset(), 
					mDatagramPacket.getLength());
			FireMessage(remoteip, ByteBuffer.wrap(rawmsg.trim().getBytes()), true);
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
		mDatagramPacket = null;
		mMsgBuffer = null;
	}
};

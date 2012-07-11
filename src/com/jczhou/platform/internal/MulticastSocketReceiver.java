package com.jczhou.platform.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

import android.util.Log;

import com.jczhou.platform.internal.FireMessageReceiver;
import com.jczhou.platform.KingCAIConfig;

public class MulticastSocketReceiver extends FireMessageReceiver{
	private boolean mStopMultiReceiver = false;
    private MulticastSocket mMulticastSocket = null;
    private DatagramPacket mDatagramPacket = null;
	private ByteBuffer mMsgBuffer = ByteBuffer.allocate(9216);

	public MulticastSocketReceiver(KingService s, String groupip, int port){
		super(s);
        try{
        	mMulticastSocket = new MulticastSocket(port/*KingCAIConfig.mMulticastClientCommonPort*/);  
        	mMulticastSocket.joinGroup(InetAddress.getByName(groupip/*KingCAIConfig.mMulticastClientGroupIP*/));
        	mDatagramPacket = new DatagramPacket(mMsgBuffer.array(), mMsgBuffer.array().length);
        }catch (IOException e){
    		e.printStackTrace();
    	}
	}
	

	public void run(){
		do{
			try{
				if (mMsgBuffer != null) mMsgBuffer.clear();
				mMulticastSocket.receive(mDatagramPacket);
				String remoteip = mDatagramPacket.getAddress().getHostAddress().toString();

				Log.d("MulticastReceiver", mDatagramPacket.getAddress().getHostAddress().toString()
						+ ":"+ new String(mDatagramPacket.getData()));
				String rawmsg = new String(mDatagramPacket.getData(), mDatagramPacket.getOffset(), 
						mDatagramPacket.getLength());
				FireMessage(remoteip, new String(rawmsg.getBytes(KingCAIConfig.mCharterSet), KingCAIConfig.mCharterSet).trim());
			}catch (IOException e){
				e.printStackTrace();
			}
		}while(!mStopMultiReceiver);
		
		mMulticastSocket.close();
	}
};

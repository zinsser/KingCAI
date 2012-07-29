package com.king.cai.platform.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import android.util.Log;

import com.king.cai.platform.KingCAIConfig;
import com.king.cai.platform.internal.FireMessageReceiver;


public class UDPSocketReceiver extends FireMessageReceiver{
	private DatagramSocket mDatagramSocket = null;
	private DatagramPacket mDatagramPacket = null;
	// 接收的字节大小，客户端发送的数据不能超过这个大小
	private ByteBuffer mMsgBuffer = ByteBuffer.allocate(20480);
	private int mPort;
	
	public UDPSocketReceiver(KingService s, int port){
		super(s);
		mPort = port;
		InitSocket();
	}

	public void InitSocket(){
		if (mDatagramSocket == null){
			try {
				// 建立Socket连接
				mDatagramSocket = new DatagramSocket(mPort/*KingCAIConfig.mUDPPort*/);
				mDatagramPacket = new DatagramPacket(mMsgBuffer.array(), mMsgBuffer.array().length);
			}catch (SocketException e){
				e.printStackTrace();
			}
		}
	}
	
	public void stopRunner(){
		if (mDatagramSocket != null) {
			mDatagramSocket.close();
			mDatagramSocket = null;
		}
		mDatagramPacket = null;
	}	
	
	public boolean isRunning(){
		return mDatagramPacket != null && mDatagramSocket != null;
	}
	
	public void run(){
		while (mDatagramPacket != null && mDatagramSocket != null){
			try {
				if (mDatagramPacket == null) break;
				if (mDatagramSocket == null) break;
				if (mMsgBuffer == null) break; 
				mMsgBuffer.clear();
				mDatagramSocket.receive(mDatagramPacket);
				String remoteip = mDatagramPacket.getAddress().getHostAddress().toString();
				Log.d("UDPReceiver", mDatagramPacket.getAddress().getHostAddress().toString()
						+ ":"+ new String(mDatagramPacket.getData(), KingCAIConfig.mCharterSet));
				String rawmsg = new String(mDatagramPacket.getData(), mDatagramPacket.getOffset(), 
						mDatagramPacket.getLength(), KingCAIConfig.mCharterSet);
				FireMessage(remoteip, rawmsg.trim());
			}catch(IOException e) {
				e.printStackTrace();
			}
		};
		
		if (mDatagramSocket != null) {
			mDatagramSocket.close();
			mDatagramSocket = null;
		}
	}
}

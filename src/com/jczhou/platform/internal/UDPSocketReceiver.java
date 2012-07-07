package com.jczhou.platform.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import android.app.Service;
import android.util.Log;

import com.jczhou.platform.internal.FireMessageReceiver;
import com.jczhou.platform.KingCAIConfig;


public class UDPSocketReceiver extends FireMessageReceiver{
	private DatagramSocket mDatagramSocket = null;
	private DatagramPacket mDatagramPacket = null;
	// 接收的字节大小，客户端发送的数据不能超过这个大小
	private ByteBuffer mMsgBuffer = ByteBuffer.allocate(20480);
	
	public UDPSocketReceiver(KingService s, int port){
		super(s);
		try {
			// 建立Socket连接
			mDatagramSocket = new DatagramSocket(port/*KingCAIConfig.mUDPPort*/);
			mDatagramPacket = new DatagramPacket(mMsgBuffer.array(), mMsgBuffer.array().length);
		}catch (SocketException e){
			e.printStackTrace();
		}
	}
	
	public void run(){
		String result = "[Starting]";
		do{
			try {
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
		}while (!result.contains("[NetLogout]")
				|| !"".equals(result));
		
		mDatagramSocket.close();
	}
}

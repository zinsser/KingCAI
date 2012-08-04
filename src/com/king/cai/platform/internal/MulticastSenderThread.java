package com.king.cai.platform.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSenderThread extends Thread{
	private static final int TTL = 4;
	private MulticastSocket mMultiSocket = null;
	private DatagramPacket mDatagramPacket = null;
	
	public MulticastSenderThread(String groupAddr, int port, String outterMessage){
		try {
			mMultiSocket = new MulticastSocket();
			mMultiSocket.setTimeToLive(TTL);
	        InetAddress GroupAddress = InetAddress.getByName(groupAddr);
	        mDatagramPacket = new DatagramPacket(outterMessage.getBytes(), outterMessage.getBytes().length, 
					GroupAddress, port);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public void run(){
		try{
			mMultiSocket.send(mDatagramPacket);
			mMultiSocket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		mMultiSocket = null;
		mDatagramPacket = null;		
	}
}

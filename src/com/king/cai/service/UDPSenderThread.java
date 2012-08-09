package com.king.cai.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPSenderThread extends Thread{
	private DatagramSocket mDatagramSocket = null;
	private DatagramPacket mDatagramPacket = null;
	
	public UDPSenderThread(String peerAddr, int port, String outterMessage){
		try {
			mDatagramSocket = new DatagramSocket();
			InetAddress local = InetAddress.getByName(peerAddr);
			mDatagramPacket = new DatagramPacket(outterMessage.getBytes(), outterMessage.getBytes().length,
					local, port);
		} catch (UnknownHostException e) {
			KingService.addLog(e.toString());			
			e.printStackTrace();
		} catch (SocketException e) {
			KingService.addLog(e.toString());			
			e.printStackTrace();
		}		
	}
	
	@Override
	public void run(){
		try {
			mDatagramSocket.send(mDatagramPacket);
			mDatagramSocket.close();
		} catch (IOException e){
			KingService.addLog(e.toString());			
			e.printStackTrace();
		}
		
		mDatagramSocket = null;
		mDatagramPacket = null;
	}
}

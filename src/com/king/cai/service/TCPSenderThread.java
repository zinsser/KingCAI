package com.king.cai.service;

import java.io.IOException;
import java.io.OutputStream;

public class TCPSenderThread extends Thread{
	private OutputStream mOutputStream = null;
	private String mOutterMessage = null;
	public TCPSenderThread(OutputStream os, String msg){
		mOutputStream = os;
		mOutterMessage = msg;
	}
	
	@Override
	public void run(){
		try {
			mOutputStream.write(mOutterMessage.getBytes());
			mOutputStream.flush();
		} catch (IOException e) {
			KingService.getLogService().addLog(e.toString());			
			e.printStackTrace();
		}
	}
}

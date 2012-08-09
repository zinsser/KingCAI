package com.king.cai.platform.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.platform.internal.TCPSenderThread;


public class TCPClient {
	private Message mInnerMessage = null;	
	private String mServerAddr = null;

	private ClientObject mTextClient = null;
	private ClientObject mBinaryClient = null;
	
	public class ClientObject{
		private Socket mSocket = null;
		private InputStream mInputStream = null;
		private OutputStream mOutputStream = null;
		private TCPReceiveRunner mReceiveRunner = null;
		
		public ClientObject(Message innerMsg, String addr, int port){
			try {
				mSocket = new Socket(addr, port);
				mInputStream = mSocket.getInputStream();
				mOutputStream = mSocket.getOutputStream();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			mReceiveRunner = new TCPReceiveRunner(innerMsg, mInputStream, 
									mSocket.getInetAddress().getHostAddress(), mSocket.getPort());
			new Thread(mReceiveRunner).start();
		}
		
		public void sendMessage(String outterMessage){
			TCPSenderThread tcpSendThread = new TCPSenderThread(mOutputStream, outterMessage);
			tcpSendThread.start();
		}
		
		public void stopRunner(){
			mReceiveRunner.stopRunner();
			//TODO: let sender thread run over
			try {
				mInputStream.close();
				mOutputStream.close();
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public TCPClient(Message innerMessage, String addr){
		mInnerMessage = innerMessage;
		mServerAddr = addr;
		
		contructClientObject();
	}

	public void sendMessage(String outterMessage){
		mTextClient.sendMessage(outterMessage);
	}
	
	public void onDestroy(){
		destroyClientObject();
	}
	
	private void destroyClientObject(){
		mTextClient.stopRunner();
		mTextClient = null;
		
		mBinaryClient.stopRunner();
		mBinaryClient = null;
	}
	
	private void contructClientObject(){
		mTextClient = null;
		mTextClient = new ClientObject(mInnerMessage, mServerAddr, 
										KingCAIConfig.mTextReceivePort);
		mBinaryClient = null;
		mBinaryClient = new ClientObject(mInnerMessage, mServerAddr, 
										KingCAIConfig.mImageReceivePort);
	}
}

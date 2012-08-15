package com.king.cai.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.service.TCPSenderThread;


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
		
		public ClientObject(Message innerMsg, String addr, int port, boolean bCache){
			try {
				mSocket = new Socket(addr, port);
				mInputStream = mSocket.getInputStream();
				mOutputStream = mSocket.getOutputStream();
			} catch (UnknownHostException e) {
				KingService.getLogService().addLog(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				KingService.getLogService().addLog(e.toString());
				e.printStackTrace();
			} 
			
			mReceiveRunner = new TCPReceiveRunner(innerMsg, mInputStream, 
									mSocket.getInetAddress().getHostAddress(), mSocket.getPort(),
									bCache);
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
		public TCPReceiveRunner getRunner(){
			return mReceiveRunner;
		}
	}
	
	public TCPClient(Message innerMessage, String addr){
		mInnerMessage = innerMessage;
		mServerAddr = addr;
		
		contructClientObject();
	}

	public void sendMessage(String outterMessage){
		if (mTextClient != null){
			mTextClient.sendMessage(outterMessage);
		}
	}
	
	public void onDestroy(){
		destroyClientObject();
	}

	private void contructClientObject(){
		mTextClient = null;
		mTextClient = new ClientObject(mInnerMessage, mServerAddr, 
										KingCAIConfig.mTextReceivePort, false);
		mBinaryClient = null;
		mBinaryClient = new ClientObject(mInnerMessage, mServerAddr, 
										KingCAIConfig.mImageReceivePort, true);
	}	
	
	private void destroyClientObject(){
		if (mTextClient != null){
			mTextClient.stopRunner();
			mTextClient = null;
		}
		
		if (mBinaryClient != null){
			mBinaryClient.stopRunner();
			mBinaryClient = null;
		}
	}
	
	public void addImageDownloadTask(String qid, String imageIndex, Message innerMessage){
		if (mBinaryClient != null && mBinaryClient.getRunner() != null 
				&& mBinaryClient.getRunner().getCacher() != null){
			mBinaryClient.getRunner().getCacher().addTask(qid, imageIndex, innerMessage);
		}
	}
	
	public void updateDownloadImageSize(String qid, String imageIndex, int size){
		if (mBinaryClient != null && mBinaryClient.getRunner() != null 
				&& mBinaryClient.getRunner().getCacher() != null){
			mBinaryClient.getRunner().getCacher().updateDownloadSize(qid, imageIndex, size);
		}
	}
	
	public void updatePaperSize(Integer size){
		if (mTextClient != null && mTextClient.getRunner() != null){
			mTextClient.getRunner().updateExpectSize( size);
		}
	}
}

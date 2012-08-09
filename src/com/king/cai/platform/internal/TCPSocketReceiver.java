package com.king.cai.platform.internal;

import java.io.IOException;
import java.io.InputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.king.cai.platform.KingCAIConfig;

import android.os.Message;
import android.util.Log;

public class TCPSocketReceiver extends FirableRunner{
    private ServerSocket mTcpSocket = null;
    private Boolean mStopped = false;
	private ByteBuffer mReceiveBuf = ByteBuffer.allocate(8192);
	private ByteBuffer mImageBuf = null;
	private int mRemain = 0;
	private int mPort = 0;

	protected TCPSocketReceiver(Message msg, int port) {
		super(msg);
		mPort = port;
		InitSocket();
	}

	public void InitSocket(){
		if (mTcpSocket == null){
			try{ 
				mTcpSocket = new ServerSocket(mPort);
				mTcpSocket.setReceiveBufferSize(8192);
				mTcpSocket.setReuseAddress(true);
			}catch (IOException e){
				e.printStackTrace();
			}		
		}
	}
	
	public void stopRunner(){
		mStopped = true;
		try {
			if (mTcpSocket != null) {
				mTcpSocket.close();
				mTcpSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	public boolean isRunning(){
		return !mStopped;
	}
	
	public void run() {
		while (!mStopped) {
			Socket socket = null;
			try{
			    socket = mTcpSocket.accept();
			    
			    InputStream in = socket.getInputStream();
			    mReceiveBuf.clear();

			    while (in.read(mReceiveBuf.array()) > 0){
				    Log.d("TCPSocket", "msg:" + mReceiveBuf.array());
			    	String msg = new String(mReceiveBuf.array(), KingCAIConfig.mCharterSet);
			    	if (msg.contains(KingCAIConfig.NewImageMessageTag)){
			    		int posEnd = msg.indexOf("\\");
			    		int posLen = msg.indexOf("[length]");
			    		String id = msg.substring("[ImageBC]".length(), posLen);
			    		mRemain = Integer.parseInt(msg.substring(posLen + "[length]".length(), posEnd));
			    		mImageBuf = null;
			    		mImageBuf = ByteBuffer.allocate(mRemain);
			    		mImageBuf.clear();

			    		int readSize = 0 ;
			            int length   = 0 ;

			            do{
				            byte[] buffer = null;
				            buffer = new byte[2000];

			            	while ((readSize = in.read(buffer, 0, 2000)) != -1
			            			&& length < mRemain){
					            mImageBuf.put(buffer, 0, readSize);   
					            length += readSize;
				            }
			            }while (length < mRemain);

			            FireMessage(socket.getInetAddress().getHostAddress(),/* id, */mImageBuf, mImageBuf.capacity(), false);				    		
			    	}else{
			    		FireMessage(socket.getInetAddress().getHostAddress(), ByteBuffer.wrap(msg.getBytes()), 
			    							ByteBuffer.wrap(msg.getBytes()).capacity(), true);
			    	}
				    mReceiveBuf.clear();
			    } 
			    in.close();
			} catch (IOException e) {  
				e.printStackTrace();  
			}finally{
				if (socket != null){
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}			
		}
		try {
			if (mTcpSocket != null) {
				mTcpSocket.close();
				mTcpSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}

	@Override
	protected void doRun() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onExit() {
		// TODO Auto-generated method stub
		
	}
}
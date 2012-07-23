package com.king.cai.platform.internal;

import java.io.IOException;
import java.io.InputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.king.cai.platform.KingCAIConfig;

import android.util.Log;

public class TCPSocketReceiver extends FireMessageReceiver{
    private ServerSocket mTcpSocket = null;
    private Boolean mStopped = false;
	private ByteBuffer mReceiveBuf = ByteBuffer.allocate(8192);
	private ByteBuffer mImageBuf = null;
	private int mRemain = 0;
	private int mPort = 0;

	protected TCPSocketReceiver(KingService s, int port) {
		super(s);
		mPort = port;
		try{ 
			mTcpSocket = new ServerSocket(mPort);
			mTcpSocket.setReceiveBufferSize(8192);
			mTcpSocket.setReuseAddress(true);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public void stopRunner(){
		synchronized (mStopped){
			mStopped = true;
		};
	}
	
	public void run() {
/*		boolean bLocalStopped = false;
		synchronized (mStopped){
			bLocalStopped = mStopped;
		}
	*/	while (!mStopped) {
			Socket socket = null;
			try{
			    socket = mTcpSocket.accept();
			    mHostService.setTextChannelSocket(socket);
			    
			    InputStream in = socket.getInputStream();
			    mReceiveBuf.clear();

			    while (in.read(mReceiveBuf.array()) > 0){
				    Log.d("TCPSocket", "msg:" + mReceiveBuf.array());
			    	String msg = new String(mReceiveBuf.array(), KingCAIConfig.mCharterSet);
			    	if (msg.contains(KingCAIConfig.NewImageMessageTag)){
			    		int pos = msg.indexOf("\\");
			    		mRemain = Integer.parseInt(msg.substring("[ImageBC]".length(), pos));
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

			            FireMessage(socket.getInetAddress().getHostAddress(), mImageBuf.array());				    		
			    	}else{
			    		FireMessage(socket.getInetAddress().getHostAddress(), msg);
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
/*
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			synchronized (mStopped){
				bLocalStopped = mStopped;
			}*/			
		}
		try {
			mTcpSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
}
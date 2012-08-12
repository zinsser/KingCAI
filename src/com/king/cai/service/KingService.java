package com.king.cai.service;

import java.util.ArrayList;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.RequestMessage;
import com.king.cai.message.RequestMessage_Image;
import com.king.cai.message.RequestMessage_Login;
import com.king.cai.message.RequestMessage_QueryServer;
import com.king.cai.service.UDPServerRunner;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class KingService extends Service{        
    private String mServerAddr = null;
    private String mActiveSSID = null;

	private UDPServerRunner mUdpReceiverRoutine = null;
    private TCPClient mTcpClient = null;
    private WifiMonitor mWifiMonitor = null;

    
    //这里定义一个Binder类，用在onBind()有方法里，这样Activity那边可以获取到 
    private MyBinder mBinder = new MyBinder();      
	public class MyBinder extends Binder{
		public KingService getService(){
            return KingService.this;
        }  
    }      

	@Override
	public IBinder onBind(Intent intent) {
		if (mWifiMonitor != null){
			mWifiMonitor.registIntentFilter(getApplicationContext());
		}
        return mBinder;
	}
      
    @Override  
    public boolean onUnbind(Intent intent) {
    	if (mWifiMonitor != null){
    		mWifiMonitor.unRegistIntentFilter(getApplicationContext());
    	}
        return super.onUnbind(intent);  
    }
    
    @Override  
    public void onCreate() {  
		super.onCreate();
		mWifiMonitor = null;
		mWifiMonitor = new WifiMonitor(getApplicationContext(), 
										Message.obtain(mHandler, WIFI_EVENT));
    }
    
    public static final int SOCKET_EVENT = 0;
    public static final int WIFI_EVENT = 1;
    public static final int REQUEST_EVENT = 2;
    
    private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
			Bundle bundle = msg.getData();
    		switch (msg.what){
    		case SOCKET_EVENT:
    			Intent intent = new Intent(KingCAIConfig.SOCKET_EVENT_ACTION);
    			intent.putExtras(msg.getData());
    			sendBroadcast(intent);	
    			break;
    		case WIFI_EVENT:
    			break;
    		case REQUEST_EVENT:
    			String qid = bundle.getString("ID");
    			String imageIndex = bundle.getString("Index");
    			
    			KingService.this.sendMessage(new RequestMessage_Image(qid, imageIndex), 0);
    			break;
    		}
    	}
    };

    public void startScanSSID(Bundle bundle){
    	if (mWifiMonitor != null){
    		mWifiMonitor.startScanSSID(bundle);
    	}
    }
    
    public void connectSSID(String ssid){
    	mActiveSSID = ssid;
    	if (mWifiMonitor != null){
    		mWifiMonitor.connectToWifi(mActiveSSID);
    	}
    }
        
	public void queryServer(){
		//0，学生终端和教师服务器连接到同一个SSID，组成无线局域网		
		//1，学生终端向局域网广播发送教师服务器查询消息，同时启动UDP服务器
		//2，教师服务器收到该消息后，将ip地址作为响应通过UDP消息返回
		//3，学生终端通过UDP接收到该响应后，更新本地的服务器IP地址
		//4，并启动TCPClient，通过TCP和教师服务器保持通信
		sendMessage(new RequestMessage_QueryServer(getLocalIPAddress()));
		startUDPServer();
	}
	
	private void startUDPServer(){
		if (mUdpReceiverRoutine == null){
			mUdpReceiverRoutine = new UDPServerRunner(Message.obtain(mHandler, SOCKET_EVENT), 
					KingCAIConfig.mUDPPort);
		}
		if (!mUdpReceiverRoutine.isRunning()){
			new Thread(mUdpReceiverRoutine).start();
		}
	}

    public void updateServer(String addr, String ssid){
    	if (addr != null && !addr.equals(mServerAddr)){
	    	mServerAddr = addr;
	    	if (mTcpClient != null){
	    		mTcpClient.onDestroy();
	    		mTcpClient = null;
	    	}
	   		mTcpClient = new TCPClient(Message.obtain(mHandler, SOCKET_EVENT), mServerAddr);
    	}
    }
	
	public void connectServer(String number, String password){
		sendMessage(new RequestMessage_Login(number, password), 0);
	}
	
	public void addDownloadTask(String qid, String imageIndex){
		if (mTcpClient != null){
			Message innerMessage = mHandler.obtainMessage(REQUEST_EVENT);
			Bundle bundle = new Bundle();
			bundle.putString("ID", qid);
			bundle.putString("Index", imageIndex);
			innerMessage.setData(bundle);
			mTcpClient.addImageDownloadTask(qid, imageIndex, innerMessage);
		}
	}

	public void updateDownloadSize(String qid, String imageIndex, int size){
		if (mTcpClient != null){
			mTcpClient.updateDownloadImageSize(qid, imageIndex, size);
		}
	}
	
	//TCP Socket use this function
	public void sendMessage(RequestMessage msg, int mode ){
		if (mTcpClient != null){
			mTcpClient.sendMessage(msg.ToPack());
		}
	}	
		
	//multicast socket Socket use this function		
	public void sendMessage(RequestMessage msg) {
	    MulticastSenderThread sender = new MulticastSenderThread(KingCAIConfig.mMulticastClientGroupIP,
	    									KingCAIConfig.mImageReceivePort, msg.ToPack());
	    sender.start();
	}
	
    public String getLocalIPAddress(){
    	return mWifiMonitor != null ? mWifiMonitor.getLocalIPAddress() : "0.0.0.0";
    }
    
    public static void addLog(String log){
    	LoggerManager.getInstance().addLog(log);
    }
    
    public static ArrayList<String> getLog(){
    	return LoggerManager.getInstance().getLog();
    }
    
    public static void redirectLog2SDCard(){
    	LoggerManager.getInstance().redirectLog2SDCard();
    }
}

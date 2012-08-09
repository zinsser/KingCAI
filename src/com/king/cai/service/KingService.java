package com.king.cai.service;

import java.util.ArrayList;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.LoginRequestMessage;
import com.king.cai.message.QueryServerMessage;
import com.king.cai.message.RequestMessage;
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

    
    //���ﶨ���һ��Binder�࣬����onBind()�з��������Activity�Ǳ߿��Ի�ȡ�� 
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
    
    private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
    		case SOCKET_EVENT:
    			Bundle bundle = msg.getData();
    			boolean bText = bundle.getBoolean("TYPE");
    			if (bText){
        			Intent intent = new Intent(KingCAIConfig.SOCKET_EVENT_ACTION);
        			intent.putExtras(msg.getData());
        			sendBroadcast(intent);    				
    			}/*else{
 //   				if (mDownloadManager != null){
 //   					mDownloadManager.receiveData(bundle.getByteArray("CONTENT"),
 //   							bundle.getInt("SIZE"));
    				}
    			}*/
    			break;
    		case WIFI_EVENT:
    			break;
    		}
    	}
    };

    public void scanSSID(){
    	if (mWifiMonitor != null){
    		mWifiMonitor.startScanServer();
    	}
    }
    
    public void connectSSID(String ssid){
    	mActiveSSID = ssid;
    	if (mWifiMonitor != null){
    		mWifiMonitor.connectToWifi(mActiveSSID);
    	}
    }
        
	public void queryServer(){
		//0��ѧ���ն˺ͽ�ʦ���������ӵ�ͬһ��SSID��������߾�����		
		//1��ѧ���ն���������㲥���ͽ�ʦ��������ѯ��Ϣ��ͬʱ����UDP������
		//2����ʦ�������յ�����Ϣ�󣬽�ip��ַ��Ϊ��Ӧͨ��UDP��Ϣ����
		//3��ѧ���ն�ͨ��UDP���յ�����Ӧ�󣬸��±��صķ�����IP��ַ
		//4��������TCPClient��ͨ��TCP�ͽ�ʦ����������ͨ��
		sendMessage(new QueryServerMessage(getLocalIPAddress()));
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

    public void updateServer(String addr){
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
		sendMessage(new LoginRequestMessage(number, password), 0);
	}
	
	public void addDownloadTask(String qid, String imageIndex){
		if (mTcpClient != null){
			mTcpClient.addImageDownloadTask(qid, imageIndex);
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
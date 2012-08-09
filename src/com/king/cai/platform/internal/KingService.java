package com.king.cai.platform.internal;

import com.king.cai.KingCAIConfig;
import com.king.cai.messageservice.LoginRequestMessage;
import com.king.cai.messageservice.QueryServerMessage;
import com.king.cai.messageservice.RequestMessage;
import com.king.cai.platform.internal.UDPServerRunner;

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
    private DownloadService mDownloadManager = null;
        
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
		//	mWifiMonitor.registIntentFilter(getApplicationContext());
		}
        return mBinder;
	}
      
    @Override  
    public boolean onUnbind(Intent intent) {
    	if (mWifiMonitor != null){
    		//mWifiMonitor.unRegistIntentFilter(getApplicationContext());
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
    			}else{
    				if (mDownloadManager != null){
    					mDownloadManager.receiveData(bundle.getByteArray("CONTENT"),
    							bundle.getInt("SIZE"));
    				}
    			}
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

    public void updateServerAddr(String addr){
    	mServerAddr = addr;
    	if (mTcpClient != null){
    		mTcpClient.onDestroy();
    		mTcpClient = null;
    	}
   		mTcpClient = new TCPClient(Message.obtain(mHandler, SOCKET_EVENT), mServerAddr);
    	if (mDownloadManager == null){
    		mDownloadManager = new DownloadService();
    	}
    }
	
	public void connectServer(String number, String password){
		sendMessage(new LoginRequestMessage(number, password), 0);
	}
	
	public void addDownloadTask(DownloadTask task){
		if (mDownloadManager != null){
			mDownloadManager.addTask(task);
		}
	}

	public void updateDownloadInfo(String qid, String subid, int len){
		if (mDownloadManager != null){
			mDownloadManager.updateDownloadInfo(qid, subid, len);
		}
	}
	
	//TCP Socket use this function
	public void sendMessage(RequestMessage msg, int mode ){
		if (mTcpClient != null){
			mTcpClient.sendMessage(msg.ToPack());
		}
	}	
	
	//UDP Socket use this function
	public void sendMessage(RequestMessage msg, String destip){
		UDPSenderThread sender = new UDPSenderThread(destip, KingCAIConfig.mUDPPort, msg.ToPack());
		sender.start();
	}
	
	//multicast socket Socket use this function		
	public void sendMessage(RequestMessage msg) {
	    MulticastSenderThread sender = new MulticastSenderThread(KingCAIConfig.mMulticastClientGroupIP,
	    									KingCAIConfig.mImageReceivePort, msg.ToPack());
	    sender.start();
	}
	
    public String getLocalIPAddress(){
    	return mWifiMonitor.getLocalIPAddress();
    }
}

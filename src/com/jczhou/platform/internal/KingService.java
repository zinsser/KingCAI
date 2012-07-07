package com.jczhou.platform.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.jczhou.kingcai.messageservice.LoginFinishedMessage;
import com.jczhou.kingcai.messageservice.LoginRequestMessage;
import com.jczhou.kingcai.messageservice.QueryServerMessage;
import com.jczhou.kingcai.messageservice.RequestMessage;
import com.jczhou.platform.internal.UDPSocketReceiver;
import com.jczhou.platform.KingCAIConfig;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;

public class KingService extends Service{
    private static final String TAG = "KingService"; 
        
    private Thread mUDPReceiver = null;
    private Thread mMulticastReceiver = null;
    private Thread mImageReceiver = null;
    private Thread mTCPReceiver = null;
    
    private Socket mTCPTextChannelSocket = null;
    private String mServerIP = null;
    @Override  
    public void onCreate() {  
    	
    	mUDPReceiver = new Thread(new UDPSocketReceiver(this, KingCAIConfig.mUDPPort));
    	mTCPReceiver = new Thread(new TCPSocketReceiver(this, KingCAIConfig.mTextReceivePort));    	
		mImageReceiver = new Thread(new TCPSocketReceiver(this, KingCAIConfig.mImageReceivePort));
  
        super.onCreate();  
    }  
      
    @Override  
    public void onStart(Intent intent, int startId) {

		if (mUDPReceiver != null && !mUDPReceiver.isAlive()){
			mUDPReceiver.start();
		}
		
		if (mTCPReceiver != null && !mTCPReceiver.isAlive()){
			mTCPReceiver.start();
		}
		
		if (mMulticastReceiver != null && mMulticastReceiver.isAlive()){
			mMulticastReceiver.start();
		}
		
		if (mImageReceiver != null && mImageReceiver.isAlive()){
			mImageReceiver.start();
		}    	
        super.onStart(intent, startId);
    }  
      
    @Override  
    public void onDestroy() { 
		if (mUDPReceiver != null && !mUDPReceiver.isAlive()){
//			mUDPReceiver.stop();
		}
		if (mMulticastReceiver != null && mMulticastReceiver.isAlive()){
//			mMulticastReceiver.stop();
		}
		if (mImageReceiver != null && mImageReceiver.isAlive()){
//			mImageReceiver.stop();
		}
        super.onDestroy();
    } 
    
    //���ﶨ���һ��Binder�࣬����onBind()�з��������Activity�Ǳ߿��Ի�ȡ�� 
    private MyBinder mBinder = new MyBinder();      
	@Override
	public IBinder onBind(Intent intent) {
        return mBinder;
	}
	
	public class MyBinder extends Binder{
		public KingService getService(){
            return KingService.this;
        }  
    }      
      
    @Override  
    public boolean onUnbind(Intent intent) {  
        return super.onUnbind(intent);  
    }
    
    public void setTextChannelSocket(Socket channel){
    	mTCPTextChannelSocket = channel;
    }
    
    public void setBinaryChannelSocket(Socket channel){
    	//TODO:
    }

	public void queryServer(){
		//1��2 ��
		//3��UDPReceiver��ReciveMessage
		//4����Ϣ�����߳��յ�����������Ӧ��Ϣ�󣬽���Ϣת�������߳�
		//5������Э�������Ϣ[FinishIPTalking]���Է�
		sendMessage(new QueryServerMessage(getLocalIPAddress()));
	}
	
	public void connectServer(String number, String password){
		sendMessage(new LoginRequestMessage(number, password), 0);
	}
	
	public void setServerIPAddr(String serverip){
		mServerIP = serverip;
	}
	
	//TCP Socket use this function
	public void sendMessage(RequestMessage msg, int mode ){
		// ���ݷ�������ַ�Ͷ˿ں�ʵ����һ��Socketʵ��  
		Socket socket = null;
		try {
			socket = new Socket(mServerIP, KingCAIConfig.mTextSendPort);
			// ���ش��׽��ֵ��������������������͵����ݶ���  
			OutputStream out = socket.getOutputStream();  
			// ����������ʹӿ���̨���յ�����  
			out.write(msg.ToPack().getBytes());  
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
		 	e.printStackTrace();
		} finally {
			if (socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}  	
	}	
	
	//UDP Socket use this function
	public void sendMessage(RequestMessage msg, String destip){
		try {
			String rawMsg = msg.ToPack();
			DatagramSocket s = new DatagramSocket();
			InetAddress	local = InetAddress.getByName(destip);			
			DatagramPacket p = new DatagramPacket(rawMsg.getBytes(), rawMsg.getBytes().length,
								local, KingCAIConfig.mUDPPort);
			s.send(p);
			s.close();
		} catch (SocketException e){
			e.printStackTrace();
		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	//multicast socket Socket use this function		
	public void sendMessage(RequestMessage msg) {
	    try{
	    	String rawMsg = msg.ToPack();
	        int TTL = 4;
	        MulticastSocket multiSocket = new MulticastSocket();  
	        multiSocket.setTimeToLive(TTL);
	        InetAddress  GroupAddress = InetAddress.getByName(KingCAIConfig.mMulticastServerGroupIP);
	        DatagramPacket dp = new DatagramPacket(rawMsg.getBytes(), rawMsg.getBytes().length, 
	        					GroupAddress, KingCAIConfig.mMulticastServerCommonPort);
	        multiSocket.send(dp);
	        multiSocket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	//�ͻ������������Ե㷢��ͼƬ
	public void SendImage(RequestMessage msg, String destip) {
	    try{
	    	String rawMsg = msg.ToPack();
	        int TTL = 4;
	        MulticastSocket multiSocket = new MulticastSocket();  
	        multiSocket.setTimeToLive(TTL);
	        InetAddress  GroupAddress = InetAddress.getByName(destip);
	        DatagramPacket dp = new DatagramPacket(rawMsg.getBytes(), rawMsg.getBytes().length, 
	        					GroupAddress, KingCAIConfig.mUDPServerImagePort);
	        multiSocket.send(dp);
	        multiSocket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	//��������ͻ����㲥����ͼƬ
	public void SendImage(RequestMessage msg) {
	    try{
	    	String rawMsg = msg.ToPack();
	        int TTL = 4;
	        MulticastSocket multiSocket = new MulticastSocket();  
	        multiSocket.setTimeToLive(TTL);
	        InetAddress  GroupAddress = InetAddress.getByName(KingCAIConfig.mMulticastClientImageGroupIP);
	        DatagramPacket dp = new DatagramPacket(rawMsg.getBytes(), rawMsg.getBytes().length, 
	        					GroupAddress, KingCAIConfig.mImageReceivePort);
	        multiSocket.send(dp);
	        multiSocket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
    public String getLocalIPAddress(){
    	String ipAddr = null;
    	
    	WifiManager mWifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);   
    	if (mWifiMgr != null){
        	DhcpInfo dhcpInfo = mWifiMgr.getDhcpInfo();
        	if (dhcpInfo != null){
	        	ipAddr = ((dhcpInfo.ipAddress) & 0xff) + "."
	        			+ ((dhcpInfo.ipAddress >> 8) & 0xff) + "."
	        			+ ((dhcpInfo.ipAddress >> 16) & 0xff) + "."
	        			+ ((dhcpInfo.ipAddress >> 24) & 0xff);	        	
        	}
    	}
    	
    	return ipAddr;
    }
}

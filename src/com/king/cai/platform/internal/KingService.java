package com.king.cai.platform.internal;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.king.cai.AppEnumActivity;
import com.king.cai.messageservice.LoginRequestMessage;
import com.king.cai.messageservice.QueryServerMessage;
import com.king.cai.messageservice.RequestMessage;
import com.king.cai.platform.KingCAIConfig;
import com.king.cai.platform.internal.UDPSocketReceiver;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class KingService extends Service{
    private static final String TAG = "KingService"; 
        
    private Thread mUDPReceiver = null;
    private Thread mMulticastReceiver = null;
    private Thread mImageReceiver = null;
    private TCPSocketReceiver mImageReceiverRoutine = null;
    private Thread mTCPThread = null;
    private TCPSocketReceiver mReceiverRoutine = null;
    private Socket mTCPTextChannelSocket = null;

    private String mServerIP = null;
    
    @Override  
    public void onCreate() {  
    	
    	mUDPReceiver = new Thread(new UDPSocketReceiver(this, KingCAIConfig.mUDPPort));
    	mReceiverRoutine = new TCPSocketReceiver(this, KingCAIConfig.mTextReceivePort);
    	mTCPThread = new Thread(mReceiverRoutine);    	
    	mImageReceiverRoutine = new TCPSocketReceiver(this, KingCAIConfig.mImageReceivePort);
    	mImageReceiver = new Thread(mImageReceiverRoutine);

        super.onCreate();  
    }  
      
    @Override  
    public void onStart(Intent intent, int startId) {

		if (mUDPReceiver != null && !mUDPReceiver.isAlive()){
			mUDPReceiver.start();
		}
		
		if (mTCPThread != null && !mTCPThread.isAlive()){
			mTCPThread.start();
		}
		
		if (mMulticastReceiver != null && mMulticastReceiver.isAlive()){
			mMulticastReceiver.start();
		}
		
		if (mImageReceiver != null && mImageReceiver.isAlive()){
			mImageReceiver.start();
		}    	

   // 	setDefaultLauncher();
		super.onStart(intent, startId);
    }  
      
    @Override  
    public void onDestroy() { 
		if (mUDPReceiver != null && !mUDPReceiver.isAlive()){
//			mUDPReceiver.stop();
		}
		
		if (mReceiverRoutine != null 
				&& mTCPThread != null && !mTCPThread.isAlive()){
			mReceiverRoutine.stopRunner();
		}
		
		if (mMulticastReceiver != null && mMulticastReceiver.isAlive()){
//			mMulticastReceiver.stop();
		}
		if (mImageReceiver != null && mImageReceiver.isAlive()){
			mImageReceiverRoutine.stopRunner();
		}
        super.onDestroy();
    } 
    
    //这里定义吧一个Binder类，用在onBind()有方法里，这样Activity那边可以获取到 
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
    
    private void setDefaultLauncher(){
    	PackageManager pm = getPackageManager();

    	IntentFilter filter = new IntentFilter();
    	filter.addAction("android.intent.action.MAIN");
    	filter.addCategory("android.intent.category.HOME");
    	filter.addCategory("android.intent.category.DEFAULT"); 
    	Context context = getApplicationContext();
    	ComponentName component = new ComponentName(context.getPackageName(), 
    									AppEnumActivity.class.getName());
    	ComponentName[] components = new ComponentName[] {
    			new ComponentName("com.android.launcher", "com.android.launcher.Launcher"), 
    			component};

    	pm.clearPackagePreferredActivities("com.android.launcher");
    	pm.addPreferredActivity(filter, IntentFilter.MATCH_CATEGORY_EMPTY, components, component);    	
    }
    
    public static boolean runRootCommand(String command) {  
        Process process = null;  
        DataOutputStream os = null;  
            try {  
            process = Runtime.getRuntime().exec("su");  
            os = new DataOutputStream(process.getOutputStream());  
            os.writeBytes(command+"\n");  
            os.writeBytes("exit\n");  
            os.flush();  
            process.waitFor();  
            } catch (Exception e) {  
                    Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "+e.getMessage());  
                    return false;  
            }  
            finally {  
                    try {  
                            if (os != null) {  
                                    os.close();  
                            }  
                            process.destroy();  
                    } catch (Exception e) {  
                            // nothing  
                    }  
            }  
            return true;  
    }     
    
    public void setTextChannelSocket(Socket channel){
    	mTCPTextChannelSocket = channel;
    }
    
    public void setBinaryChannelSocket(Socket channel){
    	//TODO:
    }

	public void queryServer(){
		//1～2 见
		//3：UDPReceiver：ReciveMessage
		//4，消息接收线程收到服务器的响应消息后，将消息转发给主线程
		//5，发送协商完成消息[FinishIPTalking]到对方
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
		// 根据服务器地址和端口号实例化一个Socket实例  
		Socket socket = null;
		try {
			socket = new Socket(mServerIP, KingCAIConfig.mTextSendPort);
			// 返回此套接字的输出流，即向服务器发送的数据对象  
			OutputStream out = socket.getOutputStream();  
			// 向服务器发送从控制台接收的数据  
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

	//客户机向服务器点对点发送图片
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

	//服务器向客户机广播发送图片
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

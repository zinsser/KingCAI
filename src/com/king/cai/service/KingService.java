package com.king.cai.service;


import java.util.List;

import com.king.cai.KingCAIConfig;
import com.king.cai.common.KingCAIUtils;
import com.king.cai.message.ActiveMessage_ExplorerDirectory;
import com.king.cai.message.ActiveMessage_LoginComplete;
import com.king.cai.message.ActiveMessage_QueryComplete;
import com.king.cai.message.RequestMessage;
import com.king.cai.message.RequestMessage_Answer;
import com.king.cai.message.RequestMessage_ExplorerDirectory;
import com.king.cai.message.RequestMessage_ExplorerFile;
import com.king.cai.message.RequestMessage_Login;
import com.king.cai.message.RequestMessage_Logout;
import com.king.cai.message.RequestMessage_PaperSize;
import com.king.cai.message.RequestMessage_QueryServer;
import com.king.cai.service.UDPServerRunner;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class KingService extends Service{        
    private String mServerAddr = null;
    private String mActiveSSID = null;

    private TCPClient mTcpClient = null;
    private WifiMonitor mWifiMonitor = null;
    private MulticastReceiverRunner mMulticastReceiverRoutine = null;
    private boolean mReferenceVisible = false;
    private String mAnswers = null;
    
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
	//		mWifiMonitor.registIntentFilter(getApplicationContext());
		}
        return mBinder;
	}
      
    @Override  
    public boolean onUnbind(Intent intent) {
    	if (mWifiMonitor != null){
//    		mWifiMonitor.unRegistIntentFilter(getApplicationContext());
    	}
        return super.onUnbind(intent);  
    }
    
    @Override  
    public void onCreate() {  
		super.onCreate();
//		disableOtherApps();
		mWifiMonitor = null;
//		mWifiMonitor = new WifiMonitor(getApplicationContext(), 
//										Message.obtain(mHandler, WIFI_EVENT));
    }
    
    public static final int SOCKET_EVENT = 0;
    public static final int WIFI_EVENT = 1;

    
    private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
    		case SOCKET_EVENT:
    			Intent intent = new Intent(KingCAIConfig.SOCKET_EVENT_ACTION);
    			intent.putExtras(msg.getData());
    			getApplication().sendBroadcast(intent);	
    			break;
    		case WIFI_EVENT:
    			break;
    		}
    	}
    };

    public boolean isNetworkConnected(){
    	boolean bRet = true;
    	if (mWifiMonitor != null){
    		mWifiMonitor.isNetworkConnected();
    	}
    	return bRet;
    }
    
    public boolean hasUpdatedApp(){
    	boolean bRet = false;
    	
    	return bRet;
    }
    
    public void startScanSSID(Message msg){
    	if (mWifiMonitor != null){
    		mWifiMonitor.startScanSSID(msg);
    	}
    }
    
    public void connectSSID(String ssid){
    	mActiveSSID = ssid;
    	if (mWifiMonitor != null){
    		mWifiMonitor.connectToWifi(mActiveSSID);
    	}
    }
        
	public void queryServer(boolean bLocal){
		mLoginInfo.mOffline = bLocal;
		if (!bLocal){
			//0，学生终端和教师服务器连接到同一个SSID，组成无线局域网		
			//1，学生终端向局域网广播发送教师服务器查询消息，同时启动UDP服务器
			//2，教师服务器收到该消息后，将ip地址作为响应通过UDP消息返回
			//3，学生终端通过UDP接收到该响应后，更新本地的服务器IP地址
			//4，并启动TCPClient，通过TCP和教师服务器保持通信
			sendMessage(new RequestMessage_QueryServer(getLocalIPAddress(), "1.1.5", mActiveSSID));
			startUDPServer();
		}else{
			broadcastSimulatorEvent(ActiveMessage_QueryComplete.s_MsgTag+"127.0.0.1[versionerror]1.1.6[size]0[ssiderror]ok");
		}
	}
	
	private void broadcastSimulatorEvent(String msgData){
		Bundle bundle = new Bundle();
		bundle.putBoolean("Type", true);
		bundle.putString("Peer", "localhost");
		bundle.putByteArray("Content", msgData.getBytes());
		
		Message innerMessage = mHandler.obtainMessage(KingService.SOCKET_EVENT);
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();
	}
	
	public boolean requestPaperSize(){
		if (mAnswers == null){
			sendMessage(new RequestMessage_PaperSize(), 0);
		}
		
		return mAnswers == null;
	}
	
	public void commitAnswers(String answers){
		sendMessage(new RequestMessage_Answer(RequestMessage_Answer.s_NormalCommitMsgTag, answers), 0);
		mAnswers = answers;
	}
	
	public String getLastAnswerAtLocal(){
		return mAnswers;
	}
	
	public void setReferenceVisible(boolean visible){
		mReferenceVisible = visible;
	}
	
	public boolean isReferenceVisible(){
		return mReferenceVisible;
	}
	
	private void startUDPServer(){
		UDPServerRunner udpReceiverRoutine = new UDPServerRunner(mHandler, KingCAIConfig.mUdpPort);
		new Thread(udpReceiverRoutine).start();
	}

	private void startMulticastServer(){
		mMulticastReceiverRoutine = new MulticastReceiverRunner(mHandler, 
											KingCAIConfig.mMulticastClientGroupIP, KingCAIConfig.mMulticastClientCommonPort);
		new Thread(mMulticastReceiverRoutine).start();
	}
	
    public void updateServer(String addr, String ssid){
    	
    	stopClientSocket();
    	if (addr != null){
	    	mServerAddr = addr;  
	    	if (!mServerAddr.equals("localhost")){
		   		mTcpClient = new TCPClient(mHandler, mServerAddr);
		   		startMulticastServer();
	    	}
    	}
    			
    }
    
    private void stopClientSocket(){
    	if (mTcpClient != null){
    		mTcpClient.onDestroy();
    		mTcpClient = null;
    	}
    	
		if (mMulticastReceiverRoutine != null){
			mMulticastReceiverRoutine.stopRunner();
			mMulticastReceiverRoutine = null;
		}    	
    }
    
    public void getExplorerDirectory(){
    	if (!KingCAIUtils.isSimulatorDebug()){
    		sendMessage(new RequestMessage_ExplorerDirectory(), 0);
    	}else{
    		String activeMessage = ActiveMessage_ExplorerDirectory.s_MsgTag;
    		//<File>xxxx<id>xxx<size>xxx<File>xxxx<id>xxx<size>xxx
    		activeMessage += "<file>xxx\\xx.bmp<id>0<size>2000";
    		activeMessage += "<file>yyy\\zzz\\kk.bmp<id>1<size>2000";
    		activeMessage += "<file>yyy\\zzz\\ww\\mm.bmp<id>2<size>2000";
			broadcastSimulatorEvent(activeMessage);
    	}
    }
    
    public void getExplorerFile(String id){
    	if (!KingCAIUtils.isSimulatorDebug()){
    		sendMessage(new RequestMessage_ExplorerFile(id), 0);
    	}else{
    		Bitmap image = BitmapFactory.decodeFile(
    				Environment.getExternalStorageDirectory().getPath() +"/test.jpg");
    		if (image != null){
    			broadcastSimulatorEvent(image.toString());
    		}
    	}
    }
	
    public static class LoginInfo{
    	public boolean mIsValid = false;
    	
    	public String mID = null;
    	public String mInfo = null;
    	public boolean mOffline = false;
    	public boolean mExceptionExit = false;
    	public String mSSID = null;
    	public String mServerAddr = null;
    	public String mPassword = null;
    }

    private LoginInfo mLoginInfo = new LoginInfo();
    private LoginInfo mLastLoginInfo = null;
    
    public void updateLastLoginInfo(){
    	mLastLoginInfo = new LoginInfo();
    	mLastLoginInfo.mID = mLoginInfo.mID;
    	mLastLoginInfo.mPassword = mLoginInfo.mPassword;
    	mLastLoginInfo.mOffline = mLoginInfo.mOffline;
    	mLastLoginInfo.mSSID = mLoginInfo.mSSID;
    }
    
    public LoginInfo getLastLoginInfo(){
    	return mLastLoginInfo;
    }
    
    public void updateLoginInfo(String studentInfo, boolean bExceptionExit){
		mLoginInfo.mIsValid = true;
		mLoginInfo.mInfo = studentInfo;
		mLoginInfo.mExceptionExit = bExceptionExit;
		mLastLoginInfo = null;
    }
    
    public boolean isLoginSuccess(){
    	return mLoginInfo.mIsValid;
    }
    
    public LoginInfo getLoginInfo(){
    	return mLoginInfo.mIsValid ? mLoginInfo : null;
    } 
    
	public void loginToServer(String number, String password){
		mLoginInfo.mID = number;
		mLoginInfo.mPassword = password;
		if (mLoginInfo.mOffline){
			mLoginInfo.mIsValid = true;
			broadcastSimulatorEvent(ActiveMessage_LoginComplete.s_MsgTag+"[pass]zhou.jincai[photosize]0");
		}else{
			sendMessage(new RequestMessage_Login(number, password), 0);
		}
		mAnswers = null;
	}

	public void logoutFromServer(){
		mLoginInfo.mIsValid = false;
		if (!mLoginInfo.mOffline){
			sendMessage(new RequestMessage_Logout(), 0);
		}
	}
	
	public void updateDownloadSize(int size){
		if (mTcpClient != null){
			mTcpClient.updateDownloadImageSize(size);
		}
	}
	
    public void updateExpectSize(int size){
    	if (mTcpClient != null){
    		mTcpClient.updateExpectSize(size);
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
	    MulticastSenderThread sender = new MulticastSenderThread(KingCAIConfig.mMulticastServerGroupIP,
	    									KingCAIConfig.mMulticastServerCommonPort, msg.ToPack());
	    sender.start();
	}
	
    private String getLocalIPAddress(){
    	return mWifiMonitor != null ? mWifiMonitor.getLocalIPAddress() : "0.0.0.0";
    }
    
    public static LoggerManager getLogService(){
    	return LoggerManager.getInstance();
    }
    
    public WifiMonitor getWifiService(){
    	return mWifiMonitor;
    }
    
    protected void disableOtherApps(){
    	PackageManager packageMgr = getPackageManager();
        List<PackageInfo> pkgInfos = packageMgr.getInstalledPackages(0/*PackageManager.GET_ACTIVITIES*/);
        String packageName = getPackageName(); 
        for(PackageInfo info : pkgInfos) {
        	if (!packageName.equals(info.packageName)
        		&& !"android".equals(info.packageName)){
        		packageMgr.setApplicationEnabledSetting(info.packageName, 
        								PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
        								PackageManager.DONT_KILL_APP);
        	}
        }
    }
}

package com.jczhou.kingcai;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.content.Context;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

//import android.os.IBinder;
import com.jczhou.platform.KingCAIConfig;
import com.jczhou.kingcai.messageservice.LoginFinishedMessage;
import com.jczhou.kingcai.messageservice.LoginRequestMessage;
import com.jczhou.kingcai.messageservice.QueryServerMessage;
import com.jczhou.kingcai.messageservice.RequestMessage;

public class SocketService{
	private final static SocketService s_instance = new SocketService();
	private String mLocalIP = "";
	private String mServerIP;

	private SocketService(){
	}
	
	public static SocketService getInstance(){
		return s_instance;
	}
	
	public void QueryServer(){
		//1～2 见
		//3：UDPReceiver：ReciveMessage
		//4，消息接收线程收到服务器的响应消息后，将消息转发给主线程
		//5，发送协商完成消息[FinishIPTalking]到对方
		SendMessage(new QueryServerMessage(mLocalIP));
	}
	
	public void ConnectServer(String number, String password){
		SendMessage(new LoginRequestMessage(number, password), mServerIP);
	}
	
	public void AnswerReady(String msg){
		SendMessage(new LoginFinishedMessage(msg), mServerIP);
	}
	
	//UDP Socket use this function
	public static void SendMessage(RequestMessage msg, String destip){
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
	public static void SendMessage(RequestMessage msg) {
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
	public static void SendImage(RequestMessage msg, String destip) {
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
	public static void SendImage(RequestMessage msg) {
	    try{
	    	String rawMsg = msg.ToPack();
	        int TTL = 4;
	        MulticastSocket multiSocket = new MulticastSocket();  
	        multiSocket.setTimeToLive(TTL);
	        InetAddress  GroupAddress = InetAddress.getByName(KingCAIConfig.mMulticastClientImageGroupIP);
	        DatagramPacket dp = new DatagramPacket(rawMsg.getBytes(), rawMsg.getBytes().length, 
	        					GroupAddress, KingCAIConfig.mMulticastClientImagePort);
	        multiSocket.send(dp);
	        multiSocket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
    public static String getLocalIPAddress(Context ctx){
    	String ipAddr = null;
    	
    	WifiManager mWifiMgr = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);   
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
    
    public void SetServerIPAddr(String ServerIP){
    	mServerIP = ServerIP;
    }

    public String GetServerIPAddr(){
    	return mServerIP;
    }
    
    public void ResetServerAddr(){
    	mServerIP = null;
    }
}

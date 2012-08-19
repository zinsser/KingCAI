package com.king.cai;

public class KingCAIConfig {
	public final static int EVENT_CLIENT_IP_REQUEST = 0;
	public final static int EVENT_CLIENT_IP_FINISH = 1;	
	public final static int EVENT_CLIENT_LOGIN = 2;
	public final static int EVENT_LOGIN_FINISHED = 3;
	public final static int EVENT_RECEIVE_MESSAGE = 4;
	
	public final static int EVENT_QUERY_COMPLETE = 5;
	public final static int EVENT_LOGIN_COMPLETE = 6;
	public final static int EVENT_PAPER_READY = 7;
	public final static int EVENT_NEW_QUESTION = 8;
	public final static int EVENT_NEW_IMAGE = 9;
	public final static int EVENT_CLEAN_PAPER = 10;
	public final static int EVENT_IMAGE_READY = 11;
	public final static int EVENT_REQUEST_IMAGE = 12;
	public final static int EVENT_NEW_PAPER = 13;
	
	public final static String SOCKET_EVENT_ACTION = "com.king.cai.socket.event";
	public static final String START_SERVICE_ACTION = "com.king.cai.service";

	
	public final static String mCharterSet = "GBK";

	public final static String StudentInfo = "StudentInfo";
	public final static String ServerIP = "ServerIP";
	public final static String SSID = "SSID";
	public final static String Offline = "Offline";
	
	public final static int mTextSendPort = 2011;	
	public final static int mUDPPort = 2012;
	public final static String mMulticastServerGroupIP = "224.0.0.100";
	public final static int mMulticastServerCommonPort = 2013;
	public final static String mMulticastClientGroupIP = "224.0.0.101";	
	public final static int mMulticastClientCommonPort = 2014;
	public final static String mMulticastClientImageGroupIP = "224.0.0.102";	
	public final static int mImageReceivePort = 2015;
//	public final static String mMulticastServerImageGroupIP = "224.0.0.103";	
	public final static int mUDPServerImagePort = 2016;
	public final static int mTextReceivePort = 2018;
}

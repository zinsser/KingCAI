package com.jczhou.kingcai;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration.KeyMgmt;

public class ServerInfo{
//	public static final int SECURITY_NONE = 0;
//	public static final int SECURITY_WEP = 1;
//	public static final int SECURITY_PSK = 2;
//	public static final int SECURITY_EAP = 3;
	
	private ScanResult mResultInfo = null;

	public ServerInfo(ScanResult result){
		mResultInfo = result;
	}

	public ScanResult getScanResult(){
		return mResultInfo;
	}

	public int getSecurity(){
		return getSecurity(mResultInfo);
	}

	public String getTitle(){
		return (mResultInfo == null) ? "" : mResultInfo.SSID;
	}
	
	private int getSecurity(ScanResult result){
		int security = KeyMgmt.NONE;
		if (result.capabilities.contains("WEP")){
			security = KeyMgmt.NONE;
		}else if (result.capabilities.contains("PSK")){
			security = KeyMgmt.WPA_PSK;
		}else if (result.capabilities.contains("EAP")){
			security = KeyMgmt.WPA_EAP;
		}
		
		return security;
	}
};
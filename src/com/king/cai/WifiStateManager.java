package com.king.cai;

import java.util.ArrayList;
import java.util.List;

import com.king.cai.ServerInfo;
import com.king.cai.common.KingCAIUtils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.content.BroadcastReceiver;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;
import java.util.HashMap;

public class WifiStateManager {
	private final static int WIFI_STATUS_SCANNING = 10;
	private final static int WIFI_STATUS_RESULT_RETRIVED = 11;	
	private final static int WIFI_STATUS_SCANED = 12;	
	
	private WifiStateListener mWifiStateListener = null;
	private HashMap<String, ServerInfo> mScanResults = new HashMap<String, ServerInfo>(); 
	private WifiManager mWifiService = null;
	private int mState = WifiManager.WIFI_STATE_UNKNOWN;
	private int mActiveNetworkID = -1;
    public BroadcastReceiver mReceiver = new WifiStateReceiver();
    private ConnectivityManager mConnManager;
    
	public interface WifiStateListener{
		public void  onScanResultChanged(final ArrayList<ServerInfo> serverInfos);
		public void  onServerInfoChanged(final String wifiInfo);
	}
	
	public WifiStateManager(Context ctx, WifiStateListener listener){
		mWifiStateListener = listener;
		mWifiService = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
		mConnManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);		
	}

	public void Register(Context ctx){
		IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ctx.registerReceiver(mReceiver, filter);
	}
	
	public void UnRegister(Context ctx){
    	RemoveWifiConfiguration();
    	ctx.unregisterReceiver(mReceiver);		
	}
	
    public void StartScanServer(){
		if (!mWifiService.isWifiEnabled() 
				&& mWifiService.getWifiState() != WifiManager.WIFI_STATE_ENABLING){
			mWifiService.setWifiEnabled(true);
		}else if (mState != WIFI_STATUS_SCANNING){
			mWifiService.startScan();
			mState = WIFI_STATUS_SCANNING;
		}
    }

    public void RemoveWifiConfiguration(){
    	if (mActiveNetworkID >= 0){
    		mWifiService.disconnect();
    		mWifiService.disableNetwork(mActiveNetworkID);
    		mWifiService.removeNetwork(mActiveNetworkID);
    		mWifiService.saveConfiguration();
    		mActiveNetworkID = -1;
    	}
    }

    public boolean ConnectToWifi(String ssid){
    	ServerInfo info = mScanResults.get(ssid);
    	boolean bRet = info != null && WifiConfiguration.KeyMgmt.NONE == info.getSecurity();
    	if (bRet && mState == WIFI_STATUS_SCANED){
        	WifiConfiguration wifiConfig = new WifiConfiguration();
        	wifiConfig.SSID = "\""+info.getScanResult().SSID+"\"";
        	wifiConfig.BSSID = info.getScanResult().BSSID;
        	wifiConfig.allowedKeyManagement.set(info.getSecurity());
        	ConnectToWIFIWithConfig(wifiConfig);
    	}
    	
    	return bRet;
    }
    
    private void ConnectToWIFIWithConfig(WifiConfiguration wifiConfig){
    	RemoveWifiConfiguration();//disconnect from current active access point
    	
    	mActiveNetworkID = mWifiService.addNetwork(wifiConfig);
    	mWifiService.enableNetwork(mActiveNetworkID, true);
    	mWifiService.saveConfiguration();
    	mWifiService.reconnect();
    }

    private void RetriveConnectionInfo(){
    	WifiInfo info = mWifiService.getConnectionInfo();
    	String IpInfo = KingCAIUtils.IPIntToString(info.getIpAddress());
    	Log.d("WifiStateManager", IpInfo);

    	mWifiStateListener.onServerInfoChanged(IpInfo);
    }
     
    private void RetriveScanResult(){
		List<ScanResult> results = mWifiService.getScanResults();
		if(results != null) {
			mScanResults.clear();
			for(ScanResult result : results) {
				mScanResults.put(result.SSID, new ServerInfo(result));
			}
			
			mWifiStateListener.onScanResultChanged(null);
			mState = WIFI_STATUS_SCANED;
		}
    }
    
    private abstract class Command extends Object{
    	public abstract void Execute(Intent intent);
    };
    
    private class  ScanResultCommand extends Command{
    	public void Execute(Intent intent){
			if (mState != WIFI_STATUS_RESULT_RETRIVED){
				RetriveScanResult();
				mState = WIFI_STATUS_RESULT_RETRIVED;
			}
    	}   	
    };

    private class  WifiStateChangedCommand extends Command{
    	public void Execute(Intent intent){
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 
										WifiManager.WIFI_STATE_UNKNOWN);
			if (wifiState == WifiManager.WIFI_STATE_ENABLED 
					&& mState != WIFI_STATUS_SCANNING){
				StartScanServer();
			}
    	}
    };
 
    private class  NetworkStateChangedCommand extends Command{
    	public void Execute(Intent intent){
			final NetworkInfo netInfo = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (netInfo != null && netInfo.isConnected()){
	    		RetriveConnectionInfo();  				
			}
    	}
    };    
    private class  ConnectStateChangedCommand extends Command{
    	public void Execute(Intent intent){
    		boolean success = false;
    		//获得网络连接服务
    		// State state = connManager.getActiveNetworkInfo().getState();
    		State state = mConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState(); // 获取网络连接状态
    		if (State.CONNECTED == state) { // 判断是否正在使用WIFI网络
    			success = true;
    		}
    		state = mConnManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState(); // 获取网络连接状态
    		if (State.CONNECTED != state) { // 判断是否正在使用GPRS网络
    			success = true;
    		}
    		if (!success) {
//    			Toast.makeText(mConnManager.this, "您的网络连接已中断", Toast.LENGTH_LONG).show();
    			//TODO:发送notification
    		}
    	}
    };     
    public class WifiStateReceiver extends BroadcastReceiver{
    	private HashMap<String, Command> mExecutorMap = new HashMap<String, Command>();
    	public WifiStateReceiver(){
    		mExecutorMap.put(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION, new ScanResultCommand());
    		mExecutorMap.put(WifiManager.WIFI_STATE_CHANGED_ACTION, new WifiStateChangedCommand());
    		mExecutorMap.put(WifiManager.NETWORK_STATE_CHANGED_ACTION, new NetworkStateChangedCommand());
    		mExecutorMap.put(ConnectivityManager.CONNECTIVITY_ACTION, new ConnectStateChangedCommand());    		
    	}
    	
    	@Override
    	public void onReceive(Context context, Intent intent){
    		String action = intent.getAction();
    		if (mExecutorMap.containsKey(action)){
    			mExecutorMap.get(action).Execute(intent);
    		}
    	}
    };    
}

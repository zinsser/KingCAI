package com.king.cai.common;

import java.io.UnsupportedEncodingException;
import java.util.Set;


import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessage;
import com.king.cai.message.ActiveMessageManager;
import com.king.cai.message.RequestMessage;
import com.king.cai.service.KingService;
import com.king.cai.service.KingService.LoginInfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public abstract class ComunicableActivity extends Activity{
    private static final String TAG = "ComunicableActivity";
    
	private ServiceMessageReceiver mReceiver = null;
    private ServiceMessageHandler mHandler = null;
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	HandlerThread Comunicator = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
    	Comunicator.start();	
    	mHandler = new ServiceMessageHandler(Comunicator.getLooper());
    	mReceiver = new ServiceMessageReceiver(mHandler);
    }

    @Override
    public void onStart(){
    	super.onStart();
    	
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(KingCAIConfig.SOCKET_EVENT_ACTION);
    	registerReceiver(mReceiver, filter);

    	Intent intent = new Intent(KingCAIConfig.START_SERVICE_ACTION);  
		bindService(intent, mServiceChannel, Context.BIND_AUTO_CREATE);    	
    }
    
    @Override
    public void onStop(){
    	unregisterReceiver(mReceiver);
    	unbindService(mServiceChannel);    	

    	super.onStop();
    }
    
    private int getToastYPos(){
		DisplayMetrics dm = new DisplayMetrics(); 
		dm = getApplicationContext().getResources().getDisplayMetrics(); 
		return dm.heightPixels / 4;
    }
    
    public void showToast(String msg){
    	Toast toast = Toast.makeText(this, msg, 5000);
    	toast.setGravity(Gravity.CENTER, 0, 0 - getToastYPos());
    	toast.show();
    }

    public void showToast(int resId){
    	Toast toast = Toast.makeText(this, resId, 5000);
    	toast.setGravity(Gravity.CENTER, 0, 0 - getToastYPos());
    	toast.show();
    }    
    
    public void hiddenKeyboard(EditText destCtrl){
		InputMethodManager im =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		im.hideSoftInputFromWindow(destCtrl.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
    }
    
	protected abstract void doHandleInnerMessage(Message innerMessage);
	protected Handler mInnerMessageHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch (msg.what){
			case KingCAIConfig.EVENT_CLEAN_PAPER:
				finish();
				break;
			default:
				doHandleInnerMessage(msg);
				break;
			}
		}
	};
    
	public class ServiceMessageReceiver extends BroadcastReceiver{
    	private Handler mHostHandler = null;
    	
    	public ServiceMessageReceiver(Handler h){
    		mHostHandler = h;
    	}
    	
    	@Override
    	public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (KingCAIConfig.SOCKET_EVENT_ACTION.equals(action)){
        		Bundle bundle = new Bundle(intent.getExtras());
        		Message msg = mHostHandler.obtainMessage(ServiceMessageHandler.EVENT_SOCKET_MESSAGE);
        		msg.setData(bundle);
        		msg.sendToTarget();            	
            }
    	} 
    }
	
	public class ServiceMessageHandler extends Handler{
    	public static final int EVENT_SOCKET_MESSAGE = 0;
    	
		private ActiveMessageManager mActiveMsgMgr = new ActiveMessageManager();
		public ServiceMessageHandler(Looper loop){
			super(loop);
		}
		
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
			case EVENT_SOCKET_MESSAGE:
				Bundle bundle = msg.getData();
				boolean bTextMessage = bundle.getBoolean("Type");				
				if (bTextMessage){
					String peerip = bundle.getString("Peer");
					byte[] msgBuf = bundle.getByteArray("Content");
					
					onReceiveMessage(peerip, msgBuf);
				}else{
					Message newImageMessage = mInnerMessageHandler.obtainMessage(KingCAIConfig.EVENT_NEW_IMAGE);
					newImageMessage.setData(bundle);
					newImageMessage.sendToTarget();
				}
				break;
    		}
    	}
    	
    	private void onReceiveMessage(String peer, byte[] msgData){
			String msgContent = null;
			try {
				msgContent = new String(msgData, KingCAIConfig.mCharterSet).trim();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (msgContent != null){
	    		KingService.getLogService().addLog("Raw Msg Data:" + msgContent);
	    		ActiveMessage activeMsgExecutor = null;
	    		Set<String> keysets = mActiveMsgMgr.mActiveMsgMap.keySet();
	    		String[] keys = (String[])keysets.toArray(new String[keysets.size()]);
	    		for (int i = 0; i < keys.length; ++i){
	    			if (msgContent != null && msgContent.contains(keys[i])){
	    				activeMsgExecutor = mActiveMsgMgr.mActiveMsgMap.get(keys[i]).OnReceiveMessage(peer, msgContent);
	    				break;
	    			}
	    		}
	    		
	    		if (activeMsgExecutor != null){
	    			activeMsgExecutor.setCompleteHandler(mInnerMessageHandler);
	    			activeMsgExecutor.Execute();
	    		}else if (!msgContent.contains("[type]") && !msgContent.contains("[id]")
	    				&& msgData.length > 10){
	    			Bundle bundle = new Bundle();
	    			bundle.putByteArray("Content", msgData);   //二进制数据采用byte[]原始数据
	    			Message newImageMessage = mInnerMessageHandler.obtainMessage(KingCAIConfig.EVENT_NEW_IMAGE);
					newImageMessage.setData(bundle);
					newImageMessage.sendToTarget();
	    		}	
			}
    	}    	
    }
	
	protected abstract void onServiceReady();	
    protected myServiceChannel mServiceChannel = new myServiceChannel();
    protected class myServiceChannel implements ServiceConnection{     
    	public KingService mKingService = null;
    	public void onServiceConnected(ComponentName name, IBinder service) { 
        	mKingService = ((KingService.MyBinder)service).getService();
        	onServiceReady();
        }
        
        public void onServiceDisconnected(ComponentName name) {  
        	mKingService = null;
        }
              
        public boolean isNetworkConnected(){
        	boolean bConnected = false;
        	if (mKingService != null){
        		bConnected = mKingService.isNetworkConnected();
        	}        	
        	return bConnected;
        }
        
        public void startScanSSID(Message msg){
        	if (mKingService != null){
        		mKingService.startScanSSID(msg);
        	}
        }
        
        public void connectSSID(String ssid){
        	if (mKingService != null){
        		mKingService.connectSSID(ssid);
        	}
        }

        public void sendMessage(RequestMessage msg, int mode){
        	if (mKingService != null){
        		mKingService.sendMessage(msg, mode);
        	}
        }
        
        public void sendMessage(RequestMessage msg){
        	if (mKingService != null){
            	mKingService.sendMessage(msg);        		
        	}        	
        }
        
        public void queryServer(boolean bLocal){
        	if (mKingService != null){
        		mKingService.queryServer(bLocal);
        	}
        }
        
        public void loginToServer(String number, String password){
        	if (mKingService != null){
        		mKingService.loginToServer(number, password);
        	}
        }
        
        public boolean requestPaperSize(){
        	boolean bRet = false;
        	if (mKingService != null){
        		bRet = mKingService.requestPaperSize();
        	}
        	
        	return bRet;
        }

        public void commitAnswers(String answers){
        	if (mKingService != null){
        		mKingService.commitAnswers(answers);
        	}
        }
        
        public boolean isAnswerCommited(){
        	boolean bRet = false;
        	if (mKingService != null){
        		bRet = mKingService.isRestricted();
        	}
        	return bRet;
        }
        
        public void setReferenceVisible(boolean visible){
        	if (mKingService != null){
        		mKingService.setReferenceVisible(visible);
        	}
        }
        
        public void logoutFromServer(){
        	if (mKingService != null){
        		mKingService.logoutFromServer();
        	}
        }
        
        public boolean isReferenceVisible(){
        	boolean bRet = false;
        	if (mKingService != null){
        		bRet = mKingService.isReferenceVisible();
        	}
        	
        	return bRet;
        }
        
        public String getLastAnswerAtLocal(){
        	String str = null;
        	if (mKingService != null){
        		str = mKingService.getLastAnswerAtLocal();
        	}
        	
        	return str;
        }
        
        public void updateServerInfo(String serverip, String ssid){
        	if (mKingService != null){
        		mKingService.updateServer(serverip, ssid);
        	}
        }
        
        public void updateDownloadInfo(int size){
        	if (mKingService != null){
        		mKingService.updateExpectSize(size);
        	}
        }
        
        public void updatePaperSize(Integer size){
        	if (mKingService != null){
        		mKingService.updateExpectSize(size);
        	}
        }
        
        public void updateLastLoginInfo(){
        	if (mKingService != null){
        		mKingService.updateLastLoginInfo();
        	}
        }
        
        public LoginInfo getLastLoginInfo(){
        	LoginInfo ret = null;
        	if (mKingService != null){
        		ret = mKingService.getLastLoginInfo();
        	}
        	
        	return ret;
        }
        
        public void updateLoginInfo(String studentInfo, boolean bExceptionExit){
        	if (mKingService != null){
        		mKingService.updateLoginInfo(studentInfo, bExceptionExit);
        	}
        }
        
        public LoginInfo getLoginInfo(){
        	LoginInfo bRet = null;
        	if (mKingService != null){
        		bRet = mKingService.getLoginInfo();
        	}
        	
        	return bRet;
        }
    };
}

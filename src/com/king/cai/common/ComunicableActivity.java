package com.king.cai.common;

import java.util.Set;


import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessage;
import com.king.cai.message.ActiveMessageManager;
import com.king.cai.message.RequestMessage;
import com.king.cai.service.KingService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
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
    public void onResume(){
    	super.onResume();
    	
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(KingCAIConfig.SOCKET_EVENT_ACTION);
    	registerReceiver(mReceiver, filter);

    	Intent intent = new Intent(KingCAIConfig.START_SERVICE_ACTION);  
		bindService(intent, mServiceChannel, Context.BIND_AUTO_CREATE);    	
    }
    
    @Override
    public void onPause(){
    	unregisterReceiver(mReceiver);
    	unbindService(mServiceChannel);    	

    	super.onPause();
    }
    
    private int getToastYPos(){
		DisplayMetrics dm = new DisplayMetrics(); 
		dm = getApplicationContext().getResources().getDisplayMetrics(); 
		return dm.heightPixels / 4;
    }
    
    public void showToast(String msg){
    	Toast toast = Toast.makeText(this, msg, 2000);
    	toast.setGravity(Gravity.CENTER, 0, 0 - getToastYPos());
    	toast.show();
    }

    public void showToast(int resId){
    	Toast toast = Toast.makeText(this, resId, 2000);
    	toast.setGravity(Gravity.CENTER, 0, 0 - getToastYPos());
    	toast.show();
    }    
    
	protected abstract void doHandleInnerMessage(Message innerMessage);
	private Handler mInnerMessageHandler = new Handler(){
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
					onReceiveMessage(peerip, new String(msgBuf));
				}else{
					Message newImageMessage = mInnerMessageHandler.obtainMessage(KingCAIConfig.EVENT_NEW_IMAGE);
					newImageMessage.setData(bundle);
					newImageMessage.sendToTarget();
				}
				break;
    		}
    	}
    	
    	private void onReceiveMessage(String peer, String MsgData){
    		KingService.getLogService().addLog("MessageHandler Raw Msg Data:" + MsgData);
    		ActiveMessage activeMsgExecutor = null;
    		Set<String> keysets = mActiveMsgMgr.mActiveMsgMap.keySet();
    		String[] keys = (String[])keysets.toArray(new String[keysets.size()]);
    		for (int i = 0; i < keys.length; ++i){
    			if (MsgData != null && MsgData.contains(keys[i])){
    				activeMsgExecutor = mActiveMsgMgr.mActiveMsgMap.get(keys[i]).OnReceiveMessage(peer, MsgData);
    				break;
    			}
    		}
    		
    		if (activeMsgExecutor != null){
    			activeMsgExecutor.setCompleteHandler(mInnerMessageHandler);
    			activeMsgExecutor.Execute();
    		}	
    	}    	
    }
	
	protected abstract void doServiceReady();	
    protected myServiceChannel mServiceChannel = new myServiceChannel();
    protected class myServiceChannel implements ServiceConnection{     
    	public KingService mKingService = null;
    	public void onServiceConnected(ComponentName name, IBinder service) { 
        	mKingService = ((KingService.MyBinder)service).getService();
        	doServiceReady();
        }
        
        public void onServiceDisconnected(ComponentName name) {  
        	mKingService = null;
        }
        
        public void startScanSSID(Bundle bundle){
        	if (mKingService != null){
        		mKingService.startScanSSID(bundle);
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
        
        public void queryServer(){
        	if (mKingService != null){
        		mKingService.queryServer();
        	}
        }
        
        public void connectServer(String number, String password){
        	if (mKingService != null){
        		mKingService.connectServer(number, password);
        	}
        }
        
        public void updateServerInfo(String serverip, String ssid){
        	if (mKingService != null){
        		mKingService.updateServer(serverip, ssid);
        	}
        }
        
        public void addDownloadTask(String qid, String imageIndex){
        	if (mKingService != null){
        		mKingService.addDownloadTask(qid, imageIndex);
        	}
        }
        
        public void updateDownloadInfo(String qid, String imageIndex, int size){
        	if (mKingService != null){
        		mKingService.updateDownloadSize(qid, imageIndex, size);
        	}
        }
        
        public void updatePaperSize(Integer size){
        	if (mKingService != null){
        		mKingService.updatePaperSize(size);
        	}
        }
    };
}

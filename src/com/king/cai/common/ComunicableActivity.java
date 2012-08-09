package com.king.cai.common;

import java.nio.ByteBuffer;
import java.util.Set;


import com.king.cai.KingCAIConfig;
import com.king.cai.messageservice.ActiveMessage;
import com.king.cai.messageservice.ActiveMessageManager;
import com.king.cai.messageservice.NewImageMessage;
import com.king.cai.messageservice.RequestMessage;
import com.king.cai.platform.CommonDefine;
import com.king.cai.platform.internal.KingService;

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

    	Intent intent = new Intent(CommonDefine.START_PLATFORM_ACTION);  
		bindService(intent, mServiceChannel, Context.BIND_AUTO_CREATE);    	
    }
    
    @Override
    public void onPause(){
    	unregisterReceiver(mReceiver);
    	unbindService(mServiceChannel);    	

    	super.onPause();
    }
    
    private int GetToastYPos(){
		DisplayMetrics dm = new DisplayMetrics(); 
		dm = getApplicationContext().getResources().getDisplayMetrics(); 
		return dm.heightPixels / 4;
    }
    
    public void showToast(String msg){
    	Toast toast = Toast.makeText(this, msg, 2000);
    	toast.setGravity(Gravity.CENTER, 0, 0-GetToastYPos());
    	toast.show();
    }

    public void showToast(int resId){
    	Toast toast = Toast.makeText(this, resId, 2000);
    	toast.setGravity(Gravity.CENTER, 0, 0-GetToastYPos());
    	toast.show();
    }    
    
	protected abstract void doHandleInnerMessage(Message innerMessage);
	private Handler mInnerMessageHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if (msg != null){
				doHandleInnerMessage(msg);
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
    	public static final int EVENT_TEXT_MESSAGE = 0;
    	public static final int EVENT_BINARY_MESSAGE = 1;
    	public static final int EVENT_SOCKET_MESSAGE = 2;
    	
		private ActiveMessageManager mActiveMsgMgr = new ActiveMessageManager();
		public ServiceMessageHandler(Looper loop){
			super(loop);
		}
		
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
			case EVENT_SOCKET_MESSAGE:{
				Bundle bundle = msg.getData();
				boolean bTextMessage = bundle.getBoolean("TYPE");
				String peerip = bundle.getString("PEER");
				byte[] msgBuf = bundle.getByteArray("CONTENT");
				if (bTextMessage){
					onReceiveMessage(peerip, new String(msgBuf));
				}else{
//					onReceiveImage(peerip, );
				}
				break;
			}
    		}
    	}
    	
    	private void onReceiveImage(String peer, String qid, byte[] msgData){
			ByteBuffer buf = ByteBuffer.allocate(msgData.length);
			buf.put(msgData);
			NewImageMessage.NewImageFunctor functor = new NewImageMessage.NewImageFunctor();
			NewImageMessage activeMsgExecutor = (NewImageMessage) functor.OnReceiveMessage(peer,  qid, buf.array());
			activeMsgExecutor.setCompleteHandler(mInnerMessageHandler);
			activeMsgExecutor.Execute();
    	}
    	
    	private void onReceiveMessage(String peer, String MsgData){
    		Log.d("MessageHandler", "Raw Msg Data:" + MsgData);
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
	
	protected void doServiceReady(){
		
	}	
	
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
        
        public void connectSSID(String ssid){
        	if (mKingService != null){
        		mKingService.connectSSID(ssid);
        	}
        }
        
        public void sendMessage(RequestMessage msg, String ip){
        	if (mKingService != null){
        		mKingService.sendMessage(msg, ip);
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
        
        public void setServerIPAddr(String serverip){
        	if (mKingService != null){
        		mKingService.updateServerAddr(serverip);
        	}
        }
    };
}

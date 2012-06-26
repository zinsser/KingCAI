package com.jczhou.kingcai.common;

import java.nio.ByteBuffer;
import java.util.Set;


import com.jczhou.kingcai.messageservice.ActiveMessage;
import com.jczhou.kingcai.messageservice.ActiveMessageManager;
import com.jczhou.platform.CommonDefine;
import com.jczhou.platform.internal.KingService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public abstract class ComunicableActivity extends Activity{

	private ServiceMessageReceiver mReceiver = null;
    private ServiceMessageHandler mHandler = null;
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mHandler = new ServiceMessageHandler();
    	mReceiver = new ServiceMessageReceiver(mHandler);
    	mEventProcessListener = getEventProcessListener();
    }
    
    public EventProcessListener getEventProcessListener(){
    	if (mEventProcessListener == null){
    		mEventProcessListener = doGetEventProcessListener();
    	}
    	
    	return mEventProcessListener;
    }
    
    protected abstract EventProcessListener doGetEventProcessListener();
    
    @Override
    public void onStart(){
    	super.onStart();
    	
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(CommonDefine.TEXT_MESSAG_FROM_SERVICE_ACTION);
    	filter.addAction(CommonDefine.BINARY_MESSAG_FROM_SERVICE_ACTION);
    	registerReceiver(mReceiver, filter);

    	Intent intent = new Intent(CommonDefine.START_PLATFORM_ACTION);  
		bindService(intent, mServiceChannel, Context.BIND_AUTO_CREATE);    	
    }
    
    @Override
    public void onStop(){
    	unregisterReceiver(mReceiver);
    	unbindService(mServiceChannel);    	
    	super.onStop();
    }    
    
    @Override
    public void onResume(){
    	super.onResume();
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    }
    
	public class ServiceMessageReceiver extends BroadcastReceiver{
    	private Handler mHostHandler = null;
    	
    	public ServiceMessageReceiver(Handler h){
    		mHostHandler = h;
    	}
    	
    	@Override
    	public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (CommonDefine.TEXT_MESSAG_FROM_SERVICE_ACTION.equals(action)){
        		Bundle bundle = new Bundle();
        		Message msg = mHostHandler.obtainMessage(ServiceMessageHandler.EVENT_TEXT_MESSAGE);
        		msg.setData(bundle);
        		msg.sendToTarget();
            }else if (CommonDefine.BINARY_MESSAG_FROM_SERVICE_ACTION.equals(action)){
        		Bundle bundle = new Bundle();
        		Message msg = mHostHandler.obtainMessage(ServiceMessageHandler.EVENT_BINARY_MESSAGE);
        		msg.setData(bundle);
        		msg.sendToTarget();            	
            }
    	} 
    }
	
	public class ServiceMessageHandler extends Handler{
    	public static final int EVENT_TEXT_MESSAGE = 0;
    	public static final int EVENT_BINARY_MESSAGE = 1;
		private ActiveMessageManager mActiveMsgMgr = new ActiveMessageManager();
		
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
    		case EVENT_TEXT_MESSAGE:{
    			Bundle bundle = msg.getData();
            	String peerip = bundle.getString("PEER");
            	String msgData = bundle.getString("CONTENT");    			
    			OnReceiveMessage(peerip, msgData);
    			break;
    		}
    		case EVENT_BINARY_MESSAGE:{
    			Bundle bundle = msg.getData();
            	String peerip = bundle.getString("PEER");
            	byte[] msgData = bundle.getByteArray("CONTENT");    			
    			OnReceiveImage(peerip, msgData);
    			break;
    		}
    		}
    	}
    	
    	private void OnReceiveImage(String peer, byte[] MsgData){
    		//TODO:
    	}
    	
    	private void OnReceiveMessage(String peer, String MsgData){
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
    			activeMsgExecutor.Execute(mEventProcessListener);
    		}	
    	}    	
    }
	
	protected KingService mKingService = null; 	
    private myServiceChannel mServiceChannel = new myServiceChannel();
    private class myServiceChannel implements ServiceConnection{     
        public void onServiceConnected(ComponentName name, IBinder service) { 
        	mKingService = ((KingService.MyBinder)service).getService();  
        }
        
        public void onServiceDisconnected(ComponentName name) {  
        	mKingService = null;
        }
    };
    
    protected EventProcessListener mEventProcessListener = null;    
    public interface EventProcessListener{
		public abstract void  onTalkingFinished(final String serverip);
		public abstract void  onLoginSuccess(final String studentinfo);
		public abstract void  onLoginFail();	
		public abstract void  onPaperTitleReceived(String title);	
		public abstract void  onNewQuestion(String answer, int type, String content);
		public abstract void  onCleanPaper();
		public abstract void  onNewImage(Integer id, ByteBuffer buf);
	}
}

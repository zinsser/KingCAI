package com.jczhou.platform.internal;

import com.jczhou.platform.internal.MulticastSocketReceiver;
import com.jczhou.platform.internal.UDPSocketReceiver;
import com.jczhou.platform.CommonDefine;
import com.jczhou.platform.KingCAIConfig;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

public class PlatformService extends Service{
    private static final String TAG = "SocketService";
    
    public static final String MESSAGE_FROM_UI_ACTION = "message.from.ui"; 
    
    private UIMessageHandler mUIMessageHandler = null;
    private MessageReceiver mUIMessageReceiver = null;
    private HandlerThread mUIComunicator = null;
    
    private Thread mUDPReceiver = null;
    private Thread mMulticastReceiver = null;
    private Thread mImageReceiver = null;
    
    @Override  
    public void onCreate() {  
    	mUIComunicator = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
    	mUIComunicator.start();	
    	mUIMessageHandler = new UIMessageHandler(mUIComunicator.getLooper());
    	mUIMessageReceiver = new MessageReceiver(mUIMessageHandler);
    	
    	mUDPReceiver = new Thread(new UDPSocketReceiver(this, KingCAIConfig.mUDPPort));
		mMulticastReceiver = new Thread(new MulticastSocketReceiver(this, 
								KingCAIConfig.mMulticastClientGroupIP, KingCAIConfig.mMulticastClientCommonPort));
/*		mImageReceiver =  new Thread(new MulticastSocketReceiver(mHandler, 
							KingCAIConfig.mMulticastClientImageGroupIP, KingCAIConfig.mMulticastClientImagePort));
							*/
		mImageReceiver = new Thread(new UDPSocketReceiver(this, KingCAIConfig.mMulticastClientImagePort));
  
        super.onCreate();  
    }  
      
    @Override  
    public void onStart(Intent intent, int startId) {
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(MESSAGE_FROM_UI_ACTION);
    	filter.addAction(Intent.ACTION_BOOT_COMPLETED);
    	registerReceiver(mUIMessageReceiver, filter);

		if (mUDPReceiver != null && !mUDPReceiver.isAlive()){
			mUDPReceiver.start();
		}
		
		if (mMulticastReceiver != null && mMulticastReceiver.isAlive()){
			mMulticastReceiver.start();
		}
		
		if (mImageReceiver != null && mImageReceiver.isAlive()){
			mImageReceiver.start();
		}    	
        super.onStart(intent, startId);
    }  
      
    @Override  
    public void onDestroy() { 
    	unregisterReceiver(mUIMessageReceiver);
		if (mUIComunicator != null && !mUIComunicator.isAlive()){
			mUIComunicator.stop();
		}
		if (mUDPReceiver != null && !mUDPReceiver.isAlive()){
			mUDPReceiver.stop();
		}
		if (mMulticastReceiver != null && mMulticastReceiver.isAlive()){
			mMulticastReceiver.stop();
		}
		if (mImageReceiver != null && mImageReceiver.isAlive()){
			mImageReceiver.stop();
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
		public PlatformService getService(){
            return PlatformService.this;
        }  
    }      
      
    @Override  
    public boolean onUnbind(Intent intent) {  
        return super.onUnbind(intent);  
    }
    
    public class MessageReceiver extends BroadcastReceiver{
    	private Handler mHostHandler = null;
    	
    	public MessageReceiver(Handler h){
    		mHostHandler = h;
    	}
    	
    	@Override
    	public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MESSAGE_FROM_UI_ACTION.equals(action)){
        		Bundle bundle = new Bundle();
        		Message msg = mHostHandler.obtainMessage(UIMessageHandler.EVENT_UI_MESSAGE);
        		msg.setData(bundle);
        		msg.sendToTarget();
            }else if (Intent.ACTION_BOOT_COMPLETED.equals(action)){
            	Log.d("PlatformReceiver", "Boot Complete");
            	context.startService(new Intent(CommonDefine.START_PLATFORM_ACTION));
            }
    	} 
    }
    
    public class UIMessageHandler extends Handler{
    	public static final int EVENT_UI_MESSAGE = 0;
    	
    	public UIMessageHandler(Looper looper){
    		super(looper);
    	}
    	
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
    		case EVENT_UI_MESSAGE:
    			break;
    		}
    	}
    }
}

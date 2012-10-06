package com.king.cai.message;

import android.os.Bundle;
import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessage_QueryComplete extends ActiveMessage{
	public final static String s_MsgTag = "[QueryResponse]";
	private String mSocketMessage = null;
	private String mPeerIP;
	public ActiveMessage_QueryComplete(String peer, String socketMessage){
		super(s_MsgTag);
		mSocketMessage = socketMessage;
		mPeerIP = peer;
	}
	
	public static class QueryResponseFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String socketMessage) {
			return new ActiveMessage_QueryComplete(peer, socketMessage);
		}
	}

	@Override
	public void Execute() {
		//ipaddr[versionerror]xxx[size]xx[ssiderror]xxx
		String subMessage = super.FromPack(mSocketMessage);
		if (subMessage.contains("[versionerror]") 
				&& subMessage.contains("[size]")){
			int versionPos = subMessage.indexOf("[versionerror]");
			int sizePos = subMessage.indexOf("[size]");
			int ssidPos = subMessage.length();
			if (subMessage.contains("[ssiderror]")){
				ssidPos = subMessage.indexOf("[ssiderror]");
			}
			String version = subMessage.substring(versionPos+"[versionerror]".length(), sizePos);
			String size = subMessage.substring(sizePos + "[size]".length(), ssidPos);
			Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_QUERY_COMPLETE);
			Bundle bundle = new Bundle();
			bundle.putString("Peer", mPeerIP);
			bundle.putString("Version", version);
			bundle.putString("Size", size);
			innerMessage.setData(bundle);
			innerMessage.sendToTarget();			
		}
	}
}

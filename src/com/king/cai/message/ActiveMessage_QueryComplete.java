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
		Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_QUERY_COMPLETE);
		Bundle bundle = new Bundle();
		bundle.putString("Peer", mPeerIP);
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();
	}
}

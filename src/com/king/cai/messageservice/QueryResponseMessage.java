package com.king.cai.messageservice;

import com.king.cai.common.ComunicableActivity.EventProcessListener;
import com.king.cai.messageservice.ActiveMessageManager.ActiveFunctor;

public class QueryResponseMessage extends ActiveMessage{
	public final static String s_MsgTag = "[QueryResponse]";
	private String mMsgPack = null;
	private String mPeerIP;
	public QueryResponseMessage(String peer, String RawMsg){
		super(s_MsgTag);
		mMsgPack = RawMsg;
		mPeerIP = peer;
	}
	
	public static class QueryResponseFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param) {
			return new QueryResponseMessage(peer, param);
		}
	}

	@Override
	public void Execute(EventProcessListener l) {
		l.onTalkingFinished(mPeerIP);
	}
}
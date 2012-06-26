package com.jczhou.kingcai.messageservice;

import com.jczhou.kingcai.common.ComunicableActivity.EventProcessListener;
import com.jczhou.kingcai.messageservice.ActiveMessageManager.ActiveFunctor;

public class PaperTitleMessage extends ActiveMessage{
	public final static String s_MsgTag = "[DispatchTitle]";
	private String mMsgPack = null;
	public PaperTitleMessage(String RawMsg){
		super(s_MsgTag);
		mMsgPack = RawMsg;
	}
	
	public static class PaperTitleFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new PaperTitleMessage(param);
		}
	}

	@Override
	public void Execute(EventProcessListener l) {
		String pack = super.FromPack(mMsgPack);
		l.onPaperTitleReceived(pack);
	}
}

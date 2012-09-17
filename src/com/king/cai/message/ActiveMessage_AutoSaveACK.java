package com.king.cai.message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessage_AutoSaveACK    extends ActiveMessage{
	public static final String s_MsgTag = "[AutoSaveACK]";
	private String mSocketMessage ;
	protected ActiveMessage_AutoSaveACK(String socketMessage) {
		super(s_MsgTag);
		mSocketMessage = socketMessage;
	}

	public static class AutoSaveACKFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new ActiveMessage_AutoSaveACK(param);
		}
	}

	@Override
	public void Execute() {
		mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_AUTOSAVE_ACK).sendToTarget();
	}
}

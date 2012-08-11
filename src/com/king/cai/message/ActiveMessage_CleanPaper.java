package com.king.cai.message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessage_CleanPaper  extends ActiveMessage{
	public final static String s_MsgTag = "[CleanPaper]";
	
	public ActiveMessage_CleanPaper(){
		super(s_MsgTag);
	}
	
	public static class CleanPaperFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new ActiveMessage_CleanPaper();
		}
	}

	@Override
	public void Execute() {
		mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_CLEAN_PAPER).sendToTarget();
//		l.onCleanPaper();
	}
}
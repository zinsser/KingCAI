package com.king.cai.message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class CleanPaperMessage  extends ActiveMessage{
	public final static String s_MsgTag = "[CleanPaper]";
	
	public CleanPaperMessage(){
		super(s_MsgTag);
	}
	
	public static class CleanPaperFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new CleanPaperMessage();
		}
	}

	@Override
	public void Execute() {
		mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_CLEAN_PAPER).sendToTarget();
//		l.onCleanPaper();
	}
}
package com.king.cai.messageservice;

import com.king.cai.common.ComunicableActivity.EventProcessListener;
import com.king.cai.messageservice.ActiveMessageManager.ActiveFunctor;

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
	public void Execute(EventProcessListener l) {
		l.onCleanPaper();
	}
}
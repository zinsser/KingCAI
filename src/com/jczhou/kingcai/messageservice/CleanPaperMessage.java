package com.jczhou.kingcai.messageservice;

import com.jczhou.kingcai.common.ComunicableActivity.EventProcessListener;
import com.jczhou.kingcai.messageservice.ActiveMessageManager.ActiveFunctor;

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
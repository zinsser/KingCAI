package com.king.cai.message;

import android.os.Bundle;
import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessage_LastAnswer   extends ActiveMessage{
	public final static String s_MsgTag = "[LastAnswer]";
	private String mSocketMessage;
	
	public ActiveMessage_LastAnswer(String msg){
		super(s_MsgTag);
		mSocketMessage = msg;
	}
	
	public static class LastAnswerFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new ActiveMessage_LastAnswer(param);
		}
	}

	@Override
	public void Execute() {
		//
		String subMsg = FromPack(mSocketMessage);
//		int lastPos = subMsg.indexOf('\\');
//		String retFromPC = subMsg.substring(0, lastPos);
		

		
		Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_LAST_ANSWER_COMPLETE);
		Bundle bundle = new Bundle();
		bundle.putString("LastAnswer", subMsg);
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();
	}
}
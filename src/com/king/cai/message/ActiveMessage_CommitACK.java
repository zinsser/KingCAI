package com.king.cai.message;

import android.os.Bundle;
import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessage_CommitACK extends ActiveMessage{
	public final static String s_MsgTag = "[CommitACK]";
	private String mSocketMessage;

	protected ActiveMessage_CommitACK(String msg) {
		super(s_MsgTag);
		mSocketMessage = msg;
	}
	
	public static class CommitACKFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new ActiveMessage_CommitACK(param);
		}
	}

	@Override
	public void Execute() {
		String subMsg = FromPack(mSocketMessage);
		//OK[ask]xxxx[id]xx@xxxx[id]xx
		int askPos = subMsg.indexOf("[ask]");
		int lastPos = subMsg.indexOf("\\");
		String retFromPC = subMsg.substring(0, askPos);
		String asks = subMsg.substring(askPos+"[ask]".length(), lastPos);
		Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_COMMIT_ACK);
		Bundle bundle = new Bundle();
		bundle.putBoolean("Result", retFromPC.equals("OK") ? true : false);
		bundle.putString("Ask", asks);
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();
	}
}

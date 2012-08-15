package com.king.cai.message;

import android.os.Bundle;
import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessage_PaperResponse extends ActiveMessage{
	public final static String s_MsgTag = "[PaperResponse]";
	private String mSocketMessage = null;
	public ActiveMessage_PaperResponse(String socketMessage){
		super(s_MsgTag);
		mSocketMessage = socketMessage;
	}	
	
	public static class PaperResponseFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String socketMessage){
			return new ActiveMessage_PaperResponse(socketMessage);
		}
	}

	@Override
	public void Execute() {
		//[PaperResponse]xxx
		String subMessage = super.FromPack(mSocketMessage);
		if (subMessage.length() > 0){
			String strSize = subMessage.substring(s_MsgTag.length(), subMessage.length());

			Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_PAPER_READY);
			Bundle bundle = new Bundle();
			bundle.putInt("Size", Integer.parseInt(strSize));
			innerMessage.setData(bundle);
			innerMessage.sendToTarget();			
		}
	}
}

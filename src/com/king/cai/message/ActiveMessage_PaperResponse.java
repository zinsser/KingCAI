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
		//[PaperResponse]xxx[count]xxx
		String subMessage = super.FromPack(mSocketMessage);
		if (subMessage.length() > 0){
			int lastPos = subMessage.indexOf('\\');
			int countPos = subMessage.indexOf("[count]");
			String strSize = subMessage.substring(0, countPos);
			String strCount = subMessage.substring(countPos + "[count]".length(), lastPos);
			
			Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_PAPER_READY);
			Bundle bundle = new Bundle();
			bundle.putString("Count", strCount);
			Integer size = 65535;
			try {
				size = Integer.parseInt(strSize);
			}catch (NumberFormatException e){
				e.printStackTrace();
			}finally{
				bundle.putInt("Size", size);
				innerMessage.setData(bundle);
				innerMessage.sendToTarget();
			}
		}
	}
}

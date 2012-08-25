package com.king.cai.message;

import android.os.Bundle;
import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessage_ImageResponse  extends ActiveMessage{
	public final static String s_MsgTag = "[ImageBC]";
	private String mSocketMessage = null;
	public ActiveMessage_ImageResponse(String socketMessage){
		super(s_MsgTag);
		mSocketMessage = socketMessage;
	}	
	
	public static class NewImageFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String socketMessage){
			return new ActiveMessage_ImageResponse(socketMessage);
		}
	}

	@Override
	public void Execute() {
		//[ImageBC]2000[id]xx[sub]xx
		String subMessage = super.FromPack(mSocketMessage);
		if (subMessage.contains("[id]") && subMessage.contains("[sub]")){
			int idPos = subMessage.indexOf("[id]");
			String strSize = subMessage.substring(0, idPos);
			int subIDPos = subMessage.indexOf("[sub]");
			String qid = subMessage.substring(idPos + "[id]".length(), subIDPos);
			int lastPos = subMessage.indexOf('\\');
			String imageIndex = subMessage.substring(subIDPos + "[sub]".length(), lastPos);
			
			Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_IMAGE_READY);
			Bundle bundle = new Bundle();
			bundle.putString("ID", qid);
			bundle.putString("Index", imageIndex);
			bundle.putInt("Size", Integer.parseInt(strSize));
			innerMessage.setData(bundle);
			innerMessage.sendToTarget();
		}
	}
}
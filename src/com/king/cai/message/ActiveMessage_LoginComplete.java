package com.king.cai.message;

import android.os.Bundle;
import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessage_LoginComplete extends ActiveMessage{
	public final static String s_MsgTag = "[LoginResponse]";
	private String mSocketMessage = null;
	public ActiveMessage_LoginComplete(String socketMessage){
		super(s_MsgTag);
		mSocketMessage = socketMessage;
	}
	
	public static class LoginResponseFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String socketMessage){
			return new ActiveMessage_LoginComplete(socketMessage);
		}
	}

	@Override
	public void Execute() {
		String pack = super.FromPack(mSocketMessage);
		Bundle bundle = new Bundle();
		//[pass]xxxx
		Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_LOGIN_COMPLETE);
		if (pack.contains("[pass]")){
			String studentInfo = pack.substring("[pass]".length(), pack.length());
			bundle.putBoolean("RESULT", true);
			bundle.putString("INFO", studentInfo);
		}else{
			bundle.putBoolean("RESULT", false);
		}
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();		
	}
}
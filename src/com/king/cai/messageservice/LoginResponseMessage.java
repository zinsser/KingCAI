package com.king.cai.messageservice;

import android.os.Bundle;
import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.messageservice.ActiveMessageManager.ActiveFunctor;

public class LoginResponseMessage extends ActiveMessage{
	public final static String s_MsgTag = "[LoginResponse]";
	private String mMsgPack = null;
	public LoginResponseMessage(String RawMsg){
		super(s_MsgTag);
		mMsgPack = RawMsg;
	}
	
	public static class LoginResponseFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new LoginResponseMessage(param);
		}
	}

	@Override
	public void Execute() {
		String pack = super.FromPack(mMsgPack);
		Bundle bundle = new Bundle();
		Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_LOGIN_COMPLETE);
		if (pack.contains("[pass]")){
			String studentInfo = pack.substring("[pass]".length(), pack.length());
			bundle.putBoolean("RESULT", true);
			bundle.putString("INFO", studentInfo);
//			l.onLoginSuccess(studentinfo);
		}else{
			bundle.putBoolean("RESULT", false);
//			l.onLoginFail();
		}
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();
		
	}
}
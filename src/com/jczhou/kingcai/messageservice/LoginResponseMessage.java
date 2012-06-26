package com.jczhou.kingcai.messageservice;

import com.jczhou.kingcai.common.ComunicableActivity.EventProcessListener;
import com.jczhou.kingcai.messageservice.ActiveMessageManager.ActiveFunctor;

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
	public void Execute(EventProcessListener l) {
		String pack = super.FromPack(mMsgPack);
		if (pack.contains("[pass]")){
			String studentinfo = pack.substring("[pass]".length(), pack.length());
			l.onLoginSuccess(studentinfo);
		}else{
			l.onLoginFail();
		}
	}
}
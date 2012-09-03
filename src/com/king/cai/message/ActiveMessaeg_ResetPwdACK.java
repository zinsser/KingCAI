package com.king.cai.message;

import android.os.Bundle;
import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessaeg_ResetPwdACK  extends ActiveMessage{
	public final static String s_MsgTag = "[PasswordACK]";
	private String mSocketMessage;
	
	public ActiveMessaeg_ResetPwdACK(String msg){
		super(s_MsgTag);
		mSocketMessage = msg;
	}
	
	public static class ResetPwdACKFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new ActiveMessaeg_ResetPwdACK(param);
		}
	}

	@Override
	public void Execute() {
		//ok\
		String subMsg = FromPack(mSocketMessage);
		int lastPos = subMsg.indexOf('\\');
		String retFromPC = subMsg.substring(0, lastPos);
		
		Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_RESET_PWDACK);
		Bundle bundle = new Bundle();
		bundle.putBoolean("Result", retFromPC.equals("OK") ? true : false);
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();
	}
}
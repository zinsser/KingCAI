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
		//[pass]xxxx[photosize]xxx
		//[id_error]
		//[password_error]
		//[answer_commit]
		//[auto_commit]xxxx
		//[id_exist]
		//[error_commit]xxx[photosize]xxx
		Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_LOGIN_COMPLETE);
		if (pack.contains("[pass]")){
			int photoSizePos = pack.indexOf("[photosize]");
			String studentInfo = pack.substring("[pass]".length(), photoSizePos);
			String photoSize = pack.substring(photoSizePos + "[photosize]".length(), pack.length());
			bundle.putBoolean("Result", true);
			bundle.putString("Info", studentInfo);
			bundle.putString("PhotoSize", photoSize);
			bundle.putString("ErrCause", "[pass]");
		}else if (pack.contains("[error_commit]")){
			String studentInfo = pack.substring("[auto_commit]".length(), pack.length());
			bundle.putBoolean("Result", true);
			bundle.putString("Info", studentInfo);
			bundle.putString("ErrCause", "[autocommit]");
		}else if (pack.contains("[id_error]")){
			bundle.putBoolean("Result", false);
			bundle.putString("ErrCause", "[id]");
		}else if (pack.contains("[password_error]")){
			bundle.putBoolean("Result", false);			
			bundle.putString("ErrCause", "[pwd]");
		}else if (pack.contains("[answer_commit]")){
			bundle.putBoolean("Result", false);
			bundle.putString("ErrCause", "[normalcommit]");
		}else if (pack.contains("[id_exist]")){
			bundle.putBoolean("Result", false);
			bundle.putString("ErrCause", "[id_exist]");
		}
		
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();		
	}
}
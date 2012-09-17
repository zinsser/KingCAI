package com.king.cai.message;

import android.os.Bundle;
import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessage_TestTimeout  extends ActiveMessage{
	public static final String s_MsgTag = "[BeforeEndTest]";
	private String mSocketMessage;
	
	protected ActiveMessage_TestTimeout(String socketMessage) {
		super(s_MsgTag);
		mSocketMessage = socketMessage;
	}

	public static class TestTimeoutFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new ActiveMessage_TestTimeout(param);
		}
	}
	
	
	@Override
	public void Execute() {
		String subMessage = super.FromPack(mSocketMessage);
		int lastPos = subMessage.indexOf("\\");
		String lastTime = subMessage.substring(0, lastPos);
		Bundle bundle = new Bundle();
		bundle.putString("LastTime", lastTime);
		Message message = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_TEST_TIMEOUT);
		message.setData(bundle);
		message.sendToTarget();
	}
}

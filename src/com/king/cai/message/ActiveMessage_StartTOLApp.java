package com.king.cai.message;

import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessage_StartTOLApp extends ActiveMessage{
	public final static String s_MsgTag = "[StartTOLApp]";
	private String mPeerIP;
	public ActiveMessage_StartTOLApp(String peer){
		super(s_MsgTag);
		mPeerIP = peer;
	}
	
	public static class TOLStartorFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param) {
			return new ActiveMessage_StartTOLApp(peer);
		}
	}

	@Override
	public void Execute() {
	}
}
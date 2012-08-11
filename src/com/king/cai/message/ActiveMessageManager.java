package com.king.cai.message;

import java.util.HashMap;

public class ActiveMessageManager {
	public HashMap<String, ActiveFunctor> mActiveMsgMap = new HashMap<String, ActiveFunctor>();
	public ActiveMessageManager(){
		mActiveMsgMap.put(ActiveMessage_QueryComplete.s_MsgTag, new ActiveMessage_QueryComplete.QueryResponseFunctor());		
		mActiveMsgMap.put(ActiveMessage_LoginComplete.s_MsgTag, new ActiveMessage_LoginComplete.LoginResponseFunctor());
		mActiveMsgMap.put(ActiveMessage_NewQuestion.s_MsgTag, new ActiveMessage_NewQuestion.NewQuestionFunctor());
		mActiveMsgMap.put(ActiveMessage_CleanPaper.s_MsgTag, new ActiveMessage_CleanPaper.CleanPaperFunctor());
		mActiveMsgMap.put(ActiveMessage_ImageResponse.s_MsgTag, new ActiveMessage_ImageResponse.NewImageFunctor());
		mActiveMsgMap.put(ActiveMessage_StartTOLApp.s_MsgTag, new ActiveMessage_StartTOLApp.TOLStartorFunctor());
	}
	
	public static abstract class ActiveFunctor {
		public abstract ActiveMessage OnReceiveMessage(String peer, String param);
	}
}

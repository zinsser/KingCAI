package com.king.cai.message;

import java.util.HashMap;

public class ActiveMessageManager {
	public HashMap<String, ActiveFunctor> mActiveMsgMap = new HashMap<String, ActiveFunctor>();
	public ActiveMessageManager(){
		mActiveMsgMap.put(QueryResponseMessage.s_MsgTag, new QueryResponseMessage.QueryResponseFunctor());		
		mActiveMsgMap.put(LoginResponseMessage.s_MsgTag, new LoginResponseMessage.LoginResponseFunctor());
		mActiveMsgMap.put(NewQuestionMessage.s_MsgTag, new NewQuestionMessage.NewQuestionFunctor());
		mActiveMsgMap.put(CleanPaperMessage.s_MsgTag, new CleanPaperMessage.CleanPaperFunctor());
		mActiveMsgMap.put(NewImageMessage.s_MsgTag, new NewImageMessage.NewImageFunctor());		
	}
	
	public static abstract class ActiveFunctor {
		public abstract ActiveMessage OnReceiveMessage(String peer, String param);
	}
}

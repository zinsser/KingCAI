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
		mActiveMsgMap.put(ActiveMessage_PaperResponse.s_MsgTag, new ActiveMessage_PaperResponse.PaperResponseFunctor());		
		mActiveMsgMap.put(ActiveMessaeg_ResetPwdACK.s_MsgTag, new ActiveMessaeg_ResetPwdACK.ResetPwdACKFunctor());
		mActiveMsgMap.put(ActiveMessage_CommitACK.s_MsgTag, new ActiveMessage_CommitACK.CommitACKFunctor());
		mActiveMsgMap.put(ActiveMessage_LastAnswer.s_MsgTag, new ActiveMessage_LastAnswer.LastAnswerFunctor());				
	}
	
	public static abstract class ActiveFunctor {
		public abstract ActiveMessage OnReceiveMessage(String peer, String param);
	}
}

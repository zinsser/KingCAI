package com.jczhou.kingcai.messageservice;

import com.jczhou.kingcai.common.ComunicableActivity.EventProcessListener;
import com.jczhou.kingcai.messageservice.ActiveMessageManager.ActiveFunctor;

public class NewQuestionMessage  extends ActiveMessage{
	public final static String s_MsgTag = "[QuestionBC]";
	private String mMsgPack = null;
	private String mPeerIP;
	public NewQuestionMessage(String peer, String RawMsg){
		super(s_MsgTag);
		mMsgPack = RawMsg;
		mPeerIP = peer;
	}
	
	public static class NewQuestionFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new NewQuestionMessage(peer, param);
		}
	}

	@Override
	public void Execute(EventProcessListener l) {
		String pack = super.FromPack(mMsgPack);
		String[] questions = pack.split("@");
		for (String question : questions){
			//[answer]xxx[type]x[content]xxx
			//[id]xx[answer]xxx[type]x[image]1/0[content]xxx
			if (question.contains("[type]") && question.contains("[content]")
					&& question.contains("[answer]")){
				int TypePos = question.indexOf("[type]");
				String answer = question.substring("[answer]".length(), TypePos);
				int type = Integer.parseInt(question.substring(TypePos+"[type]".length(), TypePos+"[type]".length()+1));
				int ContentPos = question.indexOf("[content]");
				String content = question.substring(ContentPos + "[content]".length(), question.length());
				
				l.onNewQuestion(answer, type, content);
			}
		}
	}
}
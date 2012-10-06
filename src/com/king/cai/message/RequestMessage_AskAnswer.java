package com.king.cai.message;

public class RequestMessage_AskAnswer extends RequestMessage{
	private final static String s_MsgTag = "[AnswerAsk]";
	private String mAskAnswer;	//id@id@
	
	public RequestMessage_AskAnswer(String askAnswer){
		super(s_MsgTag);
		mAskAnswer = askAnswer;
	}
	
	@Override
	public String Pack(){
		return mAskAnswer;
	}
}
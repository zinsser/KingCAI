package com.king.cai.message;

public class RequestMessage_Answer extends RequestMessage{
	private final static String s_MsgTag = "[AnswerCommit]";
	private String mAnswerString;
	
	public RequestMessage_Answer(String answerString){
		super(s_MsgTag);
		mAnswerString = answerString;
	}
	
	@Override
	public String Pack(){
		return mAnswerString;
	}
}
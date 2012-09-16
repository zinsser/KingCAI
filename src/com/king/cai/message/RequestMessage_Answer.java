package com.king.cai.message;

public class RequestMessage_Answer extends RequestMessage{
	public final static String s_NormalCommitMsgTag = "[AnswerCommit]";
	public final static String s_ExceptionCommitMsgTag = "[AutoCommit]";
	
	private String mAnswerString;
	
	public RequestMessage_Answer(String tag, String answerString){
		super(tag);
		mAnswerString = answerString;
	}
	
	@Override
	public String Pack(){
		return mAnswerString;
	}
}
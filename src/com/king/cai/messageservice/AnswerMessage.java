package com.king.cai.messageservice;

public class AnswerMessage extends RequestMessage{
	private final static String s_MsgTag = "[AnswerCommit]";
	private String mAnswerString;
	
	public AnswerMessage(String answerString){
		super(s_MsgTag);
		mAnswerString = answerString;
	}
	
	@Override
	public String Pack(){
		return mAnswerString;
	}
}
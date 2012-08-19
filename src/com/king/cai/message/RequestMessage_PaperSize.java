package com.king.cai.message;

public class RequestMessage_PaperSize extends RequestMessage{
	private final static String s_MsgTag = "[PaperRequestSize]";
	
	public RequestMessage_PaperSize(){
		super(s_MsgTag);
	}
	
	@Override
	public String Pack(){
		return "Request Question";
	}
}
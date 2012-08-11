package com.king.cai.message;

public class RequestMessage_Paper extends RequestMessage{
	private final static String s_MsgTag = "[PaperRequest]";
	
	public RequestMessage_Paper(){
		super(s_MsgTag);
	}
	
	@Override
	public String Pack(){
		return "Request Paper";
	}
}
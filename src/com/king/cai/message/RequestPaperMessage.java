package com.king.cai.message;

public class RequestPaperMessage extends RequestMessage{
	private final static String s_MsgTag = "[PaperRequest]";
	
	public RequestPaperMessage(){
		super(s_MsgTag);
	}
	
	@Override
	public String Pack(){
		return "Request Paper";
	}
}
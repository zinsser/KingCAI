package com.jczhou.kingcai.messageservice;

public class LogoutRequestMessage extends RequestMessage{
	private final static String s_MsgTag = "[LogoutRequest]";
	
	public LogoutRequestMessage(){
		super(s_MsgTag);
	}
	
	@Override
	public String Pack(){
		return "EXIT";
	}
}

package com.king.cai.message;

public class RequestMessage_Logout extends RequestMessage{
	private final static String s_MsgTag = "[LogoutRequest]";
	
	public RequestMessage_Logout(){
		super(s_MsgTag);
	}
	
	@Override
	public String Pack(){
		return "EXIT";
	}
}

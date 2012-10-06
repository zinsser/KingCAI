package com.king.cai.message;

public class RequestMessage_UpdateApk extends RequestMessage{
	private final static String s_MsgTag = "[UpdateAPP]";
	
	public RequestMessage_UpdateApk(){
		super(s_MsgTag);
	}

	@Override
	public String Pack(){
		return "";
	}
}

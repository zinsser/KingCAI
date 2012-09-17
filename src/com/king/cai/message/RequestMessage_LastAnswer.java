package com.king.cai.message;

public class RequestMessage_LastAnswer  extends RequestMessage{
	public final static String s_MsgTag = "[LastAnswer]";
	
	public RequestMessage_LastAnswer(){
		super(s_MsgTag);
	}
	
	@Override
	public String Pack(){
		return "";
	}
}
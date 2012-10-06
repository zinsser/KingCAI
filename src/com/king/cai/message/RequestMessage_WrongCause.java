package com.king.cai.message;

public class RequestMessage_WrongCause extends RequestMessage{
	private final static String s_MsgTag = "[WrongCause]";
	private String mWrongCause;
	public RequestMessage_WrongCause(String cause){
		super(s_MsgTag);
		mWrongCause = cause;
	}
	
	@Override
	public String Pack(){
		return mWrongCause;
	}
}
package com.jczhou.kingcai.messageservice;

public class LoginFinishedMessage extends RequestMessage{
	private final static String s_MsgTag = "[LoginFinished]";
	private String mMsg;
	
	public LoginFinishedMessage(String msg){
		super(s_MsgTag);
		mMsg = msg;
	}
	
	@Override
	public String Pack(){
		return mMsg;
	}
}

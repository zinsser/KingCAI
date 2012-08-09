package com.king.cai.message;

public class LoginRequestMessage extends RequestMessage{
	private final static String s_MsgTag = "[LoginRequest]";
	private String mNumber;
	private String mPassword;
	
	public LoginRequestMessage(String number, String password){
		super(s_MsgTag);
		mNumber = number;
		mPassword = password;
	}
	
	@Override
	public String Pack(){
		return "[Number]" + mNumber + "[Password]" + mPassword;
	}
}
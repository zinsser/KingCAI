package com.king.cai.message;

public class RequestMessage_ResetPassword extends RequestMessage{
	private final static String s_MsgTag = "[ResetPassword]";
	private String mOldPassword;
	private String mNewPassword;
	
	public RequestMessage_ResetPassword(String oldPassword, String newPassword){
		super(s_MsgTag);
		mOldPassword = oldPassword;
		mNewPassword = newPassword;
	}
	
	@Override
	public String Pack(){
		return "[old]" + mOldPassword + "[new]" + mNewPassword;
	}
}


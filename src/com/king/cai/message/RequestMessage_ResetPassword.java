package com.king.cai.message;

public class RequestMessage_ResetPassword extends RequestMessage{
	private final static String s_MsgTag = "[ResetPassword]";
	private String mId;
	private String mOldPassword;
	private String mNewPassword;
	
	public RequestMessage_ResetPassword(String id, String oldPassword, String newPassword){
		super(s_MsgTag);
		mId = id;
		mOldPassword = oldPassword;
		mNewPassword = newPassword;
	}
	
	@Override
	public String Pack(){
		return "[id]" + mId + "[old]" + mOldPassword + "[new]" + mNewPassword;
	}
}


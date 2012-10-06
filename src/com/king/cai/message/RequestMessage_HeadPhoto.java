package com.king.cai.message;

public class RequestMessage_HeadPhoto extends RequestMessage{
	private final static String s_MsgTag = "[HeadPhoto]";
	private String mId;
	
	public RequestMessage_HeadPhoto(String Id){
		super(s_MsgTag);
		mId = Id;
	}
	
	@Override
	public String Pack(){
		return mId;
	}
}
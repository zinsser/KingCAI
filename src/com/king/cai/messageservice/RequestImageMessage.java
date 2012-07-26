package com.king.cai.messageservice;

public class RequestImageMessage extends RequestMessage{
	private final static String s_MsgTag = "[ImageRequest]";
	private Integer mId;
	public RequestImageMessage(Integer id){
		super(s_MsgTag);
		mId = id;
	}
	
	@Override
	public String Pack(){
		return ""+mId;
	}
}
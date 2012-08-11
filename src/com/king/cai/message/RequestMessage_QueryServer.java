package com.king.cai.message;

public class RequestMessage_QueryServer extends RequestMessage{
	private final static String s_MsgTag = "[QueryRequest]";
	private String mMsgContent;
	
	public RequestMessage_QueryServer(String msgContent){
		super(s_MsgTag);
		mMsgContent = msgContent;
	}
	
	@Override
	public String Pack(){
		return mMsgContent;
	}
}

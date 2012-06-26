package com.jczhou.kingcai.messageservice;

public class QueryServerMessage extends RequestMessage{
	private final static String s_MsgTag = "[QueryRequest]";
	private String mMsgContent;
	
	public QueryServerMessage(String msgContent){
		super(s_MsgTag);
		mMsgContent = msgContent;
	}
	
	@Override
	public String Pack(){
		return mMsgContent;
	}
}

package com.king.cai.message;

public class RequestMessage_ExplorerDirectory extends RequestMessage{
	public final static String s_MsgTag = "[ResourceRequest]";
	
	public RequestMessage_ExplorerDirectory(){
		super(s_MsgTag);
	}
	
	@Override
	public String Pack(){
		return "Explorer Directory";
	}
}
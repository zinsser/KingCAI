package com.king.cai.message;

public class RequestMessage_ExplorerFile  extends RequestMessage{
	public final static String s_MsgTag = "[FileRequest]";
	private String mFileId;
	public RequestMessage_ExplorerFile(String id){
		super(s_MsgTag);
		mFileId = id;
	}
	
	@Override
	public String Pack(){
		return mFileId;
	}
}
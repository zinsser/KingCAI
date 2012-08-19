package com.king.cai.message;

public class RequestMessage_ImageData extends RequestMessage{
	private final static String s_MsgTag = "[ImageRequestData]";
	private String mId;
	private String mImageIndex;
	public RequestMessage_ImageData(String id, String imageIndex){
		super(s_MsgTag);
		mId = id;
		mImageIndex = imageIndex;
	}
	
	@Override
	public String Pack(){
		return "[id]" + mId + "[sub]" + mImageIndex;
	}
}
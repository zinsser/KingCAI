package com.king.cai.message;

public class RequestMessage_Image extends RequestMessage{
	private final static String s_MsgTag = "[ImageRequest]";
	private String mId;
	private String mImageIndex;
	public RequestMessage_Image(String id, String imageIndex){
		super(s_MsgTag);
		mId = id;
		mImageIndex = imageIndex;
	}
	
	@Override
	public String Pack(){
		return "[id]" + mId + "[sub]" + mImageIndex;
	}
}
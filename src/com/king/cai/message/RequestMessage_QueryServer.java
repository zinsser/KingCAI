package com.king.cai.message;

public class RequestMessage_QueryServer extends RequestMessage{
	private final static String s_MsgTag = "[QueryRequest]";
	private String mMsgContent;
	private String mVersionCode;
	private String mSSID;
	
	public RequestMessage_QueryServer(String msgContent, String versionCode, String ssid){
		super(s_MsgTag);
		mMsgContent = msgContent;
		mVersionCode = versionCode;
		mSSID = ssid;
	}

	@Override
	public String Pack(){
		return mMsgContent+"[version]"+mVersionCode+"[ssid]"+mSSID;
	}
}

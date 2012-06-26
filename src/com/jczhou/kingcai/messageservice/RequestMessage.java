package com.jczhou.kingcai.messageservice;

public abstract class RequestMessage {
	private String mMessageTag;

	protected RequestMessage(String msgTag){
		mMessageTag = msgTag;
	}
	
	public String GetMessageTag(){
		return mMessageTag;
	}
	
	public String ToPack(){
		return mMessageTag + Pack();
	}
	
	protected abstract String Pack();
}

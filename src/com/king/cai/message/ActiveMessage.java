package com.king.cai.message;

import android.os.Handler;

public abstract class ActiveMessage {
	private String mMsgTag;
	protected Handler mCompleteHandler = null;
	protected ActiveMessage(String msgTag){
		mMsgTag = msgTag;
	}
	
	protected String FromPack(String msgPack){
		int tagIdx = msgPack.indexOf(mMsgTag);
		return msgPack.substring(tagIdx + mMsgTag.length(), msgPack.length());
	}	
	
	protected String GetMessageTag(){
		return mMsgTag;
	}

	public void setCompleteHandler(Handler h){
		mCompleteHandler = null;
		mCompleteHandler = h;
	}
	
	public abstract void Execute();
}

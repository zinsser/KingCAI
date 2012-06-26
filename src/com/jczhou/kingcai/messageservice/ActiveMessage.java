package com.jczhou.kingcai.messageservice;

import com.jczhou.kingcai.common.ComunicableActivity.EventProcessListener;


public abstract class ActiveMessage {
	private String mMsgTag;

	protected ActiveMessage(String msgTag){
		mMsgTag = msgTag;
	}

	public abstract void Execute(EventProcessListener l);
	
	protected String FromPack(String msgPack){
		return msgPack.substring(mMsgTag.length(), msgPack.length());
	}	
	
	protected String GetMessageTag(){
		return mMsgTag;
	}

}

package com.jczhou.kingcai.messageservice;

import java.nio.ByteBuffer;

import com.jczhou.kingcai.common.ComunicableActivity.EventProcessListener;
import com.jczhou.kingcai.messageservice.ActiveMessageManager.ActiveFunctor;

public class NewImageMessage  extends ActiveMessage{
	public final static String s_MsgTag = "[ImageBC]";
	private String mMsgPack = null;
	public NewImageMessage(String RawMsg){
		super(s_MsgTag);
		mMsgPack = RawMsg;
	}
	
	public static class NewImageFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new NewImageMessage(param);
		}
	}

	@Override
	public void Execute(EventProcessListener l) {
		String pack = super.FromPack(mMsgPack);		
		//[id]xxx[length]xxx[data]xxxx
		int lenPos = pack.indexOf("[length]");
		Integer id = Integer.parseInt(pack.substring("[id]".length(), lenPos));
		int dataPos = pack.indexOf("[data]");
		Integer len = Integer.parseInt(pack.substring(lenPos + "[length]".length(), dataPos));
		ByteBuffer dataBuf = ByteBuffer.allocate(len);
		String rawData = pack.substring(dataPos+"[data]".length(), pack.length());
		dataBuf.put(rawData.getBytes());
		l.onNewImage(id, dataBuf);		
	}
}
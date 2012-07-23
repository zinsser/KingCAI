package com.king.cai.messageservice;

import java.nio.ByteBuffer;

import com.king.cai.common.ComunicableActivity.EventProcessListener;
import com.king.cai.messageservice.ActiveMessageManager.ActiveFunctor;
import com.king.cai.platform.KingCAIConfig;

public class NewImageMessage  extends ActiveMessage{
	public final static String s_MsgTag = KingCAIConfig.NewImageMessageTag;
	private String mMsgPack = null;
	private ByteBuffer mImageBuffer = null;
	public NewImageMessage(String RawMsg){
		super(s_MsgTag);
		mMsgPack = RawMsg;
	}
	
	public NewImageMessage(byte[] data){
		super(s_MsgTag);
		mImageBuffer = null;
		mImageBuffer = ByteBuffer.allocate(data.length);
		mImageBuffer.put(data);
	}
	
	
	public static class NewImageFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String msgData){
			return new NewImageMessage(msgData);
		}
		
		public ActiveMessage OnReceiveMessage(String peer, byte[] param){
			return new NewImageMessage(param);
		}
	}

	@Override
	public void Execute(EventProcessListener l) {
		l.onNewImage(0, mImageBuffer);
	}
}
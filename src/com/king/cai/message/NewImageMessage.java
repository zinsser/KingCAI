package com.king.cai.message;

import java.nio.ByteBuffer;

import android.os.Bundle;
import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class NewImageMessage  extends ActiveMessage{
	public final static String s_MsgTag = KingCAIConfig.NewImageMessageTag;
	private String mMsgPack = null;
	private ByteBuffer mImageBuffer = null;
	private String mQID = null;
	public NewImageMessage(String RawMsg){
		super(s_MsgTag);
		mMsgPack = RawMsg;
	}
	
	public NewImageMessage(String qid, byte[] data){
		super(s_MsgTag);
		mQID = qid;
		mImageBuffer = null;
		mImageBuffer = ByteBuffer.allocate(data.length);
		mImageBuffer.put(data);
	}
	
	
	public static class NewImageFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String msgData){
			return new NewImageMessage(msgData);
		}
		
		public ActiveMessage OnReceiveMessage(String peer, String qid, byte[] param){
			return new NewImageMessage(qid, param);
		}
	}

	@Override
	public void Execute() {
		Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_NEW_IMAGE);
		Bundle bundle = new Bundle();
		bundle.putString("QUESTION", mQID);
		bundle.putByteArray("DATA", mImageBuffer.array());
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();
//		l.onNewImage(mQID, mImageBuffer);
	}
}
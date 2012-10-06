package com.king.cai.message;

import android.os.Bundle;
import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessage_ExplorerDirectory extends ActiveMessage{
	public final static String s_MsgTag = "[ResourceResponse]";
	private String mSocketMessage;
	
	public ActiveMessage_ExplorerDirectory(String msg){
		super(s_MsgTag);
		mSocketMessage = msg;
	}
	
	public static class ExplorerDirectoryFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new ActiveMessage_ExplorerDirectory(param);
		}
	}

	@Override
	public void Execute() {
		String subMsg = FromPack(mSocketMessage);
		//¡¾tag¡¿<File>xxxx<id>xxx<size>xxx<File>xxxx<id>xxx<size>xxx
		String[] files = subMsg.split("<file>");
		for (String file : files){
			if (file.contains("<size>") && file.contains("<id>")){
				int idPos = file.indexOf("<id>");
				int sizePos = file.indexOf("<size>");
				String name = file.substring(0, idPos);
				String id = file.substring(idPos+"<id>".length(), sizePos);
				String size = file.substring(sizePos + "<size>".length(), file.length());
				
				Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_EXPLORER_FILE_READY);
				Bundle bundle = new Bundle();
				bundle.putString("Name", name);
				bundle.putString("Id", id);
				bundle.putString("Size", size);
				innerMessage.setData(bundle);
				innerMessage.sendToTarget();
			}
		}
		
		mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_EXPLORER_DIRECTORY_READY).sendToTarget();
	}
}
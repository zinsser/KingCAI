package com.king.cai.message;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;

import com.king.cai.KingCAIConfig;
import com.king.cai.message.ActiveMessageManager.ActiveFunctor;

public class ActiveMessage_NewQuestion  extends ActiveMessage{
	public final static String s_MsgTag = "[QuestionBC]";
	private String mSocketMessage = null;
	private String mPeerIP;
	public ActiveMessage_NewQuestion(String peer, String socketMessage){
		super(s_MsgTag);
		mSocketMessage = socketMessage;
		mPeerIP = peer;
	}
	
	public static class NewQuestionFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String socketMessage){
			return new ActiveMessage_NewQuestion(peer, socketMessage);
		}
	}

	@Override
	public void Execute() {
		Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_NEW_PAPER);
		Bundle bundle = new Bundle();
		bundle.putString("Paper", mSocketMessage);
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();		
		new QuestionParseTask().execute(super.FromPack(mSocketMessage));
	}
	
	public class ProgressObject{
		String mID;
		int mType;
		String mReference;
		String mContent;
		int mImageCount;
		ProgressObject(String id, int type, String reference, String content, int imageCount){
			mID = id;
			mType = type;
			mReference = reference;
			mContent = content;
			mImageCount = imageCount;
		}
	}
	
	public class QuestionParseTask extends  AsyncTask<String, ProgressObject, String> {
		@Override
		protected String doInBackground(String... packs) {
			for (String subpack : packs){
				String[] questions = subpack.split("@");

				for (String question : questions){
					//[id]xx[answer]xxx[type]x[image]xx[content]xxx
					if (question.contains("[type]") && question.contains("[content]")
							&& question.contains("[answer]") 
							&& question.contains("[id]") && question.contains("[image]")){
						int answerPos = question.indexOf("[answer]");
						int TypePos = question.indexOf("[type]");
						int imagePos = question.indexOf("[image]");
						int contentPos = question.indexOf("[content]");
						String qId = question.substring("[id]".length(), answerPos);
						String answer = question.substring(answerPos+"[answer]".length(), TypePos);
						int type = Integer.parseInt(question.substring(TypePos+"[type]".length(), TypePos+"[type]".length()+1));
						String imageCount = question.substring(imagePos + "[image]".length(), contentPos);
						String content = question.substring(contentPos + "[content]".length(), question.length());
						publishProgress(new ProgressObject(qId, type, answer, content, Integer.parseInt(imageCount)));
					}
				}
			}
			return null;
		}
		@Override  
		protected void onPostExecute(String result) {  
		}  
		
		@Override  
		protected void onPreExecute() {  
		}  
		
		@Override  
		protected void onProgressUpdate(ProgressObject... values) {  
			for (ProgressObject obj : values){
				Message innerMessage = mCompleteHandler.obtainMessage(KingCAIConfig.EVENT_NEW_QUESTION);
				Bundle bundle = new Bundle();
				bundle.putString("ID", obj.mID);
				bundle.putInt("Type", obj.mType);
				bundle.putString("Reference", obj.mReference);
				bundle.putString("Content", obj.mContent);
				bundle.putInt("ImageCount", obj.mImageCount);
				innerMessage.setData(bundle);
				innerMessage.sendToTarget();					
			}
		}
	};
}
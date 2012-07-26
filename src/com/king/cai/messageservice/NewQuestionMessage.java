package com.king.cai.messageservice;

import android.os.AsyncTask;

import com.king.cai.common.ComunicableActivity.EventProcessListener;
import com.king.cai.messageservice.ActiveMessageManager.ActiveFunctor;

public class NewQuestionMessage  extends ActiveMessage{
	public final static String s_MsgTag = "[QuestionBC]";
	private String mMsgPack = null;
	private String mPeerIP;
	public NewQuestionMessage(String peer, String RawMsg){
		super(s_MsgTag);
		mMsgPack = RawMsg;
		mPeerIP = peer;
	}
	
	public static class NewQuestionFunctor extends ActiveFunctor{

		@Override
		public ActiveMessage OnReceiveMessage(String peer, String param){
			return new NewQuestionMessage(peer, param);
		}
	}

	@Override
	public void Execute(EventProcessListener l) {
		new QuestionParseTask(l).execute(super.FromPack(mMsgPack));
	}
	
	public class ProgressObject{
		int mType;
		String mAnswer;
		String mContent;
		boolean mHasImage;
		ProgressObject(int type, String answer, String content, boolean hasImage){
			mType = type;
			mAnswer = answer;
			mContent = content;
			mHasImage = hasImage;
		}
	}
	
	public class QuestionParseTask extends  AsyncTask<String, ProgressObject, String> {
		EventProcessListener mProcessListener;
		public QuestionParseTask(EventProcessListener l){
			mProcessListener = l;
		}
		@Override
		protected String doInBackground(String... packs) {
			for (String subpack : packs){
				String[] questions = subpack.split("@");

				for (String question : questions){
					//[id]xx[answer]xxx[type]x[image]1/0[content]xxx
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
						String hasImage = question.substring(imagePos + "[image]".length(), contentPos);
						String content = question.substring(contentPos + "[content]".length(), question.length());
						publishProgress(new ProgressObject(type, answer, content, "1".equals(hasImage)));
					}
				}
			}
			return null;
		}
		@Override  
		protected void onPostExecute(String result) {  
			// 在doInBackground完成后，会收到处理好的结果result  
//			progressdialog.dismiss();   
		}  
		
		@Override  
		protected void onPreExecute() {  
			// 执行了execute  
//			progressdialog.show();  
		}  
		
		@Override  
		protected void onProgressUpdate(ProgressObject... values) {  
			// 更新进度
			//setProgress
			for (ProgressObject obj : values){
				mProcessListener.onNewQuestion(obj.mAnswer, obj.mType, obj.mContent, obj.mHasImage);					
			}
		}
	};
}
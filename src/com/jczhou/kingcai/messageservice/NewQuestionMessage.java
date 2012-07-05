package com.jczhou.kingcai.messageservice;

import android.os.AsyncTask;

import com.jczhou.kingcai.common.ComunicableActivity.EventProcessListener;
import com.jczhou.kingcai.messageservice.ActiveMessageManager.ActiveFunctor;

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
		String pack = super.FromPack(mMsgPack);

	}
	
	public class QuestionParseTask extends  AsyncTask<String, Integer, String> {
		EventProcessListener mProcessListener;
		public QuestionParseTask(EventProcessListener l){
			mProcessListener = l;
		}
		@Override
		protected String doInBackground(String... pack) {
			String[] questions = pack[0].split("@");
			for (String question : questions){
				//[answer]xxx[type]x[content]xxx
				//[id]xx[answer]xxx[type]x[image]1/0[content]xxx
				if (question.contains("[type]") && question.contains("[content]")
						&& question.contains("[answer]")){
					int TypePos = question.indexOf("[type]");
					String answer = question.substring("[answer]".length(), TypePos);
					int type = Integer.parseInt(question.substring(TypePos+"[type]".length(), TypePos+"[type]".length()+1));
					int ContentPos = question.indexOf("[content]");
					String content = question.substring(ContentPos + "[content]".length(), question.length());
					
					l.onNewQuestion(answer, type, content);
				}
			}
			return null;
		}
		@Override  
		protected void onPostExecute(String result) {  
			// 在doInBackground完成后，会收到处理好的结果result  
			progressdialog.dismiss();   
		}  
		
		@Override  
		protected void onPreExecute() {  
			// 执行了execute  
			progressdialog.show();  
		}  
		
		@Override  
		protected void onProgressUpdate(Integer... values) {  
			// 更新进度  
			mProcessListener();
		}  
	
	};
}
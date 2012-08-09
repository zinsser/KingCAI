package com.king.cai.examination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import android.graphics.Bitmap;

public class QuestionManager {
	private HashMap<String, QuestionInfo> mQuestions = new HashMap<String, QuestionInfo>();
	private ArrayList<QuestionListener> mListeners = new ArrayList<QuestionListener>();
	
	public static interface QuestionListener{
		public abstract void OnQuestionArrayChanged(ArrayList<String> ids);
		public abstract void OnAddQuestion(String id);		
		public abstract void OnClearQuestion();
		public abstract void OnImageReady(String qid, Bitmap bmp);
	}
	
	public QuestionManager(){
	}
	
	public void ImportLocalQuestions(){
		mQuestions.clear();
        for (int i = 0; i < Questions.sQuestions.length; ++i){
        	if (i == 0 || i == 6){
        		mQuestions.put(Integer.toString(i), new QuestionInfo(String.valueOf(i), 0, null, Questions.sQuestions[i], false));        		
        	}else if (i == 2){
        		mQuestions.put(Integer.toString(i), new QuestionInfo(String.valueOf(i), 3, "#123#abc#", Questions.sQuestions[i], false));        		
        	}else if (i == 3){
        		mQuestions.put(Integer.toString(i), new QuestionInfo(String.valueOf(i), 3, "#123#", Questions.sQuestions[i], true));        		
        	}else{
        		mQuestions.put(Integer.toString(i), new QuestionInfo(String.valueOf(i), 1, "#A#", Questions.sQuestions[i], false));        		
        	}
        }
        
       	NotifyQuestionArrayChanged();
	}
	
	public void NotifyQuestionArrayChanged(){
		for (QuestionListener l : mListeners){
			l.OnQuestionArrayChanged(GetIDs());
		}
	}
	
	public void AddQuestion(String id, int type, String reference, String detail, boolean bHasImage){
		mQuestions.put(id, new QuestionInfo(String.valueOf(id), type, reference, detail, bHasImage));

		for (QuestionListener l : mListeners){
			l.OnAddQuestion(id);
		}
	}
	
	public void AddQuestion(QuestionInfo question){
		String id = Integer.toString(mQuestions.size());
		mQuestions.put(id, question);
		for (QuestionListener l : mListeners){
			l.OnAddQuestion(id);
		}	
	}

	public void AddQuestionImage(Integer id, Bitmap bmp){
		if (bmp != null && mQuestions.get(id) != null){
			mQuestions.get(id).AddGraphic(bmp);
			for (QuestionListener l : mListeners){
				l.OnImageReady("0", bmp);
			}
		}
	}

	public void AddQuestionImage(String sid, Bitmap bmp){
		for (String qid : mQuestions.keySet()){
			if (mQuestions.get(qid).mID.equals(sid)){
				if (bmp != null && mQuestions.get(qid) != null){
					mQuestions.get(qid).AddGraphic(bmp);
					for (QuestionListener l : mListeners){
						l.OnImageReady(sid, bmp);
					}
				}
				break;
			}
		}
	}
	
	
	public  ArrayList<String> GetIDs(){
		ArrayList<String> ids = new ArrayList<String>();
		
		Set<String> keysets = mQuestions.keySet();
//		Integer[] keys = (Integer[])keysets.toArray(new Integer[keysets.size()]);
		for (String key : keysets){
			ids.add(key);
		}
		Collections.sort(ids);
		return ids;
	}
	
	public HashMap<String, QuestionInfo> GetQuestions(){
		return mQuestions;
	}
	
	public QuestionInfo GetQuestionItem(String id){
		return id != null && GetIDs().contains(id) ? mQuestions.get(id) : null;
	}

	//获取问题总个数（不包含标题之类的item）
	public int GetQuestionCount(){
		ArrayList<String> ids = GetIDs();
		int retCnt = ids.size();

		if (retCnt > 0){
			int lastIdx = retCnt - 1;
			retCnt = retCnt - GetUnQuestionCount(ids.get(lastIdx));
		}
		return retCnt;
	}
	
	//获取在beforeID之前，非问题的个数（在beforeID前，剔除标题之类的item后问题总个数）
	public int GetUnQuestionCount(String beforeID){
		int retCnt = 0;
		if (GetIDs().contains(beforeID)){
			for (String id : GetIDs()){ //由于GetIDs返回的列表有序，所以这么做是合适的
				if (id.equals(beforeID)){
					if (mQuestions.get(id).IsPaperTitle()){
						retCnt++;
					}
					break;
				}
				if (mQuestions.get(id).IsPaperTitle()){
					retCnt++;
				}
			}
		}

		return retCnt;
	}	
	
	public void Clear(){
		mQuestions.clear();
		for (QuestionListener l : mListeners){
			l.OnClearQuestion();
		}			
	}
	
	public void AddListener(QuestionListener l){
		if (!mListeners.contains(l)){
			mListeners.add(l);
		}
	}
}

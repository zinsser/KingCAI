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
		public abstract void OnImageReady(String qid, String imageIndex, Bitmap bmp);
	}
	
	public QuestionManager(){
	}
	
	public void ImportLocalQuestions(){
		mQuestions.clear();
        for (int i = 0; i < Questions.sQuestions.length; ++i){
        	if (i == 0 || i == 6){
        		AddQuestion(String.valueOf(i), 0, null, Questions.sQuestions[i], 0);
        	}else if (i == 2){
        		AddQuestion(String.valueOf(i), 3, "#123#abc#", Questions.sQuestions[i], 0);        		
        	}else if (i == 3){
        		AddQuestion(String.valueOf(i), 3, "#123#", Questions.sQuestions[i], 1);        		
        	}else{
        		AddQuestion(String.valueOf(i), 1, "#A#", Questions.sQuestions[i], 0);        		
        	}
        }
        
       	NotifyQuestionArrayChanged();
	}
	
	public void NotifyQuestionArrayChanged(){
		for (QuestionListener l : mListeners){
			l.OnQuestionArrayChanged(GetIDs());
		}
	}
	
	public void AddQuestion(String id, int type, String reference, String detail, int imageCount){
		AddQuestion(new QuestionInfo(id, type, reference, detail, imageCount));
	}
	
	public void AddQuestion(QuestionInfo question){
		String id = question.getQuestionID();
		mQuestions.put(id, question);
		for (QuestionListener l : mListeners){
			l.OnAddQuestion(id);
		}	
	}

	public void AddQuestionImage(String id, String imageIndex, Bitmap bmp){
		if (bmp != null && mQuestions.get(id) != null){
			mQuestions.get(id).AddGraphic(imageIndex, bmp);
			for (QuestionListener l : mListeners){
				l.OnImageReady(id, imageIndex, bmp);
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

	//��ȡ�����ܸ���������������֮���item��
	public int GetQuestionCount(){
		ArrayList<String> ids = GetIDs();
		int retCnt = ids.size();

		if (retCnt > 0){
			int lastIdx = retCnt - 1;
			retCnt = retCnt - GetUnQuestionCount(ids.get(lastIdx));
		}
		return retCnt;
	}
	
	//��ȡ��beforeID֮ǰ��������ĸ�������beforeIDǰ���޳�����֮���item�������ܸ�����
	public int GetUnQuestionCount(String beforeID){
		int retCnt = 0;
		if (GetIDs().contains(beforeID)){
			for (String id : GetIDs()){ //����GetIDs���ص��б�����������ô���Ǻ��ʵ�
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

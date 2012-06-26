package com.jczhou.kingcai.examination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class QuestionManager {
	private HashMap<Integer, QuestionInfo> mQuestions = new HashMap<Integer, QuestionInfo>();
	private ArrayList<QuestionListener> mListeners = new ArrayList<QuestionListener>();
	
	public static interface QuestionListener{
		public abstract void OnQuestionArrayChanged(ArrayList<Integer> ids);
		public abstract void OnAddQuestion(Integer id);		
		public abstract void OnClearQuestion();			
	}
	
	public QuestionManager(){
	}
	
	public void ImportLocalQuestions(){
		mQuestions.clear();
        for (int i = 0; i < Questions.sQuestions.length; ++i){
        	if (i == 0 || i == 6){
        		mQuestions.put(i, new QuestionInfo(0, null, Questions.sQuestions[i]));        		
        	}else if (i == 2){
        		mQuestions.put(i, new QuestionInfo(3, "#123#abc#def#", Questions.sQuestions[i]));        		
        	}else{
        		mQuestions.put(i, new QuestionInfo(1, "#A#", Questions.sQuestions[i]));        		
        	}
        }
        
       	NotifyQuestionArrayChanged();
	}
	
	public void NotifyQuestionArrayChanged(){
		for (QuestionListener l : mListeners){
			l.OnQuestionArrayChanged(GetIDs());
		}
	}
	
	public void AddQuestion(int id, int type, String reference, String detail){
		mQuestions.put(id, new QuestionInfo(type, reference, detail));

		for (QuestionListener l : mListeners){
			l.OnAddQuestion(id);
		}
	}
	
	public void AddQuestion(QuestionInfo question){
		Integer id = mQuestions.size();
		mQuestions.put(id, question);
		for (QuestionListener l : mListeners){
			l.OnAddQuestion(id);
		}	
	}

	public  ArrayList<Integer> GetIDs(){
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		Set<Integer> keysets = mQuestions.keySet();
//		Integer[] keys = (Integer[])keysets.toArray(new Integer[keysets.size()]);
		for (Integer key : keysets){
			ids.add(key);
		}
		Collections.sort(ids);
		return ids;
	}
	
	public HashMap<Integer, QuestionInfo> GetQuestions(){
		return mQuestions;
	}
	
	public QuestionInfo GetQuestionItem(Integer id){
		return id != null && GetIDs().contains(id) ? mQuestions.get(id) : null;
	}

	//��ȡ�����ܸ���������������֮���item��
	public int GetQuestionCount(){
		ArrayList<Integer> ids = GetIDs();
		int retCnt = ids.size();

		if (retCnt > 0){
			int lastIdx = retCnt - 1;
			retCnt = retCnt - GetUnQuestionCount(ids.get(lastIdx));
		}
		return retCnt;
	}
	
	//��ȡ��beforeID֮ǰ��������ĸ�������beforeIDǰ���޳�����֮���item�������ܸ�����
	public int GetUnQuestionCount(Integer beforeID){
		int retCnt = 0;
		if (GetIDs().contains(beforeID)){
			for (Integer id : GetIDs()){ //����GetIDs���ص��б�����������ô���Ǻ��ʵ�
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

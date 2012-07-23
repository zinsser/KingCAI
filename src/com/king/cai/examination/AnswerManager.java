package com.king.cai.examination;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;

public class AnswerManager implements QuestionManager.QuestionListener{
	private HashMap<Integer, Answer> mAnswers = new HashMap<Integer, Answer>();
	private QuestionManager mQuestionMgr = null;
	public AnswerManager(QuestionManager questionMgr){
		mQuestionMgr = questionMgr;
		mQuestionMgr.AddListener(this);
	}

	public HashMap<Integer, Answer> GetAnswers(){
		return mAnswers;
	}
	
	public Answer GetAnswer(Integer id){
		Answer answer = null;
		if (mAnswers.keySet().contains(id)){
			answer = mAnswers.get(id);
		}		
		return answer;
	}

	public void AddAnswer(QuestionManager questionMgr){
		for (Integer id : questionMgr.GetIDs()){
			if (questionMgr.GetQuestionItem(id).IsOption()){
				mAnswers.put(id, new OptionsAnswerInfo(questionMgr.GetQuestionItem(id).mReference));
			}else if (questionMgr.GetQuestionItem(id).IsBlank()){
				mAnswers.put(id, new BlankAnswerInfo(questionMgr.GetQuestionItem(id).mReference));				
			}else if (questionMgr.GetQuestionItem(id).IsLogic()){
				
			}else{
				mAnswers.put(id, null);
			}
		}		
	}
	
	public void AddAnswer(Integer id, QuestionInfo question, String s){
		if (question.IsOption()){
			mAnswers.put(id, new OptionsAnswerInfo(question.mReference, s));
		}else if (question.IsBlank()){
			mAnswers.put(id, new BlankAnswerInfo(question.mReference, s));				
		}else if (question.IsLogic()){
			
		}else{
			mAnswers.put(id, null);
		}
	}
	
	public String toString(){
		String retAnswer = "@";
		for (Integer id : mAnswers.keySet()){
			if (!mQuestionMgr.GetQuestionItem(id).IsPaperTitle()){
				if (mAnswers.get(id) != null){
					retAnswer += id + mAnswers.get(id).toString() + "@";
				}else{
					retAnswer += id + "@";
				}
			}
		}
		return retAnswer;
	}

	public void OnQuestionArrayChanged(ArrayList<Integer> ids) {
		AddAnswer(mQuestionMgr);		
	}

	public void OnAddQuestion(Integer id) {
		AddAnswer(mQuestionMgr);
	}

	public void OnClearQuestion() {
		mAnswers.clear();
	}

	public void OnImageReady(Bitmap bmp) {
		
	}
}

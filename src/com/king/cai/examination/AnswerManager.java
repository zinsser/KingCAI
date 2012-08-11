package com.king.cai.examination;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;

public class AnswerManager implements QuestionManager.QuestionListener{
	private HashMap<String, Answer> mAnswers = new HashMap<String, Answer>();
	private QuestionManager mQuestionMgr = null;
	public AnswerManager(QuestionManager questionMgr){
		mQuestionMgr = questionMgr;
		mQuestionMgr.AddListener(this);
	}

	public HashMap<String, Answer> GetAnswers(){
		return mAnswers;
	}
	
	public Answer GetAnswer(String id){
		Answer answer = null;
		if (mAnswers.keySet().contains(id)){
			answer = mAnswers.get(id);
		}		
		return answer;
	}

	public void AddAnswer(QuestionManager questionMgr){
		for (String id : questionMgr.GetIDs()){
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
	
	public void AddAnswer(String id, QuestionInfo question, String s){
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
		for (String id : mAnswers.keySet()){
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

	public void OnQuestionArrayChanged(ArrayList<String> ids) {
		AddAnswer(mQuestionMgr);		
	}

	public void OnAddQuestion(String id) {
		AddAnswer(mQuestionMgr);
	}

	public void OnClearQuestion() {
		mAnswers.clear();
	}

	public void OnImageReady(String qid, String imageIndex, Bitmap bmp) {		
	}
}

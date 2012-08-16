package com.king.cai.examination;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;

public class AnswerManager implements QuestionManager.QuestionListener{
	private HashMap<String, Answer> mAnswers = new HashMap<String, Answer>();
	private QuestionManager mQuestionMgr = null;
	public AnswerManager(QuestionManager questionMgr){
		mQuestionMgr = questionMgr;
		mQuestionMgr.addListener(this);
	}

	public HashMap<String, Answer> getAnswers(){
		return mAnswers;
	}
	
	public Answer getAnswer(String id){
		Answer answer = null;
		if (mAnswers.keySet().contains(id)){
			answer = mAnswers.get(id);
		}		
		return answer;
	}

	private Answer constructAnswer(String id, String answerContent){
		Answer answer = null;
		if (mQuestionMgr.getQuestionItem(id).isOption()){
			answer = new OptionsAnswerInfo(mQuestionMgr.getQuestionItem(id).mReference, answerContent);
		}else if (mQuestionMgr.getQuestionItem(id).isBlank()){
			answer = new BlankAnswerInfo(mQuestionMgr.getQuestionItem(id).mReference, answerContent);				
		}else if (mQuestionMgr.getQuestionItem(id).isLogic()){
			
		}
		
		return answer;
	}

	private Answer constructAnswer(String id){
		return constructAnswer(id, null);
	}
	
	
	public void addAnswer(){
		mAnswers.clear();
		for (String id : mQuestionMgr.getIDs()){
			mAnswers.put(id, constructAnswer(id));
		}
	}
	
	public void addAnswer(QuestionInfo question){
		mAnswers.put(question.getQuestionID(), constructAnswer(question.getQuestionID()));
	}
	
	public void addAnswer(String id, String s){
		mAnswers.put(id, constructAnswer(id, s));
	}
	
	public String toString(){
		String retAnswer = "@";
		for (String id : mAnswers.keySet()){
			if (!mQuestionMgr.getQuestionItem(id).isPaperTitle()){
				if (mAnswers.get(id) != null){
					retAnswer += id + mAnswers.get(id).toString() + "@";
				}else{
					retAnswer += id + "@";
				}
			}
		}
		return retAnswer;
	}

	public void onQuestionArrayChanged(ArrayList<Integer> ids) {
		addAnswer();
	}

	public void onAddQuestion(Integer id) {
		addAnswer(mQuestionMgr.getQuestionItem(id));
	}

	public void onClearQuestion() {
		mAnswers.clear();
	}

	public void onImageReady(String qid, String imageIndex, Bitmap bmp) {
	}
}

package com.king.cai.examination;

import java.util.HashMap;
import java.util.Iterator;

import android.os.Parcel;

public class BlankAnswerInfo extends Answer{
	private final static int mType = QuestionInfo.QUESTION_TYPE_BLANK;
	private HashMap<Integer, String> mAnswerString = new HashMap<Integer, String>();
	private HashMap<Integer, String> mRefAnswer = new HashMap<Integer, String>(); 
	private int mTotalBlank = 0;
	
	public BlankAnswerInfo(String refAnswer) {
		this(refAnswer, null);
	}

	public BlankAnswerInfo(String refAnswer, String answer) {
		super(mType);	
		if (answer != null){
			String2Map(answer, mAnswerString);
		}
		
		String2Map(refAnswer, mRefAnswer);
		mTotalBlank = mRefAnswer.size();
	}
	
	private void String2Map(String answer, HashMap<Integer, String> destMap){
		if (answer != null){
			String[] refs = answer.substring(1).split("#");
			if (refs.length > 1){
				for (int i = 0; i < refs.length; ++i){
					destMap.put(i+1, refs[i]);
				}		
			}else if (refs.length == 1){
				destMap.put(0, refs[0]);
			}
		}
	}
	
	@Override
	public String toString() {
		String ret = "#";

		for (Integer sub : mAnswerString.keySet()){
			ret += sub + "#" +mAnswerString.get(sub) + "#";
		}
		if (mAnswerString.size() == 0){
			ret += "#";
		}
		return ret;
	}

	@Override
	public void addAnswer(Parcel answers) {
		Integer cnt = answers.readInt();
		for (int i = 0; i < cnt; ++i){
			Integer subid = answers.readInt();
			String answer = answers.readString();
			mAnswerString.put(subid, answer);			
		}
	}

	@Override
	public Parcel getAnswer() {
		Parcel answer = Parcel.obtain();
		answer.writeInt(mAnswerString.size());
		for (Iterator<Integer> iter = mAnswerString.keySet().iterator(); 
				iter.hasNext(); ){
			Integer id = iter.next();
			answer.writeInt(id);
			answer.writeString(mAnswerString.get(id));			
		}
		answer.setDataPosition(0);
		return answer;
	}
	
	@Override
	public Parcel getRefAnswer() {
		Parcel answer = Parcel.obtain();
		answer.writeInt(mTotalBlank);
		for (Iterator<Integer> iter = mRefAnswer.keySet().iterator(); 
				iter.hasNext(); ){
			Integer id = iter.next();
			answer.writeInt(id);
			answer.writeString(mRefAnswer.get(id));
		}
	
		answer.setDataPosition(0);
		return answer;
	}
	
	@Override
	public boolean isAnswered() {
		return mAnswerString.size() != 0;
	}

	@Override
	public boolean isCorrect() {
		return mRefAnswer!= null && mAnswerString != null 
				&& mAnswerString.equals(mRefAnswer);
	}
}

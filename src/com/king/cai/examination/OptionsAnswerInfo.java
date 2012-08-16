package com.king.cai.examination;

import android.os.Parcel;

public class OptionsAnswerInfo extends Answer{
	private final static int mType = QuestionInfo.QUESTION_TYPE_OPTION;	
	private boolean[] mAnswers = new boolean[4];
	private String mRefAnswer;
	
	public OptionsAnswerInfo(String refAnswer) {
		this(refAnswer, null);
	}
/*
	public OptionsAnswerInfo(boolean[] answers) {
		super(mType);
		assert answers.length == 4;
		for (int i = 0; i < mAnswers.length; ++i){
			mAnswers[i] = answers[i]; 
		}
	}
*/
	public OptionsAnswerInfo(String refAnswer, String answer) {
		super(mType);
		mRefAnswer = refAnswer;
		if (answer != null){
			string2BooleanArray(answer, mAnswers);
		}else{
			for (int i = 0; i < mAnswers.length; ++i){
				mAnswers[i] = false;
			}			
		}
	}	
	
	@Override
	public String toString(){
		String ret = mAnswers[0] ? "#A" : "";
		ret += mAnswers[1] ? "#B" : "";
		ret += mAnswers[2] ? "#C" : "";
		ret += mAnswers[3] ? "#D" : "";
		ret += "#";
		return ret;
	}

	@Override
	public void addAnswer(Parcel answers) {
		answers.setDataPosition(0);
		answers.readBooleanArray(mAnswers);
	}

	@Override
	public Parcel getAnswer() {
		Parcel ret = Parcel.obtain();
		ret.writeBooleanArray(mAnswers);
		ret.setDataPosition(0);
		return ret;
	}

	@Override
	public Parcel getRefAnswer() {
		Parcel ret = Parcel.obtain();
		boolean[] refAnswers =  new boolean[4];
		string2BooleanArray(mRefAnswer, refAnswers);
		ret.writeBooleanArray(refAnswers);
		ret.setDataPosition(0);
		return ret;
	}	
	
	@Override
	public boolean isAnswered() {
		return mAnswers[0] || mAnswers[1] || mAnswers[2] || mAnswers[3];
	}

	@Override
	public boolean isCorrect() {
		String temp = toString();
		return temp.equals(mRefAnswer);
	}

	private void string2BooleanArray(String strAnswer, boolean[] boolAnswers){
		for (int i = 0; i < boolAnswers.length; ++i){
			boolAnswers[i] = false;
		}

		if (strAnswer != null && !strAnswer.equals("")){
			String[] refs = strAnswer.substring(1).split("#");
			for (String ref : refs){
				if ("A".equals(ref)){
					boolAnswers[0] = true;
				}
				if ("B".equals(ref)){
					boolAnswers[1] = true;				
				}
				if ("C".equals(ref)){
					boolAnswers[2] = true;
				}
				if ("D".equals(ref)){
					boolAnswers[3] = true;				
				}			
			}
		}
	}	
}

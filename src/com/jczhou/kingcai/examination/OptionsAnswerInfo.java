package com.jczhou.kingcai.examination;

import android.os.Parcel;

public class OptionsAnswerInfo extends Answer{
	private final static int mType = QuestionInfo.QUESTION_TYPE_OPTION;	
	private boolean[] mAnswers = new boolean[4];
	private String mRefAnswer;
	
	public OptionsAnswerInfo(String refAnswer) {
		super(mType);
		mRefAnswer = refAnswer;

		for (int i = 0; i < mAnswers.length; ++i){
			mAnswers[i] = false;
		}
	}

	public OptionsAnswerInfo(boolean[] answers) {
		super(mType);
		assert answers.length == 4;
		for (int i = 0; i < mAnswers.length; ++i){
			mAnswers[i] = answers[i]; 
		}
	}

	public OptionsAnswerInfo(String refAnswer, String answer) {
		super(mType);
		mRefAnswer = refAnswer;
		
		String2BooleanArray(answer, mAnswers);
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
	public void AddAnswer(Parcel answers) {
		answers.setDataPosition(0);
		answers.readBooleanArray(mAnswers);
	}

	@Override
	public Parcel GetAnswer() {
		Parcel ret = Parcel.obtain();
		ret.writeBooleanArray(mAnswers);
		ret.setDataPosition(0);
		return ret;
	}

	@Override
	public Parcel GetRefAnswer() {
		Parcel ret = Parcel.obtain();
		boolean[] refAnswers =  new boolean[4];
		String2BooleanArray(mRefAnswer, refAnswers);
		ret.writeBooleanArray(refAnswers);
		ret.setDataPosition(0);
		return ret;
	}	
	
	@Override
	public boolean IsAnswered() {
		return mAnswers[0] || mAnswers[1] || mAnswers[2] || mAnswers[3];
	}

	@Override
	public boolean IsCorrect() {
		return toString().equals(mRefAnswer);
	}

	private void String2BooleanArray(String strAnswer, boolean[] boolAnswers){
		for (int i = 0; i < boolAnswers.length; ++i){
			boolAnswers[i] = false;
		}

		if (strAnswer != null){
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

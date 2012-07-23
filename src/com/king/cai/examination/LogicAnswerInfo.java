package com.king.cai.examination;

import android.os.Parcel;

public class LogicAnswerInfo extends Answer{
	private final static int mType = QuestionInfo.QUESTION_TYPE_LOGIC;
	public LogicAnswerInfo(String refAnswer) {
		super(mType);
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public void AddAnswer(Parcel answers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Parcel GetAnswer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean IsAnswered() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean IsCorrect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Parcel GetRefAnswer() {
		// TODO Auto-generated method stub
		return null;
	}

}

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
	public void addAnswer(Parcel answers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Parcel getAnswer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAnswered() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCorrect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Parcel getRefAnswer() {
		// TODO Auto-generated method stub
		return null;
	}

}

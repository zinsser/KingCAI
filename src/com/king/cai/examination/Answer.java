package com.king.cai.examination;

import android.os.Parcel;

public abstract class Answer {
	public boolean mIsMark = false;
	private int mType = -1;

	protected Answer(int type){
		mType = type;
	}
	public int getQuestionType(){
		return mType;
	}
	public abstract String toString();
	public abstract void addAnswer(Parcel values);	
	public abstract Parcel getAnswer();
	public abstract Parcel getRefAnswer();
	public abstract boolean isAnswered();
	public abstract boolean isCorrect();
}

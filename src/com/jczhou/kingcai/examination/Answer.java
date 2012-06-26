package com.jczhou.kingcai.examination;

import android.os.Parcel;

public abstract class Answer {
	public boolean mIsMark = false;
	private int mType = -1;

	protected Answer(int type){
		mType = type;
	}
	public int QuestionType(){
		return mType;
	}
	public abstract String toString();
	public abstract void AddAnswer(Parcel values);	
	public abstract Parcel GetAnswer();
	public abstract Parcel GetRefAnswer();		
	public abstract boolean IsAnswered();
	public abstract boolean IsCorrect();
}

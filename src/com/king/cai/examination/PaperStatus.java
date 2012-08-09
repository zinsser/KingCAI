package com.king.cai.examination;

import java.util.ArrayList;

import android.content.res.Resources;
import android.text.Html;
import android.widget.ListView;

import com.king.cai.R;
import com.king.cai.examination.ItemViewHolder;

public abstract class PaperStatus{
	protected ArrayList<String> mListFirst = new ArrayList<String>();
	protected ArrayList<String> mListSecond = new ArrayList<String>();

	//0:All;  1:answered;   2:marked 
	//0:All;  1:correct;   2:incorrect
	protected int mFilterLevel = 0;  	 	

	protected PaperActivity mStatusOwner = null;
	protected PaperStatus mNextStatus = null;
	protected PaperStatus(PaperActivity owner, PaperStatus nextStatus){
		mStatusOwner = owner;
		mNextStatus = nextStatus;
	}

	protected String MakeMixString(int iField, int resid){
    	final Resources res = mStatusOwner.getResources();
    	return String.format(Html.fromHtml(res.getString(resid)).toString(), iField);
	}	

	public void onSearchFinished() {
		mStatusOwner.ChangeFilterButtonText(R.string.AllQuestions);
		mFilterLevel = 0;
	}
	
	protected void switchStatus(){
		if (mNextStatus != null){
			LeaveStatus();
			mNextStatus.EnterStatus();
		}
	}
	
	public abstract void onCommitClick();
	public abstract void onFilterClick(ListView listView, PaperViewAdapter fullAdapter);

	public abstract void doGettingItemView(ItemViewHolder holder, String id, int fontsize);	
	
	public abstract void EnterStatus();
	public abstract void LeaveStatus();
}

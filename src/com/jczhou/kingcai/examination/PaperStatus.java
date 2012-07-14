package com.jczhou.kingcai.examination;

import java.util.ArrayList;

import android.content.res.Resources;
import android.text.Html;
import android.widget.ListView;

import com.jczhou.kingcai.R;
import com.jczhou.kingcai.examination.ItemViewHolder;

public abstract class PaperStatus{
	protected ArrayList<Integer> mListFirst = new ArrayList<Integer>();
	protected ArrayList<Integer> mListSecond = new ArrayList<Integer>();

	//0:All;  1:answered;   2:marked 
	//0:All;  1:correct;   2:incorrect
	protected int mFilterLevel = 0;  	 	

	protected PaperActivity mStatusOwner = null;
	
	protected PaperStatus(PaperActivity owner){
		mStatusOwner = owner;
		InitStatus();
	}

	protected String MakeMixString(int iField, int resid){
    	final Resources res = mStatusOwner.getResources();
    	return String.format(Html.fromHtml(res.getString(resid)).toString(), iField);
	}	

	public void onSearchFinished() {
		mStatusOwner.ChangeFilterButtonText(R.string.AllQuestions);
		mFilterLevel = 0;
	}
	
	public abstract void onCommitClick();
	public abstract void onFilterClick(ListView listView, PaperViewAdapter fullAdapter);

	public abstract void doGettingItemView(ItemViewHolder holder, Integer id, int fontsize);	
	
	protected abstract void InitStatus();
}

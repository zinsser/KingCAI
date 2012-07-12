package com.jczhou.kingcai.examination;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
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

	protected Bitmap mMarkIcon = null;
    protected Bitmap mUnMarkIcon = null;	
	protected PaperActivity mStatusOwner = null;
	
	protected PaperStatus(PaperActivity owner){
		mStatusOwner = owner;
		InitStatus();
		LoadOptionIcon(mStatusOwner);
	}

	protected String MakeMixString(int iField, int resid){
    	final Resources res = mStatusOwner.getResources();
    	return String.format(Html.fromHtml(res.getString(resid)).toString(), iField);
	}	

	public void onSearchFinished() {
		mStatusOwner.ChangeFilterButtonText(R.string.AllQuestions);
		mFilterLevel = 0;
	}

	public abstract void onBlankInputShow(Integer questinID, Answer answer);
	
	public abstract void onCommitClick();
	public abstract void onFilterClick(ListView listView, PaperViewAdapter fullAdapter);

	public abstract void doGettingItemView(ItemViewHolder holder, Integer id, int fontsize);	

	public abstract void onBlankInputDone(Integer questionID, final Answer answer);
	
	protected abstract void LoadOptionIcon(Context context);
	protected abstract void InitStatus();
}

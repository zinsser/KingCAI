package com.jczhou.kingcai.examination;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.text.Html;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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

	public void onLayoutOptionRadioButton(ItemViewHolder holder, Answer answer){
    	//—°‘ÒÃ‚
/*		if (answer != null){
	    	Parcel parcelValues  = answer.GetAnswer(); 
	    	if (parcelValues != null){
	        	boolean[] bValue = parcelValues.createBooleanArray();
	        	assert bValue.length == 4;
	            holder.radioBtnA.setChecked(bValue[0]);
	           	holder.radioBtnB.setChecked(bValue[1]);
	           	holder.radioBtnC.setChecked(bValue[2]);       	
	           	holder.radioBtnD.setChecked(bValue[3]);
	    	}
		}*/
	}	

	public void onOptionPanelClick(View v, Answer answer){
		Integer viewID = v.getId();
		if(viewID == R.id.txtQuestionDetail) {
			Integer questionID = (Integer)((TextView)v).getTag();
			mStatusOwner.OnQuestionDetailClick(questionID);
		}
	}	

	public void onSearchFinished() {
		mStatusOwner.ChangeFilterButtonText(R.string.AllQuestions);
		mFilterLevel = 0;
	}		

	public abstract void onBlankInputShow(Integer questinID, Answer answer);
	
	public abstract void onCommitClick();
	public abstract void onFilterClick(ListView listView, PaperViewAdapter fullAdapter);

	public abstract void onLayoutMarkButton(ItemViewHolder holder, Answer answer);
	public abstract void onBlankInputDone(Integer questionID, final Answer answer);
	
	protected abstract void LoadOptionIcon(Context context);
	protected abstract void InitStatus();
}

package com.king.cai.examination;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Html;
import android.view.WindowManager;
import android.widget.ListView;

import com.king.cai.R;
import com.king.cai.examination.ItemViewHolder;

public abstract class PaperStatus{
	protected ArrayList<Integer> mListFirst = new ArrayList<Integer>();
	protected ArrayList<Integer> mListSecond = new ArrayList<Integer>();

	//0:All;  1:answered;   2:marked 
	//0:All;  1:correct;   2:incorrect
	protected int mFilterLevel = 0;  	 	

	protected PaperActivity mStatusOwner = null;

	protected PaperStatus(PaperActivity owner){
		mStatusOwner = owner;
	}

	protected String MakeMixString(int iField, int resid){
    	final Resources res = mStatusOwner.getResources();
    	return String.format(Html.fromHtml(res.getString(resid)).toString(), iField);
	}	

	public void onSearchFinished() {
		mStatusOwner.ChangeFilterButtonText(R.string.AllQuestions);
		mFilterLevel = 0;
	}

	protected void showAlertDialog(){
		AlertDialog dlg = new AlertDialog.Builder(mStatusOwner)
				.setTitle(R.string.ExitPromptTitle)
				.setMessage(R.string.ExitPromptMsg)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		
					public void onClick(DialogInterface dialog, int whichButton) {
						
						mStatusOwner.finish();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.setCancelable(false)
				.create();
		dlg.show();
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	}
	
	public abstract boolean onBackkeyDown();
	public abstract void onCommitClick();
	public abstract void onFilterClick(ListView listView, PaperViewAdapter fullAdapter);
	
	public abstract void doGettingItemView(ItemViewHolder holder, Integer index, int fontsize);	
	
	public abstract void EnterStatus();
	public abstract void LeaveStatus();
}

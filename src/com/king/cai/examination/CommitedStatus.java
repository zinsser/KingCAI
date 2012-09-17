package com.king.cai.examination;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.king.cai.R;
import com.king.cai.examination.ItemViewHolder;

public class CommitedStatus extends PaperStatus{
	private boolean mShowAnswers = false;
	public CommitedStatus(PaperActivity owner, boolean showAnswer){
		super(owner);
		mShowAnswers = showAnswer;
	}
	
	@Override
	public void EnterStatus() {
		mStatusOwner.InitUncorrectList(mListFirst, mListSecond);
		//TODO:打开或注释此行代码可以实现立即显示正确与否
		mStatusOwner.ShowCorrectInfo(mListFirst.size());
	}		

	@Override
	public void LeaveStatus(){
		
	}
	
	@Override
	public void onFilterClick(ListView listView,
			PaperViewAdapter fullAdapter) {
	
		mFilterLevel = (mFilterLevel + 1) % 3;
		if (mFilterLevel == 0){
			listView.setAdapter(fullAdapter);				
			mStatusOwner.ChangeFilterButtonText(R.string.AllQuestions);
		}else if (mFilterLevel == 1){
			listView.setAdapter(fullAdapter.CloneAdapter(mListFirst));
			mStatusOwner.ChangeFilterButtonText(R.string.CorrectQuestions);
		}else if (mFilterLevel == 2){
			listView.setAdapter(fullAdapter.CloneAdapter(mListSecond));			
			mStatusOwner.ChangeFilterButtonText(R.string.IncorrectQuestions);
		}	
		((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();		
	}

	public boolean onBackkeyDown(boolean pcDied){
		showAlertDialog();		
		return true;
	}
	
	@Override
	public void onCommitClick() {
		mStatusOwner.showToast(R.string.CommitTips);		
	}

	@Override
	public void doGettingItemView(ItemViewHolder holder, Integer index, int fontsize){
		holder.doGettingItemViews(index, fontsize, mShowAnswers);
	}	
}

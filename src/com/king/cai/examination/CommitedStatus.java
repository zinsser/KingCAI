package com.king.cai.examination;

import android.widget.ListView;

import com.king.cai.R;
import com.king.cai.examination.ItemViewHolder;

public class CommitedStatus extends PaperStatus{

	public CommitedStatus(PaperActivity owner, PaperStatus nextStatus){
		super(owner, nextStatus);
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
	}

	@Override
	public void onCommitClick() {
		mStatusOwner.showToast(R.string.CommitTips);		
	}

	@Override
	public void doGettingItemView(ItemViewHolder holder, String id, int fontsize){
		holder.doGettingItemViews(id, fontsize, false);
	}	
}

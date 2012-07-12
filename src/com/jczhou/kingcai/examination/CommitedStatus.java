package com.jczhou.kingcai.examination;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ListView;

import com.jczhou.kingcai.R;
import com.jczhou.kingcai.examination.ItemViewHolder;

public class CommitedStatus extends PaperStatus{

	public CommitedStatus(PaperActivity owner){
		super(owner);
	}
	
	@Override
	protected void InitStatus() {
		mStatusOwner.InitUncorrectList(mListFirst, mListSecond);

		//TODO:打开或注释此行代码可以实现立即显示正确与否
		mStatusOwner.ShowCorrectInfo(mListFirst.size());
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
	public void doGettingItemView(ItemViewHolder holder, Integer id, int fontsize){
		holder.doGettingItemViews(id, fontsize);
	}	
	
	@Override
	protected void LoadOptionIcon(Context context) {
        mMarkIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_bullet_key_permission);        
        mUnMarkIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete);
	}

	@Override
	public void onBlankInputDone(Integer questionID, final Answer answer) {
	}

	@Override
	public void onBlankInputShow(Integer questionID, Answer answer) {
//		mStatusOwner.ShowAnswerContent(questionID);		
//		mStatusOwner.ShowReferenceContent(questionID);
	}	
}

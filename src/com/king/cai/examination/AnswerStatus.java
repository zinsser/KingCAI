package com.king.cai.examination;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.ListView;


import com.king.cai.R;
import com.king.cai.examination.ItemViewHolder;
import com.king.cai.examination.QuestionManager.QuestionListener;


public class AnswerStatus extends PaperStatus implements QuestionListener{

	private CountDownTimer mTickTimer = null;
	private QuestionManager mQuestionMgr = null;
	public AnswerStatus(PaperActivity owner, QuestionManager mgr){
		super(owner);
		mQuestionMgr = mgr;
		mQuestionMgr.addListener(this);
   	}
	
	@Override
	public void EnterStatus() {
		mListFirst.clear();
    	FetchUndoneList(mListFirst);
		mStatusOwner.ShowDoneInfo(mListFirst.size());
		startTimeTicker(true);
	}
	
	@Override
	public void LeaveStatus(){
		mTickTimer.cancel();
	}

	public void onQuestionArrayChanged(ArrayList<Integer> ids) {
		mListFirst.clear();
		FetchUndoneList(mListFirst);
		mStatusOwner.ShowDoneInfo(mListFirst.size());
	}
	
	public void onAddQuestion(Integer id) {
		if (!mQuestionMgr.getQuestionItem(id).isPaperTitle()){
			mListFirst.add(id);
		}
		mStatusOwner.ShowDoneInfo(mListFirst.size());
	}


	public void onClearQuestion() {
		mListFirst.clear();
		mStatusOwner.ShowDoneInfo(mListFirst.size());
	}

	public void onImageReady(String qid, String imageIndex, Bitmap bmp) {
	}
	
	public void doGettingItemView(ItemViewHolder holder, Integer index, int fontsize){
		holder.doGettingItemViews(index, fontsize, new ItemViewHolder.onSubViewClickListener() {

			public void onViewClick(Integer index, Answer answer) {
				PostClicked(index, answer);	
			}
		});
	}

    private void startTimeTicker(final boolean bCountDown){
    	long TotalTickTime = bCountDown ? 2700000 : 86400000;
    	mTickTimer = new CountDownTimer(TotalTickTime, 1000) {
			private String rawtitle = mStatusOwner.mTextViewTitle.getText().toString();
			private long mTotalTicks = 0;
		
			public void onTick(long millis) {
				mTotalTicks = bCountDown ?  millis/1000 : mTotalTicks+1;
				if (mTotalTicks % 300 == 0){
			    	//TODO: save question info&answer info to a pair file
					mStatusOwner.SaveConfig(false);
					mStatusOwner.SavePaper();
					mStatusOwner.CommitAnswers();
				}
				mStatusOwner.mTextViewTitle.setText(rawtitle + "  - " + SecondsToString(mTotalTicks));
			}

			public void onFinish() {
				mStatusOwner.mTextViewTitle.setText(rawtitle + "  - " + SecondsToString(mTotalTicks));
			}
			
			private String SecondsToString(long seconds){
				long second = seconds % 60;
				long minus = (seconds / 60) % 60;
				long hour =  (seconds / 60) / 60;
				return String.format("%2d:%2d:%2d", hour, minus, second);
			}
		};
		mTickTimer.start();
    }	
	
	@Override
	public void onFilterClick(ListView listView, PaperViewAdapter fullAdapter) {
		
		mFilterLevel = (mFilterLevel + 1) % 3;
		if (mFilterLevel == 0){
			listView.setAdapter(fullAdapter);	
			mStatusOwner.ChangeFilterButtonText(R.string.AllQuestions);			
		}else if (mFilterLevel == 1){//未完成
			listView.setAdapter(fullAdapter.CloneAdapter(mListFirst));
			mStatusOwner.ChangeFilterButtonText(R.string.UndoneQuestions);
		}else if (mFilterLevel == 2){//未确定
			listView.setAdapter(fullAdapter.CloneAdapter(mListSecond));			
			mStatusOwner.ChangeFilterButtonText(R.string.UncertainQuestions);
		}
	}
	
	@Override
	public void onCommitClick() {
		AlertDialog adlg = new AlertDialog.Builder(mStatusOwner)
			.setTitle(R.string.CommittingTitle)
			.setMessage(R.string.CommitPromptMsg)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					mStatusOwner.switch2WaitingStatus();
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.create();
		
		adlg.show();
		adlg.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	}

	private void PostClicked(Integer index, Answer answer){
		if (answer.mIsMark){
			if (!mListSecond.contains(index)){
				mListSecond.add(index);
			}
		}else{
			if (mListSecond.contains(index)){
				mListSecond.remove(index);
			}
		}

		if (answer.isAnswered()){
			if (mListFirst.contains(index)){
				mListFirst.remove(index);
				mStatusOwner.ShowDoneInfo(mListFirst.size());				
			}
		}else{
			if (!mListFirst.contains(index)){
				mListFirst.add(index);
				mStatusOwner.ShowDoneInfo(mListFirst.size());
			}
		}
	}

	private void FetchUndoneList(ArrayList<Integer> undonelist){
		undonelist.clear();
    	for (Integer index : mQuestionMgr.getIndexes()){
    		if (!mQuestionMgr.getQuestionItem(index).isPaperTitle()){
    			mListFirst.add(index);
    		}
    	}		
	}
}

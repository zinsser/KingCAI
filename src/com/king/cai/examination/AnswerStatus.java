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
		mQuestionMgr.AddListener(this);
   	}
	
	@Override
	public void EnterStatus() {
    	FetchUndoneList(mListFirst);
		mStatusOwner.ShowDoneInfo(mListFirst.size());
		startTimeTicker(true);
	}
	
	@Override
	public void LeaveStatus(){
		mTickTimer.cancel();
	}
	
	public void doGettingItemView(ItemViewHolder holder, Integer id, int fontsize){
		holder.doGettingItemViews(id, fontsize, new ItemViewHolder.onSubViewClickListener() {
			
			public void onViewClick(Integer questionID, Answer answer) {
				PostClicked(questionID, answer);
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
					mStatusOwner.SwitchPaperStatus();
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.create();
		
		adlg.show();
		adlg.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	}

	private void PostClicked(Integer questionID, final Answer answer){
		if (answer.mIsMark){
			if (!mListSecond.contains(questionID)){
				mListSecond.add(questionID);
			}
		}else{
			if (mListSecond.contains(questionID)){
				mListSecond.remove(questionID);
			}
		}

		if (answer.IsAnswered()){
			if (mListFirst.contains(questionID)){
				mListFirst.remove(questionID);
				mStatusOwner.ShowDoneInfo(mListFirst.size());				
			}
		}else{
			if (!mListFirst.contains(questionID)){
				mListFirst.add(questionID);
				mStatusOwner.ShowDoneInfo(mListFirst.size());
			}
		}
	}

	public void OnQuestionArrayChanged(ArrayList<Integer> ids) {
		FetchUndoneList(mListFirst);
		mStatusOwner.ShowDoneInfo(mListFirst.size());
	}

	private void FetchUndoneList(ArrayList<Integer> undonelist){
    	for (Integer id : mQuestionMgr.GetIDs()){
    		if (!mQuestionMgr.GetQuestionItem(id).IsPaperTitle()){
    			mListFirst.add(id);
    		}
    	}		
	}

	public void OnAddQuestion(Integer id) {
		if (!mQuestionMgr.GetQuestionItem(id).IsPaperTitle()){
			mListFirst.add(id);
		}
		mStatusOwner.ShowDoneInfo(mListFirst.size());
	}


	public void OnClearQuestion() {
		mListFirst.clear();
		mStatusOwner.ShowDoneInfo(mListFirst.size());
	}

	public void OnImageReady(String sid, Bitmap bmp) {
		
	}	
}

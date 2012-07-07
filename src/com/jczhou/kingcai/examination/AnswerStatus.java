package com.jczhou.kingcai.examination;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;

import com.jczhou.kingcai.R;
import com.jczhou.kingcai.examination.QuestionDetailViewAdapter.ViewHolder;
import com.jczhou.kingcai.examination.QuestionManager.QuestionListener;


public class AnswerStatus extends PaperStatus implements QuestionListener{

	private CountDownTimer mTickTimer = null;
	private QuestionManager mQuestionMgr = null;
	public AnswerStatus(PaperActivity owner, QuestionManager mgr){
		super(owner);
		mQuestionMgr = mgr;
		mQuestionMgr.AddListener(this);
   	}
	
	@Override
	protected void InitStatus() {
		mStatusOwner.FetchUndoneList(mListFirst);
		mStatusOwner.ShowDoneInfo(mListFirst.size());
		startTimeTicker(true);
	}

    private void startTimeTicker(final boolean bCountDown){
    	//TODO:倒计时器可以顺利执行Finished，但如何停止累加计时器？
    	long TotalTickTime = bCountDown ? 2700000 : 86400000;
    	mTickTimer = new CountDownTimer(TotalTickTime, 1000) {
			private String rawtitle = mStatusOwner.getTitle().toString();
			private long mTotalTicks = 0;
		
			public void onTick(long millis) {
				mTotalTicks = bCountDown ?  millis/1000 : mTotalTicks+1;
				if (mTotalTicks % 300 == 0){
			    	//TODO: save question info&answer info to a pair file
					mStatusOwner.SaveConfig(false);
					mStatusOwner.SavePaper();
					mStatusOwner.CommitAnswers();
				}
				mStatusOwner.setTitle(rawtitle + "  - " + SecondsToString(mTotalTicks));
			}

			public void onFinish() {
				mStatusOwner.setTitle(rawtitle + "  - " + SecondsToString(mTotalTicks));
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
	public void onFilterClick(ListView listView, 
							QuestionDetailViewAdapter fullAdapter) {
		mStatusOwner.findViewById(R.id.tableInput).setVisibility(View.GONE);
		mStatusOwner.findViewById(R.id.tableReference).setVisibility(View.GONE);
		
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
		new AlertDialog.Builder(mStatusOwner)
			.setTitle(R.string.CommittingTitle)
			.setMessage(R.string.CommitPromptMsg)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				private CountDownTimer tickTimer = mTickTimer;

				public void onClick(DialogInterface dialog, int which) {
					if (tickTimer != null){
						tickTimer.cancel();
					}
					mStatusOwner.SwitchPaperStatus();
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.show();
	}

	@Override
	public void onOptionPanelClick(View v, Answer answer) {
		super.onOptionPanelClick(v, answer);
		if (answer == null){
			return ;
		}
			
		int viewID = v.getId();
		Integer questionID = 0;
		switch (viewID){
		case R.id.imgMark:
			answer.mIsMark = !answer.mIsMark;
			ImageView imgMask = (ImageView)v;
			imgMask.setImageBitmap(answer.mIsMark ? mMarkIcon : mUnMarkIcon);
			questionID = (Integer)imgMask.getTag();
			PostClicked(questionID, answer);
			break;
		case R.id.radioBtnA:
		case R.id.radioBtnB:
		case R.id.radioBtnC:
		case R.id.radioBtnD:
			RadioButton radioBtn = (RadioButton)v;
			questionID = (Integer)radioBtn.getTag();
			
			boolean[] bRadioValue = answer.GetAnswer().createBooleanArray();
			assert bRadioValue.length == 4;
			boolean bChecked = false;
			if (viewID == R.id.radioBtnA){
				bRadioValue[0] = !bRadioValue[0];
				bChecked = bRadioValue[0];
			}else if (viewID == R.id.radioBtnB){
				bRadioValue[1] = !bRadioValue[1];
				bChecked = bRadioValue[1];
			}else if (viewID == R.id.radioBtnC){
				bRadioValue[2] = !bRadioValue[2];
				bChecked = bRadioValue[2];
			}else if (viewID == R.id.radioBtnD){
				bRadioValue[3] = !bRadioValue[3];
				bChecked = bRadioValue[3];
			}
			radioBtn.setChecked(bChecked);
			
			Parcel answers = Parcel.obtain();
			answers.writeBooleanArray(bRadioValue);
			answer.AddAnswer(answers);
			
			PostClicked(questionID, answer);				
			break;
		default:
			break;
		}
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

	@Override
	public void onLayoutMarkButton(ViewHolder holder, Answer answer) {
        holder.mark.setEnabled(true);
        if (answer != null && answer.mIsMark){
        	holder.mark.setImageBitmap(mMarkIcon);
        }else{
            holder.mark.setImageBitmap(mUnMarkIcon);
        }
	}

	@Override
	protected void LoadOptionIcon(Context context) {
        mMarkIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.mark_icon);        
        mUnMarkIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.unmark_icon);
	}

	@Override
	public void onLayoutOptionRadioButton(ViewHolder holder, Answer answer) {
		super.onLayoutOptionRadioButton(holder, answer);
		
        holder.radioBtnA.setEnabled(true);
        holder.radioBtnB.setEnabled(true);
        holder.radioBtnC.setEnabled(true);
        holder.radioBtnD.setEnabled(true);
	}

	@Override
	public void onBlankInputDone(Integer questionID, final Answer answer) {
		PostClicked(questionID, answer);
	}

	@Override
	public void onBlankInputShow(Integer questionID, Answer answer){
		mStatusOwner.ShowAnswerContent(questionID);
	}


	public void OnQuestionArrayChanged(ArrayList<Integer> ids) {
		mStatusOwner.FetchUndoneList(mListFirst);
		mStatusOwner.ShowDoneInfo(mListFirst.size());
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
}

package com.jczhou.kingcai.examination;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.jczhou.kingcai.R;
import com.jczhou.kingcai.examination.QuestionManager.QuestionListener;

public class QuestionItemViewHolder extends ItemViewHolder{
	protected ImageView mMark = null;
	protected RatingBar mRatingBarHardness = null;
	protected LinearLayout mLinearLayoutOptions = null;
	protected LinearLayout mLinearLayoutBlanks = null;
	protected ImageView mImageView_1 = null;
	protected ImageView mImageView_2 = null;
	
	protected Bitmap mMarkIcon = null;
    protected Bitmap mUnMarkIcon = null;	
    
	public QuestionItemViewHolder(PaperActivity hostActivity, View rawView,
    		QuestionManager questionMgr) {
		super(hostActivity, rawView, questionMgr);
		
	    mMark = (ImageView) rawView.findViewById(R.id.imageViewMarker);
	    mRatingBarHardness = (RatingBar)rawView.findViewById(R.id.ratingBarHardness);
	    
	    mLinearLayoutOptions = (LinearLayout)rawView.findViewById(R.id.linearLayoutOption);
	    mLinearLayoutBlanks = (LinearLayout)rawView.findViewById(R.id.linearLayoutBlanks);
	    
	    mImageView_1 = (ImageView)rawView.findViewById(R.id.imageView_1);
	    mImageView_2 = (ImageView)rawView.findViewById(R.id.imageView_2);
	    
	    mQuestionMgr.AddListener(new QuestionListener(){

			public void OnQuestionArrayChanged(ArrayList<Integer> ids) {
				// TODO Auto-generated method stub
				
			}

			public void OnAddQuestion(Integer id) {
				// TODO Auto-generated method stub
			}

			public void OnClearQuestion() {
				// TODO Auto-generated method stub
				
			}

			public void OnImageReady(Bitmap bmp) {
				mImageView_1.setImageBitmap(bmp);
			}
	    });
	}
	
	@Override
    public void doGettingItemViews(Integer id, int fontSize, onSubViewClickListener listener){
    	super.doGettingItemViews(id, fontSize, listener);
    	doLayoutSubViews(id);

    	mMarkIcon = BitmapFactory.decodeResource(mHostActivity.getResources(), R.drawable.mark_icon);
    	mUnMarkIcon  = BitmapFactory.decodeResource(mHostActivity.getResources(), R.drawable.unmark_icon);
    	
    	mMark.setTag(id);
    	
		Answer answer = mHostActivity.getAnswerManager().GetAnswer(id);
		mMark.setImageBitmap(mUnMarkIcon);
		if (answer != null && answer.mIsMark){
			mMark.setImageBitmap(mMarkIcon);	
		}

	    mMark.setOnClickListener(new PanelClickListener(listener));	
	}

	@Override
    public void doGettingItemViews(Integer id, int fontSize){
    	super.doGettingItemViews(id, fontSize);
    	doLayoutSubViews(id);
    	
		AnswerManager answerMgr = mHostActivity.getAnswerManager();
		
    	if (mRatingBarHardness != null){
    		mRatingBarHardness.setVisibility(View.VISIBLE);
    	}
    	
    	mMarkIcon = BitmapFactory.decodeResource(mHostActivity.getResources(), R.drawable.ic_bullet_key_permission);
    	mUnMarkIcon  = BitmapFactory.decodeResource(mHostActivity.getResources(), R.drawable.ic_delete);
    	
    	mMark.setEnabled(false);
    	Answer answer = answerMgr.GetAnswer(id);
    	if (answer != null && answer.IsCorrect()){
	    	mMark.setImageBitmap(mMarkIcon);
	    }else if (answer != null && !answer.IsCorrect()){
	        mMark.setImageBitmap(mUnMarkIcon);
	    }
    }

	private void doLayoutSubViews(Integer id){
    	if (mLinearLayoutAnswerArea != null){
    		mLinearLayoutAnswerArea.setVisibility(View.VISIBLE);
    	}
    	
    	if (mLinearLayoutOptions != null){
    		mLinearLayoutOptions.setVisibility(View.GONE);
    	}
    	
    	if (mLinearLayoutBlanks != null){
    		mLinearLayoutBlanks.setVisibility(View.GONE);
    	} 	
    	
    	if (mRatingBarHardness != null){
    		mRatingBarHardness.setVisibility(View.GONE);
    	}
    	
	    QuestionInfo question = mQuestionMgr.GetQuestionItem(id);
	    if (question.HasImage()){
	    	mLinearLayoutImage.setVisibility(View.VISIBLE);
	    	mImageView_1.setVisibility(View.VISIBLE);
	    }
	}
	
	private class PanelClickListener implements  View.OnClickListener{
		private onSubViewClickListener mSubViewListener = null;

		public PanelClickListener(onSubViewClickListener l){
			mSubViewListener = l;
		}
		
		public void onClick(View v) {
			if (v.getId() == R.id.imageViewMarker){
				ImageView imgMask = (ImageView)v;
				Integer qId = (Integer)imgMask.getTag();
				Answer answer = mHostActivity.getAnswerManager().GetAnswer(qId);
				answer.mIsMark = !answer.mIsMark;
				imgMask.setImageBitmap(answer.mIsMark ? mMarkIcon : mUnMarkIcon);
				mSubViewListener.onViewClick(qId, answer);
			}
		}
	}
}

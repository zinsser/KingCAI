package com.jczhou.kingcai.examination;

import com.jczhou.kingcai.R;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

public class QuestionItemViewHolder extends ItemViewHolder{
	protected ImageView mMark = null;
	protected RatingBar mRatingBarHardness = null;
	protected LinearLayout mLinearLayoutOptions = null;
	protected LinearLayout mLinearLayoutBlanks = null;
	protected ImageView mImageView_1 = null;
	protected ImageView mImageView_2 = null;
	public QuestionItemViewHolder(Context context, View rawView,
    		QuestionManager questionMgr, AnswerManager answerMgr) {
		super(context, rawView, questionMgr, answerMgr);
		
	    mMark = (ImageView) rawView.findViewById(R.id.imageViewMarker);
	    mRatingBarHardness = (RatingBar)rawView.findViewById(R.id.ratingBarHardness);
	    
	    mMark.setOnClickListener(mPanelListener);
	    
	    mLinearLayoutOptions = (LinearLayout)rawView.findViewById(R.id.linearLayoutOption);
	    mLinearLayoutBlanks = (LinearLayout)rawView.findViewById(R.id.linearLayoutBlanks);
	    
	    mImageView_1 = (ImageView)rawView.findViewById(R.id.imageView_1);
	    mImageView_2 = (ImageView)rawView.findViewById(R.id.imageView_2);	    
	}
	
	@Override
    public void doGettingItemView(Integer id, int fontSize){
    	super.doGettingItemView(id, fontSize);
    	if (mLinearLayoutAnswerArea != null){
    		mLinearLayoutAnswerArea.setVisibility(View.VISIBLE);
    	}
    	
    	if (mLinearLayoutOptions != null){
    		mLinearLayoutOptions.setVisibility(View.GONE);
    	}
    	
    	if (mLinearLayoutBlanks != null){
    		mLinearLayoutBlanks.setVisibility(View.GONE);
    	}
    }
	
	private View.OnClickListener mPanelListener = new View.OnClickListener() {
		
		public void onClick(View v) {
	    	//TODO:			
		}
	}; 
}

package com.jczhou.kingcai.examination;

import com.jczhou.kingcai.R;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class QuestionItemViewHolder extends ItemViewHolder{
	protected ImageView mMark;
	protected LinearLayout mTableLayoutAnswer;
	protected ImageView mImageView_1;
	protected ImageView mImageView_2;
	
	public QuestionItemViewHolder(Context context, View rawView,
    		QuestionManager questionMgr, AnswerManager answerMgr) {
		super(context, rawView, questionMgr, answerMgr);
	    mMark = (ImageView) rawView.findViewById(R.id.imageViewMarker);
	    mMark.setOnClickListener(mPanelListener);
	    mTableLayoutAnswer = (LinearLayout)rawView.findViewById(R.id.linearLayoutAnswerAera);
	}
	
	@Override
    public void doGettingItemView(Integer id, int fontSize){
    	super.doGettingItemView(id, fontSize);
    	//TODO:
    }
	
	private View.OnClickListener mPanelListener = new View.OnClickListener() {
		
		public void onClick(View v) {
	    	//TODO:			
		}
	}; 
}

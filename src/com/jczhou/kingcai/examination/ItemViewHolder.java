package com.jczhou.kingcai.examination;

import com.jczhou.kingcai.R;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemViewHolder{
   	protected TextView mTextDetail = null;
   	protected LinearLayout mLinearLayoutAnswerArea = null;
   	protected LinearLayout mLinearLayoutImage = null;
   	
   	protected QuestionManager mQuestionMgr = null;
   	protected AnswerManager mAnswerMgr = null;
    public ItemViewHolder(Context context, View rawView,
    		QuestionManager questionMgr, AnswerManager answerMgr){
        mTextDetail = (TextView) rawView.findViewById(R.id.txtQuestionDetail);
        mLinearLayoutAnswerArea = (LinearLayout)rawView.findViewById(R.id.linearLayoutAnswerAera);
        mLinearLayoutImage = (LinearLayout)rawView.findViewById(R.id.linearLayoutImage);
        
        if (mTextDetail != null){
        	mTextDetail.setOnClickListener(mPanelListener);
        }
        mQuestionMgr = questionMgr;
        mAnswerMgr = answerMgr;
    }
    
    public void doGettingItemView(Integer id, int fontSize){
    	if (mTextDetail != null){
	    	mTextDetail.setTextSize(fontSize);
	    	mTextDetail.setText(mQuestionMgr.GetQuestionItem(id).mDetail);
    	}
    	
    	if (mLinearLayoutAnswerArea != null){
    		mLinearLayoutAnswerArea.setVisibility(View.GONE);
    	}
    	
    	if (mLinearLayoutImage != null){
    		mLinearLayoutImage.setVisibility(View.GONE);
    	}    	
    }
    
    private View.OnClickListener mPanelListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			
		}
	};
}

package com.jczhou.kingcai.examination;

import com.jczhou.kingcai.R;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemViewHolder{
	protected PaperActivity mHostActivity = null;
	protected TextView mTextDetail = null;
   	protected LinearLayout mLinearLayoutAnswerArea = null;
   	protected LinearLayout mLinearLayoutImage = null;
   	
   	protected QuestionManager mQuestionMgr = null;
   	
   	public interface onSubViewClickListener{
   		public abstract void onViewClick(Integer questionID, final Answer answer);
   	}
   	
    public ItemViewHolder(PaperActivity hostActivity, View rawView,
    		QuestionManager questionMgr){
    	mHostActivity = hostActivity;
    	mTextDetail = (TextView) rawView.findViewById(R.id.txtQuestionDetail);
        mLinearLayoutAnswerArea = (LinearLayout)rawView.findViewById(R.id.linearLayoutAnswerAera);
        mLinearLayoutImage = (LinearLayout)rawView.findViewById(R.id.linearLayoutImage);
        
        mQuestionMgr = questionMgr;
    }
    
	private void doLayoutSubViews(){
    	if (mLinearLayoutAnswerArea != null){
    		mLinearLayoutAnswerArea.setVisibility(View.GONE);
    	}
    	
    	if (mLinearLayoutImage != null){
    		mLinearLayoutImage.setVisibility(View.GONE);
    	}
	}
	
    private void showTextDetail(Integer id, int fontSize){
    	if (mTextDetail != null){
	    	mTextDetail.setTextSize(fontSize);
	    	mTextDetail.setText(mQuestionMgr.GetQuestionItem(id).mDetail);
    	}
    }
    
    public void doGettingItemViews(Integer id, int fontSize, onSubViewClickListener listener){
    	doLayoutSubViews();
    	showTextDetail(id, fontSize);
    }  
    
    public void doGettingItemViews(Integer id, int fontSize){
    	doLayoutSubViews();
    	showTextDetail(id, fontSize);
    }
}

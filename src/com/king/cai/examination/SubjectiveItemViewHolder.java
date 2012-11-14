package com.king.cai.examination;

import com.king.cai.R;
import com.king.cai.examination.ItemViewHolder.onSubViewClickListener;

import android.view.View;
import android.widget.ImageView;

public class SubjectiveItemViewHolder extends QuestionItemViewHolder {
	private ImageView mImageViewAnswer = null;
	
	public SubjectiveItemViewHolder(PaperActivity hostActivity, View rawView,
			QuestionManager questionMgr) {
		super(hostActivity, rawView, questionMgr);
		mImageViewAnswer = (ImageView)rawView.findViewById(R.id.imageViewAnswer);
	}

	@Override
    public void doGettingItemViews(Integer index, int fontSize, onSubViewClickListener listener){
    	super.doGettingItemViews(index, fontSize, listener);
    	doLayoutSubViews();

    	mImageViewAnswer.setTag(index);
    	mImageViewAnswer.setOnClickListener(new PanelClickListener());
	}

	@Override
    public void doGettingItemViews(Integer index, int fontSize, boolean bShowRefAnswer){
     	super.doGettingItemViews(index, fontSize, bShowRefAnswer);
     	doLayoutSubViews();
	}
	
	private void doLayoutSubViews(){
    	if (mLinearLayoutSubjective != null){
    		mLinearLayoutSubjective.setVisibility(View.VISIBLE);
    	}		
	}	
	private class PanelClickListener implements  View.OnClickListener{		
		public void onClick(View v) {
			if (v.getId() == R.id.imageViewAnswer){			
				mHostActivity.prepareForSubjectiveItem(mImageViewAnswer);
			}
		}
	}
}

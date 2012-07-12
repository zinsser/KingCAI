package com.jczhou.kingcai.examination;

import android.view.View;
import android.widget.EditText;

import com.jczhou.kingcai.R;

public class BlankItemViewHolder extends QuestionItemViewHolder {
	private EditText mEditTextInput = null;
	public BlankItemViewHolder(PaperActivity hostActivity, View rawView,
    		QuestionManager questionMgr) {
		super(hostActivity, rawView, questionMgr);
		mEditTextInput = (EditText)rawView.findViewById(R.id.editTextAnswer);
	}

	@Override
    public void doGettingItemViews(Integer id, int fontSize, onSubViewClickListener listener){
    	super.doGettingItemViews(id, fontSize, listener);
    	doLayoutSubViews();
    	mLinearLayoutBlanks.setTag(id);
    	
		String tipQuestion = String.format(mHostActivity.getResources().getString(R.string.CurrentQuestion), id);    	
    	mEditTextInput.setHint(tipQuestion);
	}

	@Override
    public void doGettingItemViews(Integer id, int fontSize){
		super.doGettingItemViews(id, fontSize);
    	doLayoutSubViews();		
		mEditTextInput.setEnabled(false);
	}
	
	private void doLayoutSubViews(){
    	if (mLinearLayoutBlanks != null){
    		mLinearLayoutBlanks.setVisibility(View.VISIBLE);
    	}		
	}	
}

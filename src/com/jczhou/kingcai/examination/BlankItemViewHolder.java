package com.jczhou.kingcai.examination;

import com.jczhou.kingcai.R;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class BlankItemViewHolder extends QuestionItemViewHolder {
	private EditText mEditTextInput = null;
	public BlankItemViewHolder(Context context, View rawView,
    		QuestionManager questionMgr, AnswerManager answerMgr) {
		super(context, rawView, questionMgr, answerMgr);
		mEditTextInput = (EditText)rawView.findViewById(R.id.editTextAnswer);
	}

	@Override
	public void doGettingItemView(Integer id, int fontSize){
		super.doGettingItemView(id, fontSize);
		
    	if (mLinearLayoutBlanks != null){
    		mLinearLayoutBlanks.setVisibility(View.VISIBLE);
    	}
	}
}

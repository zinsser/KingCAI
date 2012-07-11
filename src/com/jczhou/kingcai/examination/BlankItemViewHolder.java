package com.jczhou.kingcai.examination;

import android.content.Context;
import android.view.View;

public class BlankItemViewHolder extends QuestionItemViewHolder {

	public BlankItemViewHolder(Context context, View rawView,
    		QuestionManager questionMgr, AnswerManager answerMgr) {
		super(context, rawView, questionMgr, answerMgr);

	}

	@Override
	public void doGettingItemView(Integer id, int fontSize){
		super.doGettingItemView(id, fontSize);
	}
}

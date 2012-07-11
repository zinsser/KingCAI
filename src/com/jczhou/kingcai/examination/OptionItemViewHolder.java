package com.jczhou.kingcai.examination;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

import com.jczhou.kingcai.R;

public class OptionItemViewHolder extends QuestionItemViewHolder {
	private RadioButton mRadioButtonA;
	private RadioButton mRadioButtonB;
	private RadioButton mRadioButtonC;
	private RadioButton mRadioButtonD;

	public OptionItemViewHolder(Context context, View rawView,
    		QuestionManager questionMgr, AnswerManager answerMgr){
	    super(context, rawView, questionMgr, answerMgr);

	    mRadioButtonA = (RadioButton)rawView.findViewById(R.id.radioBtnA);
	    mRadioButtonB = (RadioButton)rawView.findViewById(R.id.radioBtnB);
	    mRadioButtonC = (RadioButton)rawView.findViewById(R.id.radioBtnC);
	    mRadioButtonD = (RadioButton)rawView.findViewById(R.id.radioBtnD);
	
	    mRadioButtonA.setOnClickListener(mPanelListener);
	    mRadioButtonB.setOnClickListener(mPanelListener);
	    mRadioButtonC.setOnClickListener(mPanelListener);
	    mRadioButtonD.setOnClickListener(mPanelListener);
	}

	@Override	
    public void doGettingItemView(Integer id, int fontSize){
    	super.doGettingItemView(id, fontSize);
    	if (mLinearLayoutOptions != null){
    		mLinearLayoutOptions.setVisibility(View.VISIBLE);
    	}
    }	
    
    private View.OnClickListener mPanelListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			
		}
	};    
}

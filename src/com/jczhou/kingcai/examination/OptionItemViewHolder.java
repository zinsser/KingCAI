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

	public OptionItemViewHolder(Context context, View rawView){
	    super(context, rawView);

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
    public void doGettingItemView(){
    	super.doGettingItemView();
    	
    }	
    
    private View.OnClickListener mPanelListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			
		}
	};    
}

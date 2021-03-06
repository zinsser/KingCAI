package com.king.cai.examination;

import android.graphics.Color;
import android.os.Parcel;
import android.view.View;
import android.widget.RadioButton;

import com.king.cai.R;

public class OptionItemViewHolder extends QuestionItemViewHolder {
	private RadioButton mRadioButtonA;
	private RadioButton mRadioButtonB;
	private RadioButton mRadioButtonC;
	private RadioButton mRadioButtonD;

	public OptionItemViewHolder(PaperActivity hostActivity, View rawView,
    		QuestionManager questionMgr){
	    super(hostActivity, rawView, questionMgr);

	    mRadioButtonA = (RadioButton)rawView.findViewById(R.id.radioBtnA);
	    mRadioButtonB = (RadioButton)rawView.findViewById(R.id.radioBtnB);
	    mRadioButtonC = (RadioButton)rawView.findViewById(R.id.radioBtnC);
	    mRadioButtonD = (RadioButton)rawView.findViewById(R.id.radioBtnD);
	}

	@Override
    public void doGettingItemViews(Integer index, int fontSize, onSubViewClickListener listener){
    	super.doGettingItemViews(index, fontSize, listener);
    	doLayoutSubViews();

    	PanelClickListener panelListener = new PanelClickListener(listener);

     	setRadioButtonInfo(mRadioButtonA, index, panelListener);
     	setRadioButtonInfo(mRadioButtonB, index, panelListener);
     	setRadioButtonInfo(mRadioButtonC, index, panelListener);
     	setRadioButtonInfo(mRadioButtonD, index, panelListener);
     	Answer answer = mHostActivity.getAnswerManager().getAnswer(mQuestionMgr.getIdByIndex(index));
     	if (answer != null){
	    	Parcel parcelValues  = answer.getAnswer(); 
	    	if (parcelValues != null){
	        	boolean[] bValue = parcelValues.createBooleanArray();
	        	assert bValue.length == 4;
	        	mRadioButtonA.setChecked(bValue[0]);
	        	mRadioButtonB.setChecked(bValue[1]);
	        	mRadioButtonC.setChecked(bValue[2]);       	
	        	mRadioButtonD.setChecked(bValue[3]);
	    	}
		}     	
	}
	
	@Override
    public void doGettingItemViews(Integer index, int fontSize, boolean bShowRefAnswer){
     	super.doGettingItemViews(index, fontSize, bShowRefAnswer);
     	doLayoutSubViews();
     	
     	mRadioButtonA.setEnabled(false);
     	mRadioButtonB.setEnabled(false);
     	mRadioButtonC.setEnabled(false);
     	mRadioButtonD.setEnabled(false);
     	
     	AnswerManager answerMgr = mHostActivity.getAnswerManager();
     	Answer answer = answerMgr.getAnswer(mQuestionMgr.getIdByIndex(index));
     	if (answer != null){
	    	Parcel parcelValues  = answer.getAnswer(); 
	    	if (parcelValues != null){
	        	boolean[] bValue = parcelValues.createBooleanArray();
	        	assert bValue.length == 4;
	        	mRadioButtonA.setChecked(bValue[0]);
	        	mRadioButtonB.setChecked(bValue[1]);
	        	mRadioButtonC.setChecked(bValue[2]);       	
	        	mRadioButtonD.setChecked(bValue[3]);
	    	}
		}
     	
     	if (!bShowRefAnswer){
     		return;
     	}
    	int colorNormal = Color.rgb(0, 0, 0); 
    	int colorBackground = Color.rgb(232, 232, 232);
    	
    	setRadioButtonInfo(mRadioButtonA, colorNormal, colorBackground);
    	setRadioButtonInfo(mRadioButtonB, colorNormal, colorBackground);
    	setRadioButtonInfo(mRadioButtonC, colorNormal, colorBackground);
    	setRadioButtonInfo(mRadioButtonD, colorNormal, colorBackground);
   		
    	if (answer!= null && !answer.isCorrect()){
    		Parcel parcelValues = answer.getRefAnswer();
        	if (parcelValues != null){
	        	boolean[] bValue = parcelValues.createBooleanArray();
	        	assert bValue.length == 4;
	    
	        	int RectifyFontColor = Color.rgb(248, 254, 131);	 
	        	int RectifyBackground = Color.rgb(0, 128, 0);
	        	if (bValue[0]) {
	            	setRadioButtonInfo(mRadioButtonA, RectifyFontColor, RectifyBackground);	        	
	            }
	           	if (bValue[1]) {
	           		setRadioButtonInfo(mRadioButtonB, RectifyFontColor, RectifyBackground);	           		
	           	}
	           	if (bValue[2]) {
	           		setRadioButtonInfo(mRadioButtonC, RectifyFontColor, RectifyBackground);	           		
	           	}
	           	if (bValue[3]) {
	           		setRadioButtonInfo(mRadioButtonD, RectifyFontColor, RectifyBackground);	           		
	           	}
        	}
    	}
    }	

	private void setRadioButtonInfo(RadioButton btn, Integer index, PanelClickListener listener){
		btn.setTag(index);
		btn.setOnClickListener(listener);
	}
	
	private void setRadioButtonInfo(RadioButton btn, int fontColor, int bgColor){
		btn.setTextColor(fontColor);
		btn.setBackgroundColor(bgColor);		
	}

	private void doLayoutSubViews(){
    	if (mLinearLayoutOptions != null){
    		mLinearLayoutOptions.setVisibility(View.VISIBLE);
    	}		
	}

	private class PanelClickListener implements  View.OnClickListener{
		private onSubViewClickListener mSubViewListener = null;

		public PanelClickListener(onSubViewClickListener l){
			mSubViewListener = l;
		}	
		
		public void onClick(View v) {
			if (v.getId() == R.id.radioBtnA
					|| v.getId() == R.id.radioBtnB
					|| v.getId() == R.id.radioBtnC
					|| v.getId() == R.id.radioBtnD){
				RadioButton radioBtn = (RadioButton)v;
				Integer index = (Integer)radioBtn.getTag();
				Answer answer = mHostActivity.getAnswerManager().getAnswer(mQuestionMgr.getIdByIndex(index));
				boolean[] bRadioValue = answer.getAnswer().createBooleanArray();
				assert bRadioValue.length == 4;
				boolean bChecked = false;
				if (v.getId() == R.id.radioBtnA){
					bRadioValue[0] = !bRadioValue[0];
					bChecked = bRadioValue[0];
				}else if (v.getId() == R.id.radioBtnB){
					bRadioValue[1] = !bRadioValue[1];
					bChecked = bRadioValue[1];
				}else if (v.getId() == R.id.radioBtnC){
					bRadioValue[2] = !bRadioValue[2];
					bChecked = bRadioValue[2];
				}else if (v.getId() == R.id.radioBtnD){
					bRadioValue[3] = !bRadioValue[3];
					bChecked = bRadioValue[3];
				}
				radioBtn.setChecked(bChecked);
				
				Parcel answers = Parcel.obtain();
				answers.writeBooleanArray(bRadioValue);
				answer.addAnswer(answers);
				
				mSubViewListener.onViewClick(index, answer);					
			}
		}
	};    
}

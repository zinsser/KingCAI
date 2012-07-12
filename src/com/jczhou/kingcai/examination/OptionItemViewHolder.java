package com.jczhou.kingcai.examination;

import android.graphics.Color;
import android.os.Parcel;
import android.view.View;
import android.widget.RadioButton;

import com.jczhou.kingcai.R;

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
    public void doGettingItemViews(Integer id, int fontSize, onSubViewClickListener listener){
    	super.doGettingItemViews(id, fontSize, listener);
    	doLayoutSubViews();

    	PanelClickListener panelListener = new PanelClickListener(listener);

     	setRadioButtonInfo(mRadioButtonA, id, panelListener);
     	setRadioButtonInfo(mRadioButtonB, id, panelListener);
     	setRadioButtonInfo(mRadioButtonC, id, panelListener);
     	setRadioButtonInfo(mRadioButtonD, id, panelListener);
     	Answer answer = mHostActivity.getAnswerManager().GetAnswer(id);
     	if (answer != null){
	    	Parcel parcelValues  = answer.GetAnswer(); 
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

	private void setRadioButtonInfo(RadioButton btn, Integer id, PanelClickListener listener){
		btn.setTag(id);
		btn.setOnClickListener(listener);
	}
	
	@Override
    public void doGettingItemViews(Integer id, int fontSize){
     	super.doGettingItemViews(id, fontSize);
     	doLayoutSubViews();
     	
     	mRadioButtonA.setEnabled(false);
     	mRadioButtonB.setEnabled(false);
     	mRadioButtonC.setEnabled(false);
     	mRadioButtonD.setEnabled(false);
     	
     	AnswerManager answerMgr = mHostActivity.getAnswerManager();
     	Answer answer = answerMgr.GetAnswer(id);
     	if (answer != null){
	    	Parcel parcelValues  = answer.GetAnswer(); 
	    	if (parcelValues != null){
	        	boolean[] bValue = parcelValues.createBooleanArray();
	        	assert bValue.length == 4;
	        	mRadioButtonA.setChecked(bValue[0]);
	        	mRadioButtonB.setChecked(bValue[1]);
	        	mRadioButtonC.setChecked(bValue[2]);       	
	        	mRadioButtonD.setChecked(bValue[3]);
	    	}
		}
     	
    	int colorNormal = Color.rgb(0, 0, 0); 
    	int colorBackground = Color.rgb(220, 220, 220);
    	
    	setRadioButtonInfo(mRadioButtonA, colorNormal, colorBackground);
    	setRadioButtonInfo(mRadioButtonB, colorNormal, colorBackground);
    	setRadioButtonInfo(mRadioButtonC, colorNormal, colorBackground);
    	setRadioButtonInfo(mRadioButtonD, colorNormal, colorBackground);
   		
    	if (answer!= null && !answer.IsCorrect()){
    		Parcel parcelValues = answer.GetRefAnswer();
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
				Integer qId = (Integer)radioBtn.getTag();
				Answer answer = mHostActivity.getAnswerManager().GetAnswer(qId);
				boolean[] bRadioValue = answer.GetAnswer().createBooleanArray();
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
				answer.AddAnswer(answers);
				
				mSubViewListener.onViewClick(qId, answer);					
			}
		}
	};    
}

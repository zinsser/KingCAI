package com.jczhou.kingcai.examination;

import java.util.ArrayList;

import android.os.Parcel;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.jczhou.kingcai.R;

public class BlankItemViewHolder extends QuestionItemViewHolder {
	private EditText mEditTextor = null;
	
	public BlankItemViewHolder(PaperActivity hostActivity, View rawView,
    		QuestionManager questionMgr) {
		super(hostActivity, rawView, questionMgr);
		mEditTextor = (EditText)rawView.findViewById(R.id.editTextAnswer);
	}

	private class TagParam{
		private Integer mId;
		private Integer mSubId;
		public TagParam(Integer qid, Integer sid){
			mId = qid;
			mSubId = sid;
		}
	}
	
	@Override
    public void doGettingItemViews(Integer id, int fontSize, onSubViewClickListener listener){
    	super.doGettingItemViews(id, fontSize, listener);
    	doLayoutSubViews();

		BlankInputListener inputListener = new BlankInputListener();
		mEditTextor.setOnEditorActionListener(inputListener);
		
    	if (mQuestionMgr.GetQuestionItem(id).GetType() == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
    		String tipQuestion = String.format(mHostActivity.getResources().getString(R.string.CurrentBlankQuestion), id, 1);
    		mEditTextor.setHint(tipQuestion);
    		mEditTextor.setTag(new TagParam(id, 1));
    		mEditTextor.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    		Parcel parcelValues  = mHostActivity.getAnswerManager().GetAnswer(id).GetRefAnswer();
    		Integer cnt = (Integer)parcelValues.readInt();
    		int i = 1;
    		for (; mLinearLayoutBlanks.getChildCount() < cnt && i < cnt; ++i){
    			tipQuestion = String.format(mHostActivity.getResources().getString(R.string.CurrentBlankQuestion), id, i + 1);
    			EditText editText = new EditText(mHostActivity);
    	    	editText.setHint(tipQuestion);
    	    	editText.setTag(new TagParam(id, i + 1));
    	    	editText.setOnEditorActionListener(inputListener);
        		mEditTextor.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        		mLinearLayoutBlanks.addView(editText);
    		}
    		((EditText)mLinearLayoutBlanks.getChildAt(i-1)).setImeOptions(EditorInfo.IME_ACTION_NEXT);
    	}else{
    		String tipQuestion = String.format(mHostActivity.getResources().getString(R.string.CurrentQuestion), id);    	
    		mEditTextor.setHint(tipQuestion);
    		mEditTextor.setTag(id);
    		mEditTextor.setImeOptions(EditorInfo.IME_ACTION_DONE);
    	}
	}

	@Override
    public void doGettingItemViews(Integer id, int fontSize){
		super.doGettingItemViews(id, fontSize);
    	doLayoutSubViews();	

    	if (mQuestionMgr.GetQuestionItem(id).GetType() == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
        	mEditTextor.setEnabled(false);
    		mEditTextor.setTag(1);
    		
    		Parcel parcelValues  = mHostActivity.getAnswerManager().GetAnswer(id).GetRefAnswer();
    		Integer cnt = (Integer)parcelValues.readInt();
    		for (int i = 1; mLinearLayoutBlanks.getChildCount() < cnt && i < cnt; ++i){
    			EditText editText = new EditText(mHostActivity);
    			editText.setEnabled(false);
    	    	editText.setTag(i + 1);
    	    	mLinearLayoutBlanks.addView(editText);
    		}
    	}else{
        	mEditTextor.setEnabled(false);
    	}    	
	}
	
	private void doLayoutSubViews(){
    	if (mLinearLayoutBlanks != null){
    		mLinearLayoutBlanks.setVisibility(View.VISIBLE);
    	}		
	}	
	
    private class BlankInputListener implements OnEditorActionListener, OnFocusChangeListener{
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED 
					|| actionId == EditorInfo.IME_ACTION_DONE){
				if (v.getText().toString().length() != 0){
					Integer subId = ((TagParam)v.getTag()).mSubId;

				}
			}else if (actionId == EditorInfo.IME_ACTION_NEXT){
				Integer qID = (Integer)mLinearLayoutBlanks.getTag();
				QuestionInfo info = mQuestionMgr.GetQuestionItem(qID);
				if (info != null && info.GetType() == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
					EditText et = ((EditText)v);
					Integer subId = ((TagParam)v.getTag()).mSubId;
				}
			}
			return false;
		}

		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			Log.d("BlankItemViewHolder", "onFocusChanged");
		}
    }
}

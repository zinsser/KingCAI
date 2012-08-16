package com.king.cai.examination;

import java.util.HashMap;
import java.util.Iterator;

import android.graphics.Color;
import android.os.Parcel;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.king.cai.R;

public class BlankItemViewHolder extends QuestionItemViewHolder {
	private EditText mEditTextor = null;

	public BlankItemViewHolder(PaperActivity hostActivity, View rawView,
    		QuestionManager questionMgr) {
		super(hostActivity, rawView, questionMgr);
		mEditTextor = (EditText)rawView.findViewById(R.id.editTextAnswer);
	}

	private class TagParam{
		public Integer mPriIndex;
		public Integer mSubIndex;
		public onSubViewClickListener mListener;
		public TagParam(Integer priIndex, Integer subIndex, onSubViewClickListener l){
			mPriIndex = priIndex;
			mSubIndex = subIndex;
			mListener = l;
		}
	}
	
	@Override
    public void doGettingItemViews(Integer index, int fontSize, onSubViewClickListener listener){
		super.doGettingItemViews(index, fontSize, listener);
    	doLayoutSubViews();

		BlankInputListener inputListener = new BlankInputListener();
		mEditTextor.setOnEditorActionListener(inputListener);
		mEditTextor.setOnFocusChangeListener(inputListener);
		if (mQuestionMgr.getQuestionItem(index).getType() == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
			TagParam tagFirst = new TagParam(index, 1, listener);
			String resFormat = mHostActivity.getResources().getString(R.string.CurrentBlankQuestion);
    		String tipQuestion = String.format(resFormat, tagFirst.mPriIndex, tagFirst.mSubIndex);
    		mEditTextor.setHint(tipQuestion);
    		mEditTextor.setTag(tagFirst);
    		mEditTextor.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    		String id = mQuestionMgr.getIdByIndex(index);
    		
    		Parcel parcelValues  = mHostActivity.getAnswerManager().getAnswer(id).getRefAnswer();
    		Integer cnt = (Integer)parcelValues.readInt();
    		int i = 1;
    		for (; mLinearLayoutBlanks.getChildCount() < cnt && i < cnt; ++i){
    			TagParam tagNext = new TagParam(index, i+1, listener);
    			tipQuestion = String.format(resFormat, tagNext.mPriIndex, tagNext.mSubIndex);
    			EditText editText = new EditText(mHostActivity);
    	    	editText.setHint(tipQuestion);
    	    	editText.setTag(tagNext);
    	    	editText.setOnEditorActionListener(inputListener);
    	    	editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    	    	editText.setSingleLine(false);
    	    	editText.setOnFocusChangeListener(inputListener);
    	    	
    	    	mLinearLayoutBlanks.addView(editText);  	    	
    		}
    		((EditText)mLinearLayoutBlanks.getChildAt(i-1)).setImeOptions(EditorInfo.IME_ACTION_DONE);
    	}else{
    		TagParam tagAlone = new TagParam(index, 0, listener);
    		String resFormat = mHostActivity.getResources().getString(R.string.CurrentQuestion);
    		String tipQuestion = String.format(resFormat, tagAlone.mPriIndex);    	
    		mEditTextor.setHint(tipQuestion);
    		mEditTextor.setTag(tagAlone);
    		mEditTextor.setImeOptions(EditorInfo.IME_ACTION_DONE);
    	}
	}

	@Override
    public void doGettingItemViews(Integer index, int fontSize, boolean bShowRefAnswer){
		super.doGettingItemViews(index, fontSize, bShowRefAnswer);
    	doLayoutSubViews();	
    	doLoadAnswers(mQuestionMgr.getIdByIndex(index));
    	int okTextColor = Color.rgb(0, 128, 0);
    	int errTextColor = Color.rgb(220, 0, 0);
    	LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	if (mQuestionMgr.getQuestionItem(index).getType() == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
			TagParam tagFirst = new TagParam(index, 1, null);    		
        	mEditTextor.setEnabled(false);
        	mEditTextor.setText(mMultiBlankAnswer.get(Integer.toString(1)));
        	mEditTextor.setBackgroundResource(R.drawable.reference_bk);        	
    		mEditTextor.setTag(tagFirst);
    		if (bShowRefAnswer){
	    		if (mMultiBlankAnswer != null && mMultiBlankRefAnswer != null
		    			&& mMultiBlankAnswer.get(1) != null && mMultiBlankRefAnswer.get(1) != null
		    			&& !mMultiBlankAnswer.get(1).equals(mMultiBlankRefAnswer.get(1))){
	    			mEditTextor.setTextColor(errTextColor);
	    		}
	    		if (mLinearLayoutBlanks.getChildCount() < 2){
		    		TextView refText = new TextView(mHostActivity);
		    		refText.setLayoutParams(param);
		    		refText.setText(mMultiBlankRefAnswer.get(Integer.toString(1)));
		    		refText.setPadding(10, 0, 0, 0);
		    		refText.setTextColor(okTextColor);
			    	mLinearLayoutBlanks.addView(refText);    		
	    		}
    		}
	    	Parcel parcelValues  = mHostActivity.getAnswerManager().getAnswer(mQuestionMgr.getIdByIndex(index)).getRefAnswer();
    		Integer cnt = (Integer)parcelValues.readInt();
    		if (bShowRefAnswer){
    			cnt = 2 * cnt;
    		}
    		for (int i = 1; mLinearLayoutBlanks.getChildCount() < cnt 
    				&& i < cnt; ++i){
    			TagParam tagNext = new TagParam(index, i+1, null);
    			EditText editText = new EditText(mHostActivity);
    			editText.setEnabled(false);
    	    	editText.setTag(tagNext);
    	    	editText.setSingleLine(false);
    	    	editText.setText(mMultiBlankAnswer.get(tagNext.mSubIndex));
    	    	editText.setBackgroundResource(R.drawable.reference_bk);
    	    	mLinearLayoutBlanks.addView(editText);
    	    	if (bShowRefAnswer){
	    	    	if (mMultiBlankAnswer != null && mMultiBlankRefAnswer != null
	    	    			&& mMultiBlankAnswer.get(tagNext.mSubIndex) != null && mMultiBlankRefAnswer.get(tagNext.mSubIndex) != null
	    	    			&& !mMultiBlankAnswer.get(tagNext.mSubIndex).equals(mMultiBlankRefAnswer.get(tagNext.mSubIndex))){
	    	    		editText.setTextColor(errTextColor);
	    	    	}
	    	    	TextView refText2 = new TextView(mHostActivity);
		    		refText2.setLayoutParams(param);
	    	    	refText2.setText(mMultiBlankRefAnswer.get(tagNext.mSubIndex));
	    	    	refText2.setTextColor(okTextColor);
	    	    	refText2.setPadding(10, 0, 0, 0);
	    	    	mLinearLayoutBlanks.addView(refText2);
    	    	}
    		}
    	}else{
    		TagParam tagAlone = new TagParam(index, 0, null);
        	mEditTextor.setEnabled(false);
        	mEditTextor.setText(mMultiBlankAnswer.get(tagAlone.mSubIndex));
        	mEditTextor.setTag(tagAlone);
        	mEditTextor.setBackgroundResource(R.drawable.reference_bk);
        	if (bShowRefAnswer){
	    		if (mMultiBlankAnswer != null && mMultiBlankRefAnswer != null
		    			&& mMultiBlankAnswer.get(tagAlone.mSubIndex) != null && mMultiBlankRefAnswer.get(tagAlone.mSubIndex) != null
		    			&& !mMultiBlankAnswer.get(tagAlone.mSubIndex).equals(mMultiBlankRefAnswer.get(tagAlone.mSubIndex))){
	    			mEditTextor.setTextColor(errTextColor);
	    		}
	    		if (mLinearLayoutBlanks.getChildCount() < 2){
		    		TextView refText = new TextView(mHostActivity);
		    		refText.setLayoutParams(param);
		    		refText.setText(mMultiBlankRefAnswer.get(tagAlone.mSubIndex));
		    		refText.setTextColor(okTextColor);
		    		refText.setPadding(10, 0, 0, 0);
			    	mLinearLayoutBlanks.addView(refText);
	    		}
        	}
    	}    	
	}
	
	private void doLayoutSubViews(){
    	if (mLinearLayoutBlanks != null){
    		mLinearLayoutBlanks.setVisibility(View.VISIBLE);
    	}
	}
	
    private HashMap<Integer, String> mMultiBlankAnswer = new HashMap<Integer, String>();
    private HashMap<Integer, String> mMultiBlankRefAnswer = new HashMap<Integer, String>();
    
    private class BlankInputListener implements OnEditorActionListener, OnFocusChangeListener{
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			TagParam param = (TagParam)v.getTag();
			Integer priIndex = param.mPriIndex;
			Integer subIndex = param.mSubIndex;
			onSubViewClickListener listener = param.mListener;
			
			if ((actionId == EditorInfo.IME_ACTION_UNSPECIFIED 
					|| actionId == EditorInfo.IME_ACTION_DONE)
					&& event.equals(KeyEvent.ACTION_DOWN)){
				mMultiBlankAnswer.put(subIndex, v.getText().toString());
				doSaveAnswers(priIndex, listener);
				return true;
			}else if (actionId == EditorInfo.IME_ACTION_NEXT
					&& event.equals(KeyEvent.ACTION_DOWN)){
				QuestionInfo info = mQuestionMgr.getQuestionItem(priIndex);
				if (info != null && info.getType() == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
					mMultiBlankAnswer.put(subIndex, v.getText().toString());					
				}
				return true;
			}
			return false;
		}
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus){
				TagParam param = (TagParam)((EditText)v).getTag();
				Integer priIndex = param.mPriIndex;
				Integer subIndex = param.mSubIndex;
				onSubViewClickListener listener = param.mListener;
				
				mMultiBlankAnswer.put(subIndex, ((EditText)v).getText().toString());				
				doSaveAnswers(priIndex, listener);				
			}
		}	
    }

	private void doSaveAnswers(Integer index, onSubViewClickListener l){
		String id = mQuestionMgr.getIdByIndex(index);
		Parcel answer = Parcel.obtain();
		answer.writeInt(mMultiBlankAnswer.size());
		for (Iterator<Integer> iter = mMultiBlankAnswer.keySet().iterator(); 
				iter.hasNext(); ){
			Integer subid = iter.next();
			answer.writeInt(subid);
			answer.writeString(mMultiBlankAnswer.get(subid));			
		}
		answer.setDataPosition(0);
		mHostActivity.getAnswerManager().getAnswer(id).addAnswer(answer);
		if (l != null){
			l.onViewClick(index, mHostActivity.getAnswerManager().getAnswer(id));
		}
	}	    
    
    private void doLoadAnswers(String qId){
		Parcel refContent = mHostActivity.getAnswerManager().getAnswer(qId).getRefAnswer();
		Integer cntRef = refContent.readInt();

		mMultiBlankRefAnswer.clear();
		for (int i = 0; i < cntRef; ++i){
			Integer subIndex = refContent.readInt();
			String answer = refContent.readString();
			mMultiBlankRefAnswer.put(subIndex, answer);
		}

		Parcel answerContent = mHostActivity.getAnswerManager().getAnswer(qId).getAnswer();
		Integer cntAnswer = answerContent.readInt();

		mMultiBlankAnswer.clear();
		for (int i = 0; i < cntAnswer; ++i){
			Integer subIndex = answerContent.readInt();
			String answer = answerContent.readString();
			mMultiBlankAnswer.put(subIndex, answer);
		}
    }
}

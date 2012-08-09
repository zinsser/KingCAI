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
		private String mId;
		private String mSubId;
		public TagParam(String qid, String sid){
			mId = qid;
			mSubId = sid;
		}
	}
	
	@Override
    public void doGettingItemViews(String id, int fontSize, onSubViewClickListener listener){
		super.doGettingItemViews(id, fontSize, listener);
    	doLayoutSubViews();

		BlankInputListener inputListener = new BlankInputListener();
		mEditTextor.setOnEditorActionListener(inputListener);
		mEditTextor.setOnFocusChangeListener(inputListener);
		if (mQuestionMgr.GetQuestionItem(id).GetType() == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
    		String tipQuestion = String.format(mHostActivity.getResources().getString(R.string.CurrentBlankQuestion), id, 1);
    		mEditTextor.setHint(tipQuestion);
    		mEditTextor.setTag(new TagParam(id, Integer.toString(1)));
    		mEditTextor.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    		Parcel parcelValues  = mHostActivity.getAnswerManager().GetAnswer(id).GetRefAnswer();
    		Integer cnt = (Integer)parcelValues.readInt();
    		int i = 1;
    		for (; mLinearLayoutBlanks.getChildCount() < cnt && i < cnt; ++i){
    			tipQuestion = String.format(mHostActivity.getResources().getString(R.string.CurrentBlankQuestion), id, i + 1);
    			EditText editText = new EditText(mHostActivity);
    	    	editText.setHint(tipQuestion);
    	    	editText.setTag(new TagParam(id, Integer.toString(i + 1)));
    	    	editText.setOnEditorActionListener(inputListener);
    	    	editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    	    	editText.setSingleLine(false);
    	    	editText.setMaxLines(3);
    	    	editText.setOnFocusChangeListener(inputListener);
    	    	mLinearLayoutBlanks.addView(editText);  	    	
    		}
    		((EditText)mLinearLayoutBlanks.getChildAt(i-1)).setImeOptions(EditorInfo.IME_ACTION_NEXT);
    	}else{
    		String tipQuestion = String.format(mHostActivity.getResources().getString(R.string.CurrentQuestion), id);    	
    		mEditTextor.setHint(tipQuestion);
    		mEditTextor.setTag(new TagParam(id, Integer.toString(0)));
    		mEditTextor.setImeOptions(EditorInfo.IME_ACTION_DONE);
    	}
	}

	@Override
    public void doGettingItemViews(String id, int fontSize, boolean bShowRefAnswer){
		super.doGettingItemViews(id, fontSize, bShowRefAnswer);
    	doLayoutSubViews();	
    	doLoadAnswers(id);
    	int okTextColor = Color.rgb(0, 128, 0);
    	int errTextColor = Color.rgb(220, 0, 0);
    	LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	if (mQuestionMgr.GetQuestionItem(id).GetType() == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
        	mEditTextor.setEnabled(false);
        	mEditTextor.setText(mMultiBlankAnswer.get(Integer.toString(1)));
        	mEditTextor.setBackgroundResource(R.drawable.reference_bk);        	
    		mEditTextor.setTag(new TagParam(id, Integer.toString(1)));
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
	    	Parcel parcelValues  = mHostActivity.getAnswerManager().GetAnswer(id).GetRefAnswer();
    		Integer cnt = (Integer)parcelValues.readInt();
    		if (bShowRefAnswer){
    			cnt = 2 * cnt;
    		}
    		for (int i = 1; mLinearLayoutBlanks.getChildCount() < cnt 
    				&& i < cnt; ++i){
    			String subid = Integer.toString(i + 1);
    			EditText editText = new EditText(mHostActivity);
    			editText.setEnabled(false);
    	    	editText.setTag(new TagParam(id, subid));
    	    	editText.setSingleLine(false);
    	    	editText.setMaxLines(3);
    	    	editText.setText(mMultiBlankAnswer.get(subid));
    	    	editText.setBackgroundResource(R.drawable.reference_bk);
    	    	mLinearLayoutBlanks.addView(editText);
    	    	if (bShowRefAnswer){
	    	    	if (mMultiBlankAnswer != null && mMultiBlankRefAnswer != null
	    	    			&& mMultiBlankAnswer.get(subid) != null && mMultiBlankRefAnswer.get(subid) != null
	    	    			&& !mMultiBlankAnswer.get(subid).equals(mMultiBlankRefAnswer.get(subid))){
	    	    		editText.setTextColor(errTextColor);
	    	    	}
	    	    	TextView refText2 = new TextView(mHostActivity);
		    		refText2.setLayoutParams(param);
	    	    	refText2.setText(mMultiBlankRefAnswer.get(subid));
	    	    	refText2.setTextColor(okTextColor);
	    	    	refText2.setPadding(10, 0, 0, 0);
	    	    	mLinearLayoutBlanks.addView(refText2);
    	    	}
    		}
    	}else{
    		String subid = Integer.toString(0);
        	mEditTextor.setEnabled(false);
        	mEditTextor.setText(mMultiBlankAnswer.get(subid));
        	mEditTextor.setTag(new TagParam(id, Integer.toString(0)));
        	mEditTextor.setBackgroundResource(R.drawable.reference_bk);
        	if (bShowRefAnswer){
	    		if (mMultiBlankAnswer != null && mMultiBlankRefAnswer != null
		    			&& mMultiBlankAnswer.get(subid) != null && mMultiBlankRefAnswer.get(subid) != null
		    			&& !mMultiBlankAnswer.get(subid).equals(mMultiBlankRefAnswer.get(subid))){
	    			mEditTextor.setTextColor(errTextColor);
	    		}
	    		if (mLinearLayoutBlanks.getChildCount() < 2){
		    		TextView refText = new TextView(mHostActivity);
		    		refText.setLayoutParams(param);
		    		refText.setText(mMultiBlankRefAnswer.get(subid));
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
	
    private HashMap<String, String> mMultiBlankAnswer = new HashMap<String, String>();
    private HashMap<String, String> mMultiBlankRefAnswer = new HashMap<String, String>();
    
    private class BlankInputListener implements OnEditorActionListener, OnFocusChangeListener{
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			TagParam param = (TagParam)v.getTag();
			String qId = param.mId;
			String subId = param.mSubId;
			
			if ((actionId == EditorInfo.IME_ACTION_UNSPECIFIED 
					|| actionId == EditorInfo.IME_ACTION_DONE)
					&& event.equals(KeyEvent.ACTION_DOWN)){
				mMultiBlankAnswer.put(subId, new String(v.getText().toString()));				
				doSaveAnswers(qId);
				return true;
			}else if (actionId == EditorInfo.IME_ACTION_NEXT
					&& event.equals(KeyEvent.ACTION_DOWN)){
				QuestionInfo info = mQuestionMgr.GetQuestionItem(qId);
				if (info != null && info.GetType() == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
					mMultiBlankAnswer.put(subId, new String(v.getText().toString()));					
				}
				return true;
			}
			return false;
		}
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus){
				TagParam param = (TagParam)((EditText)v).getTag();
				String qId = param.mId;
				String subId = param.mSubId;
				mMultiBlankAnswer.put(subId, new String(((EditText)v).getText().toString()));				
				doSaveAnswers(qId);				
			}
		}	
    }

	private void doSaveAnswers(String id){
		Parcel answer = Parcel.obtain();
		answer.writeInt(mMultiBlankAnswer.size());
		for (Iterator<String> iter = mMultiBlankAnswer.keySet().iterator(); 
				iter.hasNext(); ){
			String subid = iter.next();
			answer.writeString(subid);
			answer.writeString(mMultiBlankAnswer.get(subid));			
		}
		answer.setDataPosition(0);
		mHostActivity.getAnswerManager().GetAnswer(id).AddAnswer(answer);
	}	    
    
    private void doLoadAnswers(String qId){
		Parcel refContent = mHostActivity.getAnswerManager().GetAnswer(qId).GetRefAnswer();
		Integer cntRef = refContent.readInt();

		mMultiBlankRefAnswer.clear();
		for (int i = 0; i < cntRef; ++i){
			String subid = refContent.readString();
			String answer = refContent.readString();
			mMultiBlankRefAnswer.put(subid, answer);
		}

		Parcel answerContent = mHostActivity.getAnswerManager().GetAnswer(qId).GetAnswer();
		Integer cntAnswer = answerContent.readInt();

		mMultiBlankAnswer.clear();
		for (int i = 0; i < cntAnswer; ++i){
			String subid = answerContent.readString();
			String answer = answerContent.readString();
			mMultiBlankAnswer.put(subid, answer);
		}
    }
}

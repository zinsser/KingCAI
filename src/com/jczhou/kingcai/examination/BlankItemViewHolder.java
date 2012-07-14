package com.jczhou.kingcai.examination;

import java.util.HashMap;
import java.util.Iterator;

import android.graphics.Color;
import android.os.Parcel;
import android.view.KeyEvent;
import android.view.View;
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
    	    	editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    	    	editText.setSingleLine(false);
    	    	editText.setMaxLines(3);
    	    	editText.setBackgroundResource(R.drawable.bak_input_bg);
    	    	mLinearLayoutBlanks.addView(editText);  	    	
    		}
    		((EditText)mLinearLayoutBlanks.getChildAt(i-1)).setImeOptions(EditorInfo.IME_ACTION_NEXT);
    	}else{
    		String tipQuestion = String.format(mHostActivity.getResources().getString(R.string.CurrentQuestion), id);    	
    		mEditTextor.setHint(tipQuestion);
    		mEditTextor.setTag(new TagParam(id, 0));
    		mEditTextor.setImeOptions(EditorInfo.IME_ACTION_DONE);
    	}
	}

	@Override
    public void doGettingItemViews(Integer id, int fontSize){
		super.doGettingItemViews(id, fontSize);
    	doLayoutSubViews();	
    	doLoadAnswers(id);
    	int okTextColor = Color.rgb(0, 128, 0);
    	int errTextColor = Color.rgb(220, 0, 0);
    	
    	if (mQuestionMgr.GetQuestionItem(id).GetType() == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
        	mEditTextor.setEnabled(false);
        	mEditTextor.setText(mMultiBlankAnswer.get(1));
    		mEditTextor.setTag(new TagParam(id, 1));
    		if (mMultiBlankAnswer != null && mMultiBlankRefAnswer != null
	    			&& mMultiBlankAnswer.get(1) != null && mMultiBlankRefAnswer.get(1) != null
	    			&& !mMultiBlankAnswer.get(1).equals(mMultiBlankRefAnswer.get(1))){
    			mEditTextor.setTextColor(errTextColor);
    		}
    		if (mLinearLayoutBlanks.getChildCount() < 2){
	    		TextView refText = new TextView(mHostActivity);
	    		refText.setText(mMultiBlankRefAnswer.get(1));
	    		refText.setPadding(10, 0, 0, 0);
	    		refText.setTextColor(okTextColor);
		    	mLinearLayoutBlanks.addView(refText);    		
    		}
	    	Parcel parcelValues  = mHostActivity.getAnswerManager().GetAnswer(id).GetRefAnswer();
    		Integer cnt = (Integer)parcelValues.readInt();
    		for (int i = 1; mLinearLayoutBlanks.getChildCount() < cnt * 2 
    				&& i < cnt; ++i){
    			EditText editText = new EditText(mHostActivity);
    			editText.setEnabled(false);
    	    	editText.setTag(new TagParam(id, i + 1));
    	    	editText.setSingleLine(false);
    	    	editText.setMaxLines(3);
    	    	editText.setText(mMultiBlankAnswer.get(i+1));
    	    	editText.setBackgroundResource(R.drawable.bak_input_bg);
    	    	mLinearLayoutBlanks.addView(editText);
    	    	if (mMultiBlankAnswer != null && mMultiBlankRefAnswer != null
    	    			&& mMultiBlankAnswer.get(i+1) != null && mMultiBlankRefAnswer.get(i+1) != null
    	    			&& !mMultiBlankAnswer.get(i+1).equals(mMultiBlankRefAnswer.get(i+1))){
    	    		editText.setTextColor(errTextColor);
    	    	}
    	    	TextView refText2 = new TextView(mHostActivity);
    	    	refText2.setText(mMultiBlankRefAnswer.get(i+1));
    	    	refText2.setTextColor(okTextColor);
    	    	refText2.setPadding(10, 0, 0, 0);
    	    	mLinearLayoutBlanks.addView(refText2);
    		}
    	}else{
        	mEditTextor.setEnabled(false);
        	mEditTextor.setText(mMultiBlankAnswer.get(0));
        	mEditTextor.setTag(new TagParam(id, 0));
    		if (mMultiBlankAnswer != null && mMultiBlankRefAnswer != null
	    			&& mMultiBlankAnswer.get(1) != null && mMultiBlankRefAnswer.get(1) != null
	    			&& !mMultiBlankAnswer.get(1).equals(mMultiBlankRefAnswer.get(1))){
    			mEditTextor.setTextColor(errTextColor);
    		}
    		if (mLinearLayoutBlanks.getChildCount() < 2){
	    		TextView refText = new TextView(mHostActivity);
	    		refText.setText(mMultiBlankRefAnswer.get(0));
	    		refText.setTextColor(okTextColor);
	    		refText.setPadding(10, 0, 0, 0);
		    	mLinearLayoutBlanks.addView(refText);
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
    
    private class BlankInputListener implements OnEditorActionListener{
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			TagParam param = (TagParam)v.getTag();
			Integer qId = param.mId;
			Integer subId = param.mSubId;
			
			if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED 
					|| actionId == EditorInfo.IME_ACTION_DONE){
				mMultiBlankAnswer.put(subId, new String(v.getText().toString()));				
				doSaveToAnswer(qId);
				return true;
			}else if (actionId == EditorInfo.IME_ACTION_NEXT){
				QuestionInfo info = mQuestionMgr.GetQuestionItem(qId);
				if (info != null && info.GetType() == QuestionInfo.QUESTION_TYPE_MULTIBLANK){
					mMultiBlankAnswer.put(subId, new String(v.getText().toString()));					
				}
				return true;
			}
			return false;
		}	
    }

	private void doSaveToAnswer(Integer id){
		Parcel answer = Parcel.obtain();
		answer.writeInt(mMultiBlankAnswer.size());
		for (Iterator<Integer> iter = mMultiBlankAnswer.keySet().iterator(); 
				iter.hasNext(); ){
			Integer subid = iter.next();
			answer.writeInt(subid);
			answer.writeString(mMultiBlankAnswer.get(subid));			
		}
		answer.setDataPosition(0);
		mHostActivity.getAnswerManager().GetAnswer(id).AddAnswer(answer);
	}	    
    
    private void doLoadAnswers(Integer qId){
		Parcel refContent = mHostActivity.getAnswerManager().GetAnswer(qId).GetRefAnswer();
		Integer cntRef = refContent.readInt();

		mMultiBlankRefAnswer.clear();
		for (int i = 0; i < cntRef; ++i){
			Integer subid = refContent.readInt();
			String answer = refContent.readString();
			mMultiBlankRefAnswer.put(subid, answer);
		}

		Parcel answerContent = mHostActivity.getAnswerManager().GetAnswer(qId).GetAnswer();
		Integer cntAnswer = answerContent.readInt();

		mMultiBlankAnswer.clear();
		for (int i = 0; i < cntAnswer; ++i){
			Integer subid = answerContent.readInt();
			String answer = answerContent.readString();
			mMultiBlankAnswer.put(subid, answer);
		}
    }
}

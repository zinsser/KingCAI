package com.king.cai.examination;

import java.util.ArrayList;
import java.util.Collections;

import android.graphics.Bitmap;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import com.king.cai.R;

public class PaperViewAdapter extends BaseAdapter {
	private PaperActivity mHostActivity = null;
    private LayoutInflater mInflater;
    private int mFontSize = 22;
    private ArrayList<Integer> mIndexes = new ArrayList<Integer>();
    private QuestionManager mQuestionMgr = null;
    
    private QuestionListListener mQuestionListener = new QuestionListListener();

    public class QuestionListListener implements QuestionManager.QuestionListener{
    	@SuppressWarnings("unchecked")
    	public void onQuestionArrayChanged(ArrayList<Integer> indexes) {
    		if (indexes != null){
    			mIndexes.clear();
    			mIndexes = (ArrayList<Integer>) indexes.clone();
    	        Collections.sort(mIndexes);
    	        
    	        notifyDataSetChanged();
    		}
    	}


    	public void onAddQuestion(Integer id) {
    		mIndexes.add(id);
            Collections.sort(mIndexes);
	        notifyDataSetChanged();
    	}


    	public void onClearQuestion() {
    		mIndexes.clear();
            Collections.sort(mIndexes);		
	        notifyDataSetChanged();            
    	}


		public void onImageReady(String qid, String imageIndex, Bitmap bmp) {
			notifyDataSetChanged();
		}
    }

    private PaperViewAdapter(){
    }
    
	public PaperViewAdapter(PaperActivity hostActivity, 
				QuestionManager questionMgr, AnswerManager answerMgr) {
		mHostActivity = hostActivity;
        mInflater = LayoutInflater.from(mHostActivity);
        mQuestionMgr = questionMgr;
		mQuestionMgr.addListener(mQuestionListener);
    }
    
    @SuppressWarnings("unchecked")
	public PaperViewAdapter CloneAdapter(ArrayList<Integer> indexes){
    	PaperViewAdapter retAdapter = new PaperViewAdapter();

        retAdapter.mHostActivity = this.mHostActivity;
    	retAdapter.mInflater = this.mInflater;
        retAdapter.mFontSize = this.mFontSize;
        retAdapter.mQuestionMgr = this.mQuestionMgr;
        retAdapter.mQuestionMgr.addListener(retAdapter.mQuestionListener);
        
        retAdapter.mIndexes = (ArrayList<Integer>) indexes.clone();
        Collections.sort(retAdapter.mIndexes);

        return retAdapter;
    }
    
    @Override
    public int getItemViewType(int position) {
        return mQuestionMgr.getQuestionItem(mIndexes.get(position)).getType();
    }

    @Override
    public int getViewTypeCount() {
        return QuestionInfo.QUESTION_TYPE_MAX;
    }
    
    
    public int getCount() {
        return mIndexes.size();
    }
    

    public Object getItem(int position) {
/*    	Integer pos = position;
    	for (int i = 0; i < mIndexes.size(); ++i){
    		if (mIndexes.get(i).equals(position)){
    			pos = i;
    			break;
    		}
    	}*/
        return mIndexes.get(position);
    }
    

    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.filters, null);
        	if (mQuestionMgr.getQuestionItem(mIndexes.get(position)).isOption()){
        		holder = new OptionItemViewHolder(mHostActivity, convertView, mQuestionMgr);
        	}else if (mQuestionMgr.getQuestionItem(mIndexes.get(position)).isBlank()){
        		holder = new BlankItemViewHolder(mHostActivity, convertView, mQuestionMgr);
        	}else if (mQuestionMgr.getQuestionItem(mIndexes.get(position)).isLogic()){
        		
        	}else{
        		holder = new ItemViewHolder(mHostActivity, convertView, mQuestionMgr);
        	}
           
            convertView.setTag(holder);
        } else { 
            holder = (ItemViewHolder) convertView.getTag();
        }
        
        mHostActivity.getPaperStatus().doGettingItemView(holder, mIndexes.get(position), mFontSize);

        return convertView;
    }
    
    public void SetNormalFontSize(){
    	mFontSize = 22;
    }

    public void SetSmallFontSize(){
    	mFontSize = 20;
    }
    
    public void SetLargeFontSize(){
    	mFontSize = 24;
    }
}

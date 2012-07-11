package com.jczhou.kingcai.examination;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import com.jczhou.kingcai.R;

public class PaperViewAdapter extends BaseAdapter {
	private Context mContext = null;
    private LayoutInflater mInflater;
    private int mFontSize = 22;
    private ArrayList<Integer> mIDs = new ArrayList<Integer>();
    private QuestionManager mQuestionMgr = null;
    private AnswerManager mAnswerMgr = null;
    
    public QuestionListListener mQuestionListener = new QuestionListListener();

    public class QuestionListListener implements QuestionManager.QuestionListener{
    	@SuppressWarnings("unchecked")
    	public void OnQuestionArrayChanged(ArrayList<Integer> ids) {
    		if (ids != null){
    			mIDs.clear();
    			mIDs = (ArrayList<Integer>) ids.clone();
    	        Collections.sort(mIDs);
    	        
    	        notifyDataSetChanged();
    		}
    	}


    	public void OnAddQuestion(Integer id) {
    		mIDs.add(id);
            Collections.sort(mIDs);		
	        notifyDataSetChanged();            
    	}


    	public void OnClearQuestion() {
    		mIDs.clear();
            Collections.sort(mIDs);		
	        notifyDataSetChanged();            
    	}


		public void OnImageReady(Bitmap bmp) {
			// TODO Auto-generated method stub
			
		}
    }

    private PaperViewAdapter(){
    }
    
	public PaperViewAdapter(Context context, QuestionManager questionMgr, AnswerManager answerMgr) {
		mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mQuestionMgr = questionMgr;
        mAnswerMgr = answerMgr;
    }
    
    @SuppressWarnings("unchecked")
	public PaperViewAdapter CloneAdapter(ArrayList<Integer> ids){
    	PaperViewAdapter retAdapter = new PaperViewAdapter();

        retAdapter.mContext = this.mContext;
    	retAdapter.mInflater = this.mInflater;
        retAdapter.mFontSize = this.mFontSize;
        retAdapter.mQuestionMgr = this.mQuestionMgr;
        retAdapter.mAnswerMgr = this.mAnswerMgr;
        
        retAdapter.mIDs = (ArrayList<Integer>) ids.clone();
        Collections.sort(retAdapter.mIDs);

        return retAdapter;
    }
    
    @Override
    public int getItemViewType(int position) {
        return mQuestionMgr.GetQuestionItem(mIDs.get(position)).GetType();
    }

    @Override
    public int getViewTypeCount() {
        return QuestionInfo.QUESTION_TYPE_MAX;
    }
    
    
    public int getCount() {
        return mIDs.size();
    }
    

    public Object getItem(int position) {
    	Integer pos = position;
    	for (int i = 0; i < mIDs.size(); ++i){
    		if (mIDs.get(i).equals(position)){
    			pos = i;
    			break;
    		}
    	}
        return pos;
    }
    

    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.filters, null);
        	if (mQuestionMgr.GetQuestionItem(mIDs.get(position)).IsPaperTitle()){
        		holder = new ItemViewHolder(mContext, convertView, mQuestionMgr, mAnswerMgr);       		
        	}else if (mQuestionMgr.GetQuestionItem(mIDs.get(position)).IsOption()){
        		holder = new OptionItemViewHolder(mContext, convertView, mQuestionMgr, mAnswerMgr);
        	}else if (mQuestionMgr.GetQuestionItem(mIDs.get(position)).IsBlank()){
        		holder = new BlankItemViewHolder(mContext, convertView, mQuestionMgr, mAnswerMgr);
        	}else if (mQuestionMgr.GetQuestionItem(mIDs.get(position)).IsLogic()){
        		
        	}
           
            convertView.setTag(holder);
        } else { 
            holder = (ItemViewHolder) convertView.getTag();
        }
        
        if (holder != null){
        	holder.doGettingItemView(mIDs.get(position), mFontSize);
        }
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

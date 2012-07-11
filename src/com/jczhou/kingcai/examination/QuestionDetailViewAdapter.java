package com.jczhou.kingcai.examination;

import java.util.ArrayList;
import java.util.Collections;

import com.jczhou.kingcai.R;
import com.jczhou.kingcai.examination.PaperActivity.OptionPanelListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

public class QuestionDetailViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Integer> mIDs = new ArrayList<Integer>();
    private int mFontSize = 22;
    private OptionPanelListener mOptionPanelListener;
    public QuestionListListener mQuestionListener = new QuestionListListener();
    
    public class ViewHolder {
        ImageView mark;
        TextView text;
        RadioButton radioBtnA;
        RadioButton radioBtnB;
        RadioButton radioBtnC;
        RadioButton radioBtnD;
        LinearLayout tableLayout;
 //       ArrayList<ImageView> mGraphices = new ArrayList<ImageView>();
        LinearLayout mParent = null;
        public ViewHolder(LinearLayout parent){
        	mParent = parent;
        }
    }

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
    
    public static abstract class AdapterListener{
    	public  abstract void OnAdapterLayoutView(ViewHolder hodlerView, Integer id);
    }

    public QuestionDetailViewAdapter(){
    }
    
	public QuestionDetailViewAdapter(Context context, OptionPanelListener listener) {
        mInflater = LayoutInflater.from(context);
        mOptionPanelListener = listener;
    }
    
    @SuppressWarnings("unchecked")
	public QuestionDetailViewAdapter CloneAdapter(ArrayList<Integer> ids){
    	QuestionDetailViewAdapter retAdapter = new QuestionDetailViewAdapter();
    	retAdapter.mInflater = this.mInflater;
        retAdapter.mOptionPanelListener = this.mOptionPanelListener;
        retAdapter.mFontSize = this.mFontSize;

        retAdapter.mIDs = (ArrayList<Integer>) ids.clone();
        Collections.sort(retAdapter.mIDs);

        return retAdapter;
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.filters, null);
            
            holder = new ViewHolder((LinearLayout)convertView);
            holder.mark = (ImageView) convertView.findViewById(R.id.imgMark);
            holder.text = (TextView) convertView.findViewById(R.id.txtQuestionDetail);
            holder.radioBtnA = (RadioButton)convertView.findViewById(R.id.radioBtnA);
            holder.radioBtnB = (RadioButton)convertView.findViewById(R.id.radioBtnB);
            holder.radioBtnC = (RadioButton)convertView.findViewById(R.id.radioBtnC);
            holder.radioBtnD = (RadioButton)convertView.findViewById(R.id.radioBtnD);
            holder.tableLayout = (LinearLayout)convertView.findViewById(R.id.linearLayoutAnswerAera);
            
            holder.mark.setOnClickListener(mOptionPanelListener);
            holder.radioBtnA.setOnClickListener(mOptionPanelListener);
            holder.radioBtnB.setOnClickListener(mOptionPanelListener);
            holder.radioBtnC.setOnClickListener(mOptionPanelListener);
            holder.radioBtnD.setOnClickListener(mOptionPanelListener);
            holder.text.setOnClickListener(mOptionPanelListener);
            
            convertView.setTag(holder);
        } else { 
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setTextSize(mFontSize);
        if (mIDs.size() > position){
        	mOptionPanelListener.OnAdapterLayoutView(holder, mIDs.get(position));
        }else{
            holder.text.setTextSize(mFontSize);
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

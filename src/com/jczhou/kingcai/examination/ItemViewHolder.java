package com.jczhou.kingcai.examination;

import com.jczhou.kingcai.R;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ItemViewHolder{
   	protected TextView mTextDetail;
    public ItemViewHolder(Context context, View rawView){
        mTextDetail = (TextView) rawView.findViewById(R.id.txtQuestionDetail);
        mTextDetail.setOnClickListener(mPanelListener);
    }
    
    public void doGettingItemView(){
    	
    }
    
    private View.OnClickListener mPanelListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
}

package com.jczhou.kingcai.examination;

import java.util.ArrayList;

import com.jczhou.kingcai.R;
import com.jczhou.kingcai.examination.PaperActivity.OptionPanelListener;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class QuestionItemView {
    private ImageView mMark;
    private TextView mTextDetail;
    private RadioButton mRadioButtonA;
    private RadioButton mRadioButtonB;
    private RadioButton mRadioButtonC;
    private RadioButton mRadioButtonD;
    private LinearLayout mTableLayoutOption;
    private ImageView mImageView_1;
    private ImageView mImageView_2;
    private ImageView mImageView_3;
    private ImageView mImageView_4;    
    private ArrayList<ImageView> mImageViews = new ArrayList<ImageView>(); 
    
    public QuestionItemView(View parentView, OptionPanelListener mOptionPanelListener){
    	mMark = (ImageView) parentView.findViewById(R.id.imgMark);
    	mTextDetail = (TextView) parentView.findViewById(R.id.txtQuestionDetail);
    	mRadioButtonA = (RadioButton)parentView.findViewById(R.id.radioBtnA);
    	mRadioButtonB = (RadioButton)parentView.findViewById(R.id.radioBtnB);
    	mRadioButtonC = (RadioButton)parentView.findViewById(R.id.radioBtnC);
    	mRadioButtonD = (RadioButton)parentView.findViewById(R.id.radioBtnD);
    	mTableLayoutOption = (LinearLayout)parentView.findViewById(R.id.linearLayoutOption);
        
        mMark.setOnClickListener(mOptionPanelListener);
        mRadioButtonA.setOnClickListener(mOptionPanelListener);
        mRadioButtonB.setOnClickListener(mOptionPanelListener);
        mRadioButtonC.setOnClickListener(mOptionPanelListener);
        mRadioButtonD.setOnClickListener(mOptionPanelListener);
        mTextDetail.setOnClickListener(mOptionPanelListener);    	
    }
}

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
    private ImageView mark;
    private TextView text;
    private RadioButton radioBtnA;
    private RadioButton radioBtnB;
    private RadioButton radioBtnC;
    private RadioButton radioBtnD;
    private LinearLayout tableLayout;
    private ArrayList<ImageView> mImageViews = new ArrayList<ImageView>(); 
    
    public QuestionItemView(View parentView, OptionPanelListener mOptionPanelListener){
        mark = (ImageView) parentView.findViewById(R.id.imgMark);
        text = (TextView) parentView.findViewById(R.id.txtQuestionDetail);
        radioBtnA = (RadioButton)parentView.findViewById(R.id.radioBtnA);
        radioBtnB = (RadioButton)parentView.findViewById(R.id.radioBtnB);
        radioBtnC = (RadioButton)parentView.findViewById(R.id.radioBtnC);
        radioBtnD = (RadioButton)parentView.findViewById(R.id.radioBtnD);
        tableLayout = (LinearLayout)parentView.findViewById(R.id.linearLayoutOption);
        
        mark.setOnClickListener(mOptionPanelListener);
        radioBtnA.setOnClickListener(mOptionPanelListener);
        radioBtnB.setOnClickListener(mOptionPanelListener);
        radioBtnC.setOnClickListener(mOptionPanelListener);
        radioBtnD.setOnClickListener(mOptionPanelListener);
        text.setOnClickListener(mOptionPanelListener);    	
    }
}

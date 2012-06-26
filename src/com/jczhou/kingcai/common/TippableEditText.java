package com.jczhou.kingcai.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TippableEditText extends LinearLayout{
	private TextView mTipText = null;
	private EditText mInputText = null;
	
	public TippableEditText(Context context) {
		super(context);
	}
	
	public TippableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private void CreateInnerControl(){
		
	}
}

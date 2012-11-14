package com.king.cai.examination;

import com.king.cai.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class KingDrawableActivity extends Activity{
	private KingDrawView mDrawView = null; 
	private ToggleButton mToggleButtonErase = null;
	private ToggleButton mToggleButtonWrite = null;
	private ToggleButton mToggleButtonLine = null;
	private ToggleButton mToggleButtonCircle = null;
	private ToggleButton mToggleButtonRect = null;
	private Button mButtonFinish = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawpannel);  
        
        mDrawView = (KingDrawView)findViewById(R.id.kingDrawViewPaint);
        mToggleButtonErase = (ToggleButton)findViewById(R.id.toggleButtonErase);
        mToggleButtonErase.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mToggleButtonErase.setBackgroundResource(isChecked ? R.drawable.ic_menu_eraser_active
														: R.drawable.ic_menu_eraser);
				mDrawView.switchStatusByMode(isChecked);
			}
		});
        
        mToggleButtonWrite = (ToggleButton)findViewById(R.id.toggleButtonWrite);        
        mToggleButtonWrite.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			//	mDrawView.switchStatusByMode(isChecked);
			}
		});
        mToggleButtonWrite = (ToggleButton)findViewById(R.id.toggleButtonWrite);        
        mToggleButtonWrite.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//mDrawView.switchStatusByMode(isChecked);
			}
		});
        mToggleButtonLine = (ToggleButton)findViewById(R.id.toggleButtonLine);        
        mToggleButtonLine.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			//	mDrawView.switchStatusByMode(isChecked);
			}
		});        
        mToggleButtonCircle = (ToggleButton)findViewById(R.id.toggleButtonCircle);        
        mToggleButtonCircle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		//		mDrawView.switchStatusByMode(isChecked);
			}
		});
        mToggleButtonRect = (ToggleButton)findViewById(R.id.toggleButtonRect);        
        mToggleButtonRect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	//			mDrawView.switchStatusByMode(isChecked);
			}
		});
        
        mButtonFinish = (Button)findViewById(R.id.buttonFinish);
        mButtonFinish.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Bitmap bmp = Bitmap.createBitmap(mDrawView.getWidth(),
						mDrawView.getWidth(), Bitmap.Config.ARGB_8888);
				mDrawView.drawToBitmap(bmp);
			}
		});
	}
}

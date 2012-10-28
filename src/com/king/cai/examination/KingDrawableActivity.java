package com.king.cai.examination;

import com.king.cai.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class KingDrawableActivity extends Activity{
	private KingDrawView mDrawView = null; 
	private ToggleButton mToggleButtonErase = null;
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
	}
}

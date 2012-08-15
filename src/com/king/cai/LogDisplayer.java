package com.king.cai;

import com.king.cai.common.ComunicableActivity;
import com.king.cai.service.KingService;

import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;

public class LogDisplayer extends ComunicableActivity {
	private TextView mLogger = null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logger);
        mLogger = (TextView)findViewById(R.id.textViewLogger);
        if (mServiceChannel != null && mServiceChannel.mKingService != null){
            for (String log : KingService.getLogService().getLog()){
                mLogger.setText(log);
            }
        }
	}
	
	@Override
	protected void doHandleInnerMessage(Message innerMessage) {
	}

	@Override
	protected void doServiceReady() {		
	}
}

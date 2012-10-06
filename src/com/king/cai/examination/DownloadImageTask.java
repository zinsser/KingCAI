package com.king.cai.examination;

import com.king.cai.KingCAIConfig;
import com.king.cai.common.DownloadTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class DownloadImageTask extends DownloadTask{
	public static final String TaskType = "ImageTask";
	private String mQuestionID;
	private String mImageIndex;
	
	public DownloadImageTask(String qid, String imageIndex, Handler innerHandler){
		super(innerHandler, TaskType);
		mQuestionID = qid;
		mImageIndex = imageIndex;		
	}
	
	public void request(){
		super.request();
		
		Message innerMessage = mInnerHandler.obtainMessage(KingCAIConfig.EVENT_REQUEST_IMAGE);
		Bundle bundle = new Bundle();
		bundle.putString("ID", mQuestionID);
		bundle.putString("Index", mImageIndex);
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();
	}
	
	public String getQuestionID(){
		return mQuestionID;
	}
	
	public String getImageIndex(){
		return mImageIndex;
	}

	public boolean equals(DownloadImageTask other){
		boolean  bEqual = mQuestionID != null && mQuestionID.equals(other.mQuestionID);
		if (mImageIndex != null && other.mImageIndex != null){
			bEqual = bEqual && mImageIndex.equals(other.mImageIndex);
		}else{
			bEqual = bEqual && (mImageIndex == null && other.mImageIndex == null);
		}
		return bEqual;
	}
}

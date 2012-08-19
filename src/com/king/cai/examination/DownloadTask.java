package com.king.cai.examination;

import com.king.cai.KingCAIConfig;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class DownloadTask {
	private enum TaskStatus{
		TS_Unstart,
		TS_Process,
		TS_Finished
	};
	
	private String mQuestionID;
	private String mImageIndex;
	private TaskStatus mStatus;
	private Handler mInnerHandler;
	
	public DownloadTask(String qid, String imageIndex, Handler innerHandler){
		mInnerHandler = innerHandler;
		mQuestionID = qid;
		mImageIndex = imageIndex;
		mStatus = TaskStatus.TS_Unstart;		
	}
	
	public void request(){
		mStatus = TaskStatus.TS_Process;
		
		Message innerMessage = mInnerHandler.obtainMessage(KingCAIConfig.EVENT_REQUEST_IMAGE);
		Bundle bundle = new Bundle();
		bundle.putString("ID", mQuestionID);
		bundle.putString("Index", mImageIndex);
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();
	}
	
	public void finish(){
		mStatus = TaskStatus.TS_Finished;
	}
	
	public boolean isUnstart(){
		return mStatus == TaskStatus.TS_Unstart;
	}
	
	public boolean isProcess(){
		return mStatus == TaskStatus.TS_Process;
	}
	
	public boolean isFinished(){
		return mStatus == TaskStatus.TS_Finished;
	}
	
	public String getQuestionID(){
		return mQuestionID;
	}
	
	public String getImageIndex(){
		return mImageIndex;
	}

	public boolean equals(DownloadTask other){
		boolean  bEqual = mQuestionID != null && mQuestionID.equals(other.mQuestionID);
		if (mImageIndex != null && other.mImageIndex != null){
			bEqual = bEqual && mImageIndex.equals(other.mImageIndex);
		}else{
			bEqual = bEqual && (mImageIndex == null && other.mImageIndex == null);
		}
		return bEqual;
	}
}

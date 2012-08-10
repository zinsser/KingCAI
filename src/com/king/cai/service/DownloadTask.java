package com.king.cai.service;

import java.nio.ByteBuffer;

import android.os.Message;

public class DownloadTask {
	private enum TaskStatus{
		TS_Unstart,
		TS_Process,
		TS_Finished
	};
	
	private String mQuestionID;
	private String mImageIndex;
	private ByteBuffer mDataBuf = null;
	private TaskStatus mStatus;
	private Message mInnerMessage = null;
	
	public DownloadTask(String qid, Message innerMessage){
		mQuestionID = qid;
		mStatus = TaskStatus.TS_Unstart;
		mInnerMessage = innerMessage;
	}
	
	public void request(){
		mStatus = TaskStatus.TS_Process;
		mInnerMessage.sendToTarget();
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
	
	public void releaseBuffer(){
		mDataBuf = null;
	}
	
	public String getQuestionID(){
		return mQuestionID;
	}
	
	public void updateImageInfo(String imageIndex, int size){
		mImageIndex = imageIndex;
		mDataBuf = null;
		mDataBuf = ByteBuffer.allocate(size);
	}
	
	public void addData(ByteBuffer buf, Integer size){
		if (mDataBuf != null && mDataBuf.hasRemaining()){
			mDataBuf.put(buf.array(), 0, size);
		}
		
		if (!mDataBuf.hasRemaining()){
			mStatus = TaskStatus.TS_Finished;
		}
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
	
	public int getRemain(){
		return mDataBuf != null ? mDataBuf.remaining() : 0;
	}
	
	public ByteBuffer getDataBuffer(){
		return mDataBuf;
	}
}

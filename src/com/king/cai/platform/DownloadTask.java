package com.king.cai.platform;

import java.nio.ByteBuffer;

public class DownloadTask {
	private String mQuestionID;
	private String mSubImageID;
	private int mDataLen = 0;
	private ByteBuffer mDataBuf = null;

	public DownloadTask(String qid, String subid){
		mQuestionID = qid;
		mSubImageID = subid;
	}
	
	public void request(){
		
	}
	
	public void updateDataLength(int len){
		mDataLen = len;
		mDataBuf = null;
		mDataBuf = ByteBuffer.allocate(mDataLen);
	}
	
	public void addData(byte[] data, Integer size){
		if (mDataBuf != null){
			mDataBuf.put(data, 0, size);
		}
	}
	
	public boolean equals(DownloadTask other){
		return mQuestionID != null && mQuestionID.equals(other.mQuestionID) 
				&& mSubImageID != null && mSubImageID.equals(other.mSubImageID);
	}
	
	public int getRemain(){
		return mDataBuf != null ? mDataBuf.remaining() : mDataLen;
	}
}

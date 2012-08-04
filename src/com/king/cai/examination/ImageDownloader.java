package com.king.cai.examination;

import java.util.ArrayList;

import android.os.Message;

public class ImageDownloader {
	private ArrayList<DownloaderTask> mTasks = new ArrayList<DownloaderTask>();
	private DownloaderTask mDoingTask = null;
	
	public static class DownloaderTask{
		private String mQuestionID;
		private String mSubImageID;
		private Message mResult;
		private int mDataLen;
		
		public DownloaderTask(String qid, String subid, Message result){
			mQuestionID = qid;
			mSubImageID = subid;
			mResult = result;
		}
		
		public void request(){
			
		}
		
		public void updateDataLength(int len){
			mDataLen = len;
		}
		
		public boolean equals(DownloaderTask other){
			return mQuestionID != null && mQuestionID.equals(other.mQuestionID) 
					&& mSubImageID != null && mSubImageID.equals(other.mSubImageID);
		}
	}
	
	public void addTask(String qid, String subid, Message result){
		addTask(new DownloaderTask(qid, subid, result));
	}
	
	public void addTask(DownloaderTask newTask){
		if (newTask != null){
			if (mDoingTask == null){
				mDoingTask = newTask;
			} else if (!mDoingTask.equals(newTask)){
				boolean bExistSameTask = false;
				for (DownloaderTask task : mTasks){
					if (task.equals(newTask)){
						bExistSameTask = true;
						break;
					}
				}
				
				if (!bExistSameTask){
					mTasks.add(newTask);
				}
			}
		}
	}
	
	public void startDownload(){
		do{
			if (mDoingTask == null){
				mDoingTask = mTasks.get(0);
				mTasks.remove(0);
			}
			
			mDoingTask.request();
		}while (mTasks.size() > 0);
	}
}

package com.king.cai.service;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.os.Message;

public class DownloadCache {
	private ArrayList<DownloadTask> mTasks = new ArrayList<DownloadTask>();
	private DownloadTask mDoingTask = null;
	
	public void dispatchTask(){
		
	}
	
	public void addTask(String qid, String subid){
		addTask(new DownloadTask(qid, subid));
	}
	
	public int getRemain(){
		return mDoingTask != null ? mDoingTask.getRemain() : 0;
	}
	
	public ByteBuffer getDataBuffer(){
		return mDoingTask != null ? mDoingTask.getDataBuffer() : null;
	}
	
	public void addTask(DownloadTask newTask){
		if (newTask != null){
			if (mDoingTask == null){
				mDoingTask = newTask;
			} else if (!mDoingTask.equals(newTask)){
				if (!findTask(newTask)){
					mTasks.add(newTask);
				}
			}
		}
	}
	
	private boolean findTask(DownloadTask targetTask){
		boolean bExistSameTask = false;
		for (DownloadTask task : mTasks){
			if (task.equals(targetTask)){
				bExistSameTask = true;
				break;
			}
		}
		return bExistSameTask;
	}
	
	private boolean findTask(String qid, String subid){
		return findTask(new DownloadTask(qid, subid));
	}
	
	public void startDownload(){
		do{
			if (mDoingTask == null && mTasks.size() > 1){
				mDoingTask = mTasks.get(0);
				mTasks.remove(0);
			}
			
			mDoingTask.request();
		}while (mTasks.size() > 0);
	}

	public void exitDownload(){
		mDoingTask = null;
		for (DownloadTask task : mTasks){
			task.releaseBuffer();
			task = null;
		}
		mTasks = null;
	}
	
	public void receiveData(byte[] data, Integer size){
		if (mDoingTask != null){
			mDoingTask.addData(data, size);
			if (mDoingTask.getRemain() == 0){
				
			}
		}
	}
	
	public void updateDownloadSize(String qid, String subid, int size){
		if (findTask(qid, subid)){
//			DownloadTask task = mTasks.
		}
	}
	
}

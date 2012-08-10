package com.king.cai.service;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import android.os.Message;

public class DownloadCache {
	private Queue<DownloadTask> mTasks = new LinkedList<DownloadTask>();
	private DownloadTask mDoingTask = null;
	
	public void dispatchTask(){
		if (mDoingTask == null){
			if (mTasks.size() > 0){
				mDoingTask = mTasks.poll();
			}
		}
		
		if (mDoingTask != null && mDoingTask.isUnstart()){
			mDoingTask.request();
		}else if (mDoingTask != null && mDoingTask.isFinished()){
			mDoingTask = null;
			if (mTasks.size() > 0){
				mDoingTask = mTasks.poll();
			}
		}
	}

	public int getRemain(){
		return mDoingTask != null ? mDoingTask.getRemain() : 0;
	}
	
	public ByteBuffer getDataBuffer(){
		return mDoingTask != null ? mDoingTask.getDataBuffer() : null;
	}
	
	public void addTask(String qid, Message innerMessage){
		addTask(new DownloadTask(qid, innerMessage));
	}	
	
	public void addTask(DownloadTask newTask){
		if (newTask != null && !mTasks.contains(newTask)){
			mTasks.offer(newTask);
			dispatchTask();
		}
	}

	public void exitDownload(){
		mDoingTask = null;
		for (DownloadTask task : mTasks){
			task.releaseBuffer();
			task = null;
		}
		mTasks = null;
	}
	
	public void receiveData(ByteBuffer buf, Integer size){
		if (mDoingTask != null){
			mDoingTask.addData(buf, size);
		}
	}
	
	public void updateDownloadSize(String qid, String imageIndex, int size){
		if (mDoingTask != null && mDoingTask.getQuestionID().equals(qid)){
			mDoingTask.updateImageInfo(imageIndex, size);
		}
	}
}

package com.king.cai.examination;

import java.util.LinkedList;
import java.util.Queue;

import android.os.Handler;

public class DownloadManager {
	private Queue<DownloadTask> mTasks = new LinkedList<DownloadTask>();
	private DownloadTask mDoingTask = null;
	private DownloadManager(){
		
	}
	
	private static DownloadManager s_dlManager = null;
	public static DownloadManager getInstance(){
		if (s_dlManager == null){
			s_dlManager = new DownloadManager();
		}
		
		return s_dlManager;
	}
	
	public void dispatchTask(){
		if (mDoingTask == null 
				|| (mDoingTask != null && mDoingTask.isFinished())){
			mDoingTask = null;
			if (mTasks.size() > 0){
				mDoingTask = mTasks.poll();
			}
		}
		
		if (mDoingTask != null && mDoingTask.isUnstart()){
			mDoingTask.request();
		}
	}

	public DownloadTask getCurrentTask(){
		return mDoingTask;
	}
	
	public void addTask(String qid, String imageIndex, Handler innerHandler){
		addTask(new DownloadTask(qid, imageIndex, innerHandler));
	}	
	
	public void addTask(DownloadTask newTask){
		if (newTask != null && !mTasks.contains(newTask)){
			mTasks.offer(newTask);
		}
	}

	public void exitDownload(){
		mDoingTask = null;
		for (DownloadTask task : mTasks){
			task = null;
		}
		mTasks = null;
	}

	public void finishCurrentTask(){
		mDoingTask.finish();
		dispatchTask();
	}
}

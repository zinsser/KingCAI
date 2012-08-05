package com.king.cai.platform.internal;

import java.util.ArrayList;

import android.os.Message;

public class DownloadService {
	private ArrayList<DownloadTask> mTasks = new ArrayList<DownloadTask>();
	private DownloadTask mDoingTask = null;
	
	private void dispatchTask(){
		
	}
	
	public void addTask(String qid, String subid){
		addTask(new DownloadTask(qid, subid));
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
	
	public void receiveData(byte[] data, Integer size){
		if (mDoingTask != null){
			mDoingTask.addData(data, size);
			if (mDoingTask.getRemain() == 0){
				
			}
		}
	}
	
	public void updateDownloadInfo(String qid, String subid, int len){
		if (findTask(qid, subid)){
//			DownloadTask task = mTasks.
		}
	}
	
}

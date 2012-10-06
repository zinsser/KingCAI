package com.king.cai.common;

import android.os.Handler;

public class DownloadTask {
	private enum TaskStatus{
		TS_Unstart,
		TS_Process,
		TS_Finished
	};
	private TaskStatus mStatus;	
	protected Handler mInnerHandler;
	public String mTaskType;
	protected DownloadTask(Handler handler, String taskType){
		mStatus = TaskStatus.TS_Unstart;
		mInnerHandler = handler;
		mTaskType = taskType;
	}
	
	public void request(){
		mStatus = TaskStatus.TS_Process;
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
}

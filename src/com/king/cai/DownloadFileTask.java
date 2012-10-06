package com.king.cai;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.king.cai.common.DownloadTask;

public class DownloadFileTask extends DownloadTask{
	public static final String TaskType = "FileTask";
	private String mFileName;
	private String mFileSize;
	private String mId;
	public DownloadFileTask(Handler handler, String fileName, String size, String id) {
		super(handler, TaskType);
		mFileName = fileName;
		mFileSize = size;
		mId = id;
	}

	public void request(){
		super.request();
		
		Message innerMessage = mInnerHandler.obtainMessage(KingCAIConfig.EVENT_REQUEST_FILE);
		Bundle bundle = new Bundle();
		bundle.putString("Name", mFileName);
		bundle.putString("Size", mFileSize);
		bundle.putString("Id", mId);
		innerMessage.setData(bundle);
		innerMessage.sendToTarget();
	}
	
	public String getFileName(){
		return mFileName;
	}
	
	public String getFileId(){
		return mId;
	}
}

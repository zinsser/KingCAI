package com.king.cai;

import java.io.File;

import android.app.Dialog;
import android.content.Intent;
import android.os.Environment;
import android.os.Message;
import android.view.View;

public abstract class WorkspaceStatus {
	protected WorkspaceActivity mStatusOwner = null;
	protected WorkspaceStatus(WorkspaceActivity statusOwner){
		mStatusOwner = statusOwner;
	}
	
	protected View findViewById(int id){
		View retView  = null;
		if (mStatusOwner != null){
			retView = mStatusOwner.findViewById(id);
		}
		
		return retView;
	}
	
	protected void startActivity(Intent intent){
		if (mStatusOwner != null){
			mStatusOwner.startActivity(intent);
		}
	}

	
	private void startExplorerActivity(){
		
		String rootDir = Environment.getExternalStorageDirectory().getPath()+"/KingCAI"; //"/";// 
		File destDir = new File(rootDir);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		Intent openExplorerIntent = new Intent(mStatusOwner, ExplorerActivity.class);
		openExplorerIntent.putExtra("RootDir", rootDir);
		startActivity(openExplorerIntent);		
	}
	
	
	public abstract void enterStatus();
	public abstract void leaveStatus();
	public abstract void doHandleInnerMessage(Message innerMessage);
	
	public abstract void onBookmarkClick();
	public void onStudyClick(){
		startExplorerActivity();
	}
	public abstract void onPaperClick();
	public abstract void onWrongQuestionsClick();
	public abstract void onAppmenuClick();
	public abstract void onSettingsClick();
}

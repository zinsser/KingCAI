package com.king.cai;

import java.io.File;

import com.king.cai.message.RequestMessage_ExplorerDirectory;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
	
	public void onStudyClick(){		
	}
	
	public abstract void doHandleInnerMessage(Message innerMessage);	
	public abstract void enterStatus();
	public abstract void leaveStatus();	
	public abstract void onBookmarkClick();
	public abstract void onPaperClick();
	public abstract void onWrongQuestionsClick();
	public abstract void onAppmenuClick();
	public abstract void onSettingsClick();
}

package com.king.cai.service;

import java.util.ArrayList;

public class LoggerManager {
	private static LoggerManager s_loggerManager = null;
    private ArrayList<String> mLogger = new ArrayList<String>();
    
	private LoggerManager(){
		
	}
	public static LoggerManager getInstance(){
		if (s_loggerManager == null){
			s_loggerManager = new LoggerManager();
		}
		return s_loggerManager;
	}
	
	public void addLog(String log){
		mLogger.add(log);
	}
	
	public ArrayList<String> getLog(){
		return mLogger;
	}
}

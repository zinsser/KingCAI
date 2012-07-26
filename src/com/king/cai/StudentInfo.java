package com.king.cai;

public class StudentInfo {
	private static final String mDatabaseName = "student.db";
	private static final String mTableName = "studentinfo";
	private final String[] mTags={
		"id", "name", "password", "info", "photo"
	};

	
	public int GetItemCount(){
		return mTags.length;
	}
	
	public String GetTag(int pos){
		return mTags[pos];
	}

	public static String GetDBName(){
		return mDatabaseName;
	}
	
	public String GetTableName(){
		return mTableName;
	}
	
	public String GetTableCreatorString(){
		return " create table  "  
	            + mTableName + " (" 
				+ mTags[0] + " integer primary key,  " 
				+ mTags[1] + " text, "
				+ mTags[2] + " text, "
				+ mTags[3] + " text, "
				+ mTags[4] + " text) ";
	}
}

package com.king.cai;

import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  

public class StudentDBHelper extends SQLiteOpenHelper { 
	public final static String s_StudentTag_ID = "id";
	public final static String s_StudentTag_Name = "name";
	public final static String s_StudentTag_Passwd = "password";	
	public final static String s_StudentTag_Info = "info";
	public final static String s_StudentTag_Photo = "photo";
	
    private static final String DB_PAPER = "Student.db";  
    private static final String TBL_PAPER = "StudyInfo";  
    private static final String CREATE_TBL = " create table "  
            + " StudyInfo(id integer primary key, name text, password text, infocontent text, photo text) ";  
      
    private SQLiteDatabase mDB;
    private StudentInfo mStudentInfo = new StudentInfo();
    StudentDBHelper(Context c) {  
        super(c, StudentInfo.GetDBName(), null, 1);
    }  
    
    @Override  
    public void onCreate(SQLiteDatabase db) {  
    	mDB = db;  
    	mDB.execSQL(CREATE_TBL);  
    } 

    @Override
    public void close() {
        if (mDB != null){ 
        	mDB.close(); 
        }
    	super.close();        
    }
    
    public ContentValues FindItem(String id){
		Cursor cursor=	mDB.rawQuery("select * from StudyInfo where id=?", new String[]{id});
		if(cursor.moveToNext()){
			ContentValues values = new ContentValues();
			values.put(s_StudentTag_ID, cursor.getInt(0));
			values.put(s_StudentTag_Name, cursor.getString(1));
			values.put(s_StudentTag_Passwd, cursor.getString(2));
			values.put(s_StudentTag_Info, cursor.getString(3));
			values.put(s_StudentTag_Photo, cursor.getString(4));
			return values;
		}

		return null;    	
    }
	public void Update(ContentValues values, String id) {
//		mDB.execSQL("update StudyInfo set id=? where id=?", 
//				new Object[]{, id});
	}
	
    public long Insert(ContentValues values, String id) {  
        SQLiteDatabase db = getWritableDatabase();
        long ret = 0;
        try{
        	ret = db.insertOrThrow(TBL_PAPER, null, values);
        }catch (SQLException e){
        	ret = db.update(TBL_PAPER, values, "id=?", new String[]{id});        	
        }
        db.close(); 
        return ret;
    }  
    
    public Cursor Query() {  
        SQLiteDatabase db = getWritableDatabase();  
        Cursor c = db.query(TBL_PAPER, null, null, null, null, null, null);  
        return c;  
    }
    
    public void Delete(int id) {  
        if (mDB == null)  
        	mDB = getWritableDatabase();  
        mDB.delete(TBL_PAPER, "id=?", new String[] { String.valueOf(id) });  
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	} 
}  
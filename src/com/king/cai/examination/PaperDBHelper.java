package com.king.cai.examination;

import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  

public class PaperDBHelper extends SQLiteOpenHelper { 

    private static final String DB_PAPER = "Paper.db";  
    private static final String TBL_PAPER = "Paper";  
    private static final String CREATE_TBL = " create table "  
            + " Paper(id text primary key, question text, type integer, reference text, imagecount integer, image text) ";  
      
    private SQLiteDatabase mDB;
    
    PaperDBHelper(Context c) {  
        super(c, DB_PAPER, null, 2);  
    }  
    
    @Override  
    public void onCreate(SQLiteDatabase db) {  
    	mDB = db;  
    	mDB.execSQL(CREATE_TBL);  
    } 
    
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
    }
    
    @Override
    public void close() {
    	super.close();
        if (mDB != null){ 
        	mDB.close(); 
        }
    }  
    
    public long Insert(ContentValues values, String id) {  
        SQLiteDatabase db = getWritableDatabase();
        long ret = 0;
        try{
        	ret = db.insertOrThrow(TBL_PAPER, null, values);
        }catch (SQLException e){
        	ret = db.update(TBL_PAPER, values, "id=?", new String[]{String.valueOf(id)});        	
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
}  
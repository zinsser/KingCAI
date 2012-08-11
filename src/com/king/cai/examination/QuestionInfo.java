package com.king.cai.examination;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;

public class QuestionInfo {
	public static final int QUESTION_TYPE_TITLE = 0;
	public static final int QUESTION_TYPE_OPTION = 1;
	public static final int QUESTION_TYPE_MULTIOPTION = 2;	
	public static final int QUESTION_TYPE_BLANK = 3;
	public static final int QUESTION_TYPE_MULTIBLANK = 4;	
	public static final int QUESTION_TYPE_LOGIC = 5;
	public static final int QUESTION_TYPE_MAX = QUESTION_TYPE_LOGIC + 1;
	
	public String mID;
	public int mType;
	public String mDetail;
	public String mReference;
	public int mImageCount;
	public HashMap<String, Bitmap> mGraphics = new HashMap<String, Bitmap>();
	public ArrayList<String> mImageIndexes = new ArrayList<String>();
	
	public QuestionInfo(String id, int type, String reference, String detail, int imageCount){
		mID = id;
		mDetail = detail;
		mReference = reference;
		mImageCount = imageCount;
		mType = AnalysisDetailTypeByReference(type, reference);
	}
	
	public void AddGraphic(String imageIndex, Bitmap graphic){
		mImageIndexes.add(imageIndex);
		mGraphics.put(imageIndex, graphic);
	}
	
	public String getQuestionID(){
		return mID;
	}
	
	public Bitmap getImage(String imageIndex){
		if (mGraphics.size() < 1) return null;
		return mGraphics.get(imageIndex);
	}
	
	public boolean hasImage(){
		return mImageCount > 0;
	}
	
	public String getImageIndex(int pos){
		return mImageIndexes.get(pos);
	}
	
	private int AnalysisDetailTypeByReference(int type, String reference){
		//根据参考答案来分析更精确的问题类型：选择题（单、多）、填空题（单、多）、逻辑判断题
		int retType = type;
		if (reference != null && reference.length() > 1 
				&& type != QUESTION_TYPE_TITLE){
			String[] refs = mReference.substring(1).split("#");
			if (refs.length > 1){
				if (type == QUESTION_TYPE_OPTION){
					retType = QUESTION_TYPE_MULTIOPTION;					
				}else if (type == QUESTION_TYPE_BLANK){
					retType = QUESTION_TYPE_MULTIBLANK;
				}
			}
		}

		return retType;
	}
	
	public int GetType(){
		return mType;
	}
	
	public boolean IsBlank(){
		return (mType == QUESTION_TYPE_BLANK) || (mType == QUESTION_TYPE_MULTIBLANK); 
	}
	
	public boolean IsLogic(){
		return mType == QUESTION_TYPE_LOGIC;
	}
	
	public boolean IsOption(){
		return (mType == QUESTION_TYPE_OPTION) || (mType == QUESTION_TYPE_MULTIOPTION);		
	}
	
	public boolean IsPaperTitle(){
		return mType == QUESTION_TYPE_TITLE;
	}
}

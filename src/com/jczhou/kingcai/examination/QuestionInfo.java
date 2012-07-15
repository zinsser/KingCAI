package com.jczhou.kingcai.examination;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class QuestionInfo {
	public static final int QUESTION_TYPE_TITLE = 0;
	public static final int QUESTION_TYPE_OPTION = 1;
	public static final int QUESTION_TYPE_MULTIOPTION = 2;	
	public static final int QUESTION_TYPE_BLANK = 3;
	public static final int QUESTION_TYPE_MULTIBLANK = 4;	
	public static final int QUESTION_TYPE_LOGIC = 5;
	public static final int QUESTION_TYPE_MAX = QUESTION_TYPE_LOGIC + 1;
	
	public int mType;
	public String mDetail;
	public String mReference;
	public boolean mHasImage;
	public ArrayList<Bitmap> mGraphics = new ArrayList<Bitmap>();
	
	public QuestionInfo(int type, String reference, String detail, boolean bHasImage){
		mDetail = detail;
		mReference = reference;
		mHasImage = bHasImage;
		mType = AnalysisDetailTypeByReference(type, reference);
	}
	
	public void AddGraphic(Bitmap graphic){
		mGraphics.add(graphic);
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
	
	public Bitmap GetImage(){
		if (mGraphics.size() < 1) return null;
		return mGraphics.get(0);
	}
	
	public boolean HasImage(){
		return mGraphics.size() > 0;
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

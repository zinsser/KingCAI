package com.jczhou.kingcai.examination;

public class QuestionInfo {
	public static final int QUESTION_TYPE_TITLE = 0;
	public static final int QUESTION_TYPE_OPTION = 1;
	public static final int QUESTION_TYPE_MULTIOPTION = 2;	
	public static final int QUESTION_TYPE_BLANK = 3;
	public static final int QUESTION_TYPE_MULTIBLANK = 4;	
	public static final int QUESTION_TYPE_LOGIC = 5;	
	
	public int mType;
	public String mDetail;
	public String mReference;
	
	public QuestionInfo(int type, String reference, String detail){
		mDetail = detail;
		mReference = reference;
		
		mType = AnalysisDetailTypeByReference(type, reference);
	}
	
	private int AnalysisDetailTypeByReference(int type, String reference){
		//���ݲο�������������ȷ���������ͣ�ѡ���⣨�����ࣩ������⣨�����ࣩ���߼��ж���
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

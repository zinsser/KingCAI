package com.king.cai.examination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

public class QuestionManager {
	private final static String s_PaperTag_ID = "id";
	private final static String s_PaperTag_Question = "question";
	private final static String s_PaperTag_Type = "type";	
	private final static String s_PaperTag_Reference = "reference";
	private final static String s_PaperTag_ImageCount = "imagecount";
	
	private final static int s_PaperIdx_ID = 0;
	private final static int s_PaperIdx_Question = 1;
	private final static int s_PaperIdx_Type = 2;
	private final static int s_PaperIdx_Reference = 3;
	private final static int s_PaperIdx_Image = 4;
	
	private HashMap<Integer, QuestionInfo> mQuestions = new HashMap<Integer, QuestionInfo>();
	private ArrayList<QuestionListener> mListeners = new ArrayList<QuestionListener>();
	
	public static interface QuestionListener{
		public abstract void onQuestionArrayChanged(ArrayList<Integer> indexes);
		public abstract void onAddQuestion(Integer index);		
		public abstract void onClearQuestion();
		public abstract void onImageReady(String qid, String imageIndex, Bitmap bmp);
	}
	
	public QuestionManager(){
	}
	
	public void importQuestionsFromLocalFile(){
		mQuestions.clear();
        for (int i = 0; i < Questions.sQuestions.length; ++i){
        	if (i == 0 || i == 6){
        		addQuestion(String.valueOf(i), 0, null, Questions.sQuestions[i], 0);
        	}else if (i == 2){
        		addQuestion(String.valueOf(i), 3, "#123#abc#", Questions.sQuestions[i], 0);        		
        	}else if (i == 3){
        		addQuestion(String.valueOf(i), 3, "#123#", Questions.sQuestions[i], 1);        		
        	}else{
        		addQuestion(String.valueOf(i), 1, "#A#", Questions.sQuestions[i], 0);        		
        	}
        }
        
       	notifyQuestionArrayChanged();
	}
	
	public void importQuestionsFromDB(Context context){
		mQuestions.clear();
		
		PaperDBHelper helper = new PaperDBHelper(context);
		Cursor c = helper.Query();
		c.moveToFirst();
		while (!c.isAfterLast()){
			String id = c.getString(s_PaperIdx_ID);
			String detail = c.getString(s_PaperIdx_Question);
			int type = c.getInt(s_PaperIdx_Type);
			String reference = c.getString(s_PaperIdx_Reference);
			int imageCount = c.getInt(s_PaperIdx_Image);

			addQuestion(id, type, reference, detail, imageCount);
			c.moveToNext();
		}
		c.close();
		helper.close();		
	}
	
	public void exportQuestionsToDB(Context context, AnswerManager answerMgr){
		ArrayList<String> ids = getIDs();
		ContentValues values = new ContentValues();  		
		PaperDBHelper helper = new PaperDBHelper(context);
		for (String id : ids){
			if (id != null){
				values.put(s_PaperTag_ID, id);  
				if (getQuestionItem(id).mDetail != null){
					values.put(s_PaperTag_Question, getQuestionItem(id).mDetail);  
	
					values.put(s_PaperTag_Type, getQuestionItem(id).mType);
					if (getQuestionItem(id).mReference != null){
						values.put(s_PaperTag_Reference, getQuestionItem(id).mReference);
					}
					values.put(s_PaperTag_ImageCount, getQuestionItem(id).mImageCount);
				}
			}
			helper.Insert(values, id);
		}
		helper.close();
	}
	
	public void notifyQuestionArrayChanged(){
		for (QuestionListener l : mListeners){
			l.onQuestionArrayChanged(getIndexes());
		}
	}
	
	public void addQuestion(String id, int type, String reference, String detail, int imageCount){
		addQuestion(new QuestionInfo(id, type, reference, detail, imageCount));
	}
	
	public void addQuestion(QuestionInfo question){
		Integer index = mQuestions.size();  //此处定义了index值
		mQuestions.put(index, question);
		for (QuestionListener l : mListeners){
			l.onAddQuestion(index);
		}
	}

	public void addQuestionImage(String id, String imageIndex, Bitmap bmp){
		QuestionInfo question = getQuestionItem(id);
		if (bmp != null){
			question.addGraphic(imageIndex, bmp);
			for (QuestionListener l : mListeners){
				l.onImageReady(id, imageIndex, bmp);
			}
		}
	}

	public ArrayList<String> getIDs(){
		ArrayList<String> ids = new ArrayList<String>();
		for (Integer index : getIndexes()){
			ids.add(getQuestionItem(index).getQuestionID());
		}
		return ids;
	}
	
	public  ArrayList<Integer> getIndexes(){
		ArrayList<Integer> indexes = new ArrayList<Integer>();

//		Integer[] keys = (Integer[])keysets.toArray(new Integer[keysets.size()]);
		indexes.addAll(mQuestions.keySet());
		Collections.sort(indexes);
		return indexes;
	}

	public String getIdByIndex(Integer index){
		QuestionInfo question = getQuestionItem(index);
		return question.getQuestionID();
	}
	
	public HashMap<Integer, QuestionInfo> getQuestions(){
		return mQuestions;
	}
	
	public QuestionInfo getQuestionItem(String id){
		QuestionInfo question = null;
		if (id != null){
			for (Integer index : mQuestions.keySet()){
				if (mQuestions.get(index).getQuestionID() != null 
						&& mQuestions.get(index).getQuestionID().equals(id)){
					question = mQuestions.get(index);
					break;
				}
			}
		}
		return question;
	}
		
	public QuestionInfo getQuestionItem(Integer index){
		QuestionInfo question = null;
		if (index != null && index >= 0 && index < mQuestions.size()){
			question = mQuestions.get(index);
		}
		return question;
	}

	//获取问题总个数（不包含标题之类的item）
	public int getQuestionCount(){
		ArrayList<Integer> indexes = getIndexes();
		int retCnt = indexes.size();

		if (retCnt > 0){
			int lastIdx = retCnt - 1;
			retCnt = retCnt - getUnQuestionCount(lastIdx);
		}
		return retCnt;
	}
	
	//获取在beforeIndex之前，非问题的个数（在beforeIndex前，剔除标题之类的item后问题总个数）
	public int getUnQuestionCount(Integer beforeIndex){
		int retCnt = 0;
		if (getIndexes().contains(beforeIndex)){
			for (Integer index : getIndexes()){ //由于getIndexes返回的列表有序，所以这么做是合适的
				if (index.equals(beforeIndex)){
					if (mQuestions.get(index).isPaperTitle()){
						retCnt++;
					}
					break;
				}
				if (mQuestions.get(index).isPaperTitle()){
					retCnt++;
				}
			}
		}

		return retCnt;
	}	
	
	public void clear(){
		mQuestions.clear();
		for (QuestionListener l : mListeners){
			l.onClearQuestion();
		}			
	}
	
	public void addListener(QuestionListener l){
		if (!mListeners.contains(l)){
			mListeners.add(l);
		}
	}
}

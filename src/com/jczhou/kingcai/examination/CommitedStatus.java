package com.jczhou.kingcai.examination;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Parcel;
import android.view.View;
import android.widget.ListView;

import com.jczhou.kingcai.R;
import com.jczhou.kingcai.examination.ItemViewHolder;

public class CommitedStatus extends PaperStatus{

	public CommitedStatus(PaperActivity owner){
		super(owner);
	}
	
	@Override
	protected void InitStatus() {
		mStatusOwner.InitUncorrectList(mListFirst, mListSecond);

		//TODO:打开或注释此行代码可以实现立即显示正确与否
		mStatusOwner.ShowCorrectInfo(mListFirst.size());
	}		

	@Override
	public void onFilterClick(ListView listView,
			PaperViewAdapter fullAdapter) {
		mStatusOwner.findViewById(R.id.tableInput).setVisibility(View.GONE);
		mStatusOwner.findViewById(R.id.tableReference).setVisibility(View.GONE);
		
		mFilterLevel = (mFilterLevel + 1) % 3;
		if (mFilterLevel == 0){
			listView.setAdapter(fullAdapter);				
			mStatusOwner.ChangeFilterButtonText(R.string.AllQuestions);
		}else if (mFilterLevel == 1){
			listView.setAdapter(fullAdapter.CloneAdapter(mListFirst));
			mStatusOwner.ChangeFilterButtonText(R.string.CorrectQuestions);
		}else if (mFilterLevel == 2){
			listView.setAdapter(fullAdapter.CloneAdapter(mListSecond));			
			mStatusOwner.ChangeFilterButtonText(R.string.IncorrectQuestions);
		}		
	}

	@Override
	public void onCommitClick() {
		mStatusOwner.showToast(R.string.CommitTips);		
	}

	@Override
	protected void LoadOptionIcon(Context context) {
        mMarkIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_bullet_key_permission);        
        mUnMarkIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete);
	}

	@Override
	public void onLayoutMarkButton(ItemViewHolder holder, Answer answer) {
/*        holder.mark.setEnabled(false);
	    if (answer != null && answer.IsCorrect()){
	    	holder.mark.setImageBitmap(mMarkIcon);
	    }else if (answer != null && !answer.IsCorrect()){
	        holder.mark.setImageBitmap(mUnMarkIcon);
	    }*/
	}

	@Override
	public void onLayoutOptionRadioButton(ItemViewHolder holder, Answer answer) {
		super.onLayoutOptionRadioButton(holder, answer);
		
    	int colorNormal = Color.rgb(0, 0, 0); //black
    	int colorBackground = Color.rgb(220, 220, 220);//236, 236, 236);//(222, 254, 241);
/*		holder.radioBtnA.setTextColor(colorNormal);
		holder.radioBtnA.setBackgroundColor(colorBackground);
		
   		holder.radioBtnB.setTextColor(colorNormal);
		holder.radioBtnB.setBackgroundColor(colorBackground);
		
   		holder.radioBtnC.setTextColor(colorNormal);
		holder.radioBtnC.setBackgroundColor(colorBackground);
		
   		holder.radioBtnD.setTextColor(colorNormal);
		holder.radioBtnD.setBackgroundColor(colorBackground);
		
        holder.radioBtnA.setEnabled(false);
        holder.radioBtnB.setEnabled(false);
        holder.radioBtnC.setEnabled(false);
        holder.radioBtnD.setEnabled(false);   		
   		
    	if (answer!= null && !answer.IsCorrect()){
    		Parcel parcelValues = answer.GetRefAnswer();
        	if (parcelValues != null){
	        	boolean[] bValue = parcelValues.createBooleanArray();
	        	assert bValue.length == 4;
	    
	        	int RectifyFontColor = Color.rgb(248, 254, 131);	 //red
	        	int RectifyBackground = Color.rgb(0, 128, 0);
	        	if (bValue[0]) {
	        		holder.radioBtnA.setTextColor(RectifyFontColor);
	        		holder.radioBtnA.setBackgroundColor(RectifyBackground);
	        	}
	           	if (bValue[1]) {
	           		holder.radioBtnB.setTextColor(RectifyFontColor);
	        		holder.radioBtnB.setBackgroundColor(RectifyBackground);	           		
	           	}
	           	if (bValue[2]) {
	           		holder.radioBtnC.setTextColor(RectifyFontColor);
	        		holder.radioBtnC.setBackgroundColor(RectifyBackground);	           		
	           	}
	           	if (bValue[3]) {
	           		holder.radioBtnD.setTextColor(RectifyFontColor);
	           		holder.radioBtnD.setBackgroundColor(RectifyBackground);	           		
	           	}
        	}
    	}*/
	}

	@Override
	public void onBlankInputDone(Integer questionID, final Answer answer) {
	}

	@Override
	public void onBlankInputShow(Integer questionID, Answer answer) {
		mStatusOwner.ShowAnswerContent(questionID);		
		mStatusOwner.ShowReferenceContent(questionID);
	}	
}

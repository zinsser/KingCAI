package com.king.cai.examination;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;  
import android.graphics.Bitmap;  
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.util.AttributeSet;
import android.view.MotionEvent;  
import android.view.View;  
 
public class KingDrawView extends View {  
 
    private Canvas mCanvas = null;  
    private Paint mPaint = null ;  
    private Bitmap mBitmap = null;    
    private float mOldx, mOldy;
    
    public KingDrawView(Context context, AttributeSet abset) {  
        super(context, abset);  
              
        mBitmap = null;
        mBitmap = Bitmap.createBitmap(300, 400, Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.GRAY);
		mCanvas = null;
		mCanvas = new Canvas();        //建立画布    
		mCanvas.setBitmap(mBitmap);        //设置位图       
        
        if (mPaint != null){
        	mPaint  = null;
        }
        mPaint = new Paint(Paint.DITHER_FLAG);        //设置图层    
        mPaint.setAntiAlias(true);        //可以更平滑                    
        mPaint.setColor(Color.RED);    
        mPaint.setStrokeCap(Paint.Cap.ROUND);        //样式    
        mPaint.setStrokeWidth(3);        //线条宽      
    }  
    
    @Override 
    public boolean onTouchEvent(MotionEvent event) {  
 
        if (event.getAction() == MotionEvent.ACTION_MOVE) {     
            //画线  
            mCanvas.drawLine(mOldx, mOldy, event.getX(), event.getY(), mPaint);     
            invalidate();  
        }  
 
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            mOldx = event.getX();                 
//            mOldy = event.getY();  
            //画一个点  

            mCanvas.drawPoint(mOldx, mOldy, mPaint);
            invalidate();  
        }  
        if (event.getAction() == MotionEvent.ACTION_UP) {      
          
        }  
        //记录当前的鼠标坐标  
        mOldx = event.getX();    
        mOldy = event.getY();  
        return true;  
    }  

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);        	
    }
    
    @Override
    public void onDraw(Canvas c) {                        
    	super.onDraw(c);
    	c.drawBitmap(mBitmap, 0, 0, null);    
    }  
    
    public Bitmap convertToBitmap(){
    	return mBitmap;
    }
    
    public void SaveToImage(String filepath){
		File myCaptureFile = new File(filepath);
		BufferedOutputStream bos;
		try {
			myCaptureFile.createNewFile();			
			bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			mBitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
 }
package com.king.cai.examination;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;  
import android.graphics.Bitmap;
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;  
import android.view.View;  

public class KingDrawView extends View {  
				
	private Path mPath;
	private Paint mPaint;
	private List<Path> mPaths = new ArrayList<Path>();
	private float mDownX, mDownY;
	
	public abstract class Shape{
		public abstract void draw(Canvas canvas);
	}
	
	public class Word extends Shape{

		@Override
		public void draw(Canvas canvas) {
			
		}
	}
	
	public class Line extends Shape{

		@Override
		public void draw(Canvas canvas) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private EraseStatus mEraseStatus = new EraseStatus();
	private PrintStatus mPrintStatus = new PrintStatus();
	private DrawStatus mCurrentStatus = null;
	
	public void switchStatusByMode(boolean eraseMode){
		mCurrentStatus = eraseMode ? mEraseStatus : mPrintStatus; 
	}
	
	public abstract class DrawStatus{
		public abstract boolean onTouchEvent(MotionEvent event);		
	}
	
	public class PrintStatus extends DrawStatus{
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownX =  event.getX();
				mDownY =  event.getY();
				mPath = new Path();
				mPaths.add(mPath);
				mPath.moveTo(mDownX, mDownY);
				break;

			case MotionEvent.ACTION_MOVE:
				mPath.lineTo(event.getX(), event.getY());
				mPath.moveTo(event.getX(), event.getY());
				invalidate();
				break;

			case MotionEvent.ACTION_UP:
				if(mPath.isEmpty()){
					mPaths.clear();
				}
				break;
			}
			return true;
		}
	}
	
	public class EraseStatus extends DrawStatus{

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				mDownX = event.getX();
				mDownY = event.getY();
				mPath = new Path();
				mPath.moveTo(mDownX, mDownY);
				break;
			case MotionEvent.ACTION_UP:
				mPath.lineTo(event.getX(), event.getY());
				
				RectF rectFBound = new RectF();
				mPath.computeBounds(rectFBound, true);
				Rect rectBound = new Rect();
				rectBound.set((int)rectFBound.left, (int)rectFBound.top, 
						      (int)rectFBound.right, (int)rectFBound.bottom);
				for (int i = 0; i < mPaths.size();){
					Path path = mPaths.get(i);
					Rect pathBound = getPathBoundRect(path);
					if (Rect.intersects(rectBound, pathBound)) {
						mPaths.remove(path);
					}else{
						++i;
					}
				}
				invalidate();
				break;
			}
			
			return true;
		}

		private Rect getPathBoundRect(Path path){
			RectF rectFBounds = new RectF();
			if (path != null && !path.isEmpty()){
				path.computeBounds(rectFBounds, true);
			}
			
			return new Rect((int)rectFBounds.left, (int)rectFBounds.top,
					        (int)rectFBounds.right, (int)rectFBounds.bottom);
		}
	}

	public KingDrawView(Context context) {
		super(context);
		init();
	}

	public KingDrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public KingDrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {			
		return mCurrentStatus != null ? mCurrentStatus.onTouchEvent(event) 
									: super.onTouchEvent(event);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		for (Path path : mPaths) {
			if (path != null && !path.isEmpty()){
				canvas.drawPath(path, mPaint);				
			}
		}
		
		super.onDraw(canvas);
	}

	private void init() {		
		mPaint = new Paint();		
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeWidth(3);
		mPaint.setColor(Color.RED);	
		switchStatusByMode(false);
	}
	
	public void drawToBitmap(Bitmap bitmap){
		if (bitmap == null) return ;
		Canvas canvas = new Canvas(bitmap);
		for (Path path : mPaths) {
			if (path != null && !path.isEmpty()){
				canvas.drawPath(path, mPaint);				
			}
		}
	}
 }
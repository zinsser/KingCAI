package com.king.cai.examination;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;  
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
	
	public class PathInfo {
		Path path;  
	    Rect rect;  
	    int color;  
	    float press;  
	      
	    public PathInfo(Path pth, Rect rc, float prss){  
	        path = pth;  
	        rect = new Rect(rc);  
	        color = Color.RED;  
	        press = prss;  
	    }  
	      
	    public PathInfo(Path pth, int left, int top, int right, int bottom){  
	        path = pth;  
	        //rect = new Rect(left, top, right, bottom);  
	       // path.computeBounds(rect, true);
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
			PathInfo pathInfoH;
			float width;

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mCurX = (int) event.getX();
				mCurY = (int) event.getY();
				mPrevX = mCurX;
				mPrevY = mCurY;
				// press
				float pressD = event.getPressure();
				width = getEventPress(pressD);
				mPaint.setStrokeWidth(width);
				mPath = new Path();
				mPath.moveTo(mCurX, mCurY);
				mPath.computeBounds(mRectF, true);
				mRect.set(((int) mRectF.left - (int) width),
						((int) mRectF.top - (int) width),
						((int) mRectF.right + (int) width),
						((int) mRectF.bottom + (int) width));
				pathInfoH = new PathInfo(mPath, mRect, width);
				mPathInfo.add(pathInfoH);
				mPrePress = width;
				invalidate(mRect);
				break;

			case MotionEvent.ACTION_MOVE:
				int N = event.getHistorySize();
				int x = 0;
				int y = 0;
				float pressH = 0;
				int i = 0;
				for (i = 0; i < N; i++) {
					x = (int) event.getHistoricalX(i);
					y = (int) event.getHistoricalY(i);
					// press
					pressH = event.getHistoricalPressure(i);
					width = getEventPress(pressH);
					mPath = new Path();
					mPaint.setStrokeWidth(width);
					drawPoint(mCurX, mCurY, x, y);
					mPath.computeBounds(mRectF, true);
					mRect.set(((int) mRectF.left - (int) width - i),
							((int) mRectF.top - (int) width - i),
							((int) mRectF.right + (int) width - i),
							((int) mRectF.bottom + (int) width - i));
					pathInfoH = new PathInfo(mPath, mRect, width);
					mPathInfo.add(pathInfoH);
					mCurX = x;
					mCurY = y;
					mPrePress = width;
					/*Log.i("HandWriteEditor", "l:" + mRect.left + ",t:" + mRect.top
							+ ",r:" + mRect.right + ",b:" + mRect.right);*/
					invalidate(mRect);
				}

				x = (int) event.getX();
				y = (int) event.getY();
				pressH = event.getPressure();
				width = getEventPress(pressH);
				mPath = new Path();
				mPaint.setStrokeWidth(width);
				drawPoint(mCurX, mCurY, x, y);
				mPath.computeBounds(mRectF, true);
				mRect.set(((int) mRectF.left - (int) width - i), ((int) mRectF.top
						- (int) width - i), ((int) mRectF.right + (int) width - i),
						((int) mRectF.bottom + (int) width - i));
				pathInfoH = new PathInfo(mPath, mRect, width);
				mPathInfo.add(pathInfoH);
				mCurX = x;
				mCurY = y;
				mPrePress = width;
				invalidate(mRect);

				break;

			case MotionEvent.ACTION_UP:
				mPath.computeBounds(mRectF, true);
				float widthUp = getEventPress(event.getPressure());
				if (widthUp > mPrePress + PRESS_OFFSET) {
					widthUp = mPrePress + PRESS_OFFSET;
				} else if (widthUp < mPrePress - PRESS_OFFSET) {
					widthUp = mPrePress - PRESS_OFFSET;
				}
				mRect.set((int) (mRectF.left - widthUp),
						(int) (mRectF.top - widthUp),
						(int) (mRectF.right + widthUp),
						(int) (mRectF.bottom + widthUp));
				mPrePress = widthUp;
				PathInfo pathInfoUp = new PathInfo(mPath, mRect, widthUp);
				mPathInfo.add(pathInfoUp);

				//fadePoints();
				
				mCurX = -1;
				mCurY = 0;
				// invalidate(mRect);
				invalidate();
				break;
			}
			return true;
		}			
	}
	
	public class EraseStatus extends DrawStatus{

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	private Path mPath;
	private Paint mPaint;
	private List<PathInfo> mPathInfo;
	// private float PRESS_LEVEL = 16.5f;
	private float mPrevX, mPrevY;
	private int mCurX, mCurY;
	private float mPrePress;
	private Rect mRect;
	private RectF mRectF;
	private final float PRESS_OFFSET = 5f;

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
		return mCurrentStatus != null ? mCurrentStatus.onTouchEvent(event) : false;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPath(mPath, mPaint);

		Rect r = canvas.getClipBounds();

		PathInfo pathInfo;
		for (int i = 0; i < mPathInfo.size(); i++) {
			pathInfo = mPathInfo.get(i);

			if (Rect.intersects(r, pathInfo.rect)) {
				mPaint.setColor(pathInfo.color);
				// press
				mPaint.setStrokeWidth(pathInfo.press);
				// press over
			}
			canvas.drawPath(pathInfo.path, mPaint);
		}

		mPaint.setColor(Color.RED);
		canvas.drawPath(mPath, mPaint);
	}

	private void init() {
		mRect = new Rect();
		// mBitmap = Bitmap.createBitmap(600, 800,Bitmap.Config.ARGB_8888);
		mPathInfo = new ArrayList<PathInfo>();
		mPath = new Path();
		mPaint = new Paint();
		mRectF = new RectF();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeWidth(3);
		mPaint.setColor(Color.RED);	
		switchStatusByMode(false);
	}

	private void drawPoint(int x1, int y1, int x2, int y2) {
		// mPath.moveTo(x1,y1);
		// mPath.lineTo(x2, y2);

		mPath.moveTo(mPrevX, mPrevY);
		mPath.quadTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2);

		mPrevX = (x1 + x2) / 2;
		mPrevY = (y1 + y2) / 2;
	}

	private float getEventPress(float press) {
		// 0.05~0.25,normal 0.15
		// float width = 10 + (press * 10 - (float) PRESS_LEVEL);
		float width = 3 * press; //8*
		//Log.i("HandWriteEditor", "press:" + press + ",width:" + width);
		return width;
	}
	
	/*private void fadePoints(){  
	    int count = mPathInfo.size();  
	    int start = count - 1;  
	    int end = count - 16;  
	    end = end < 0 ? 0 : end;   
	    PathInfo pathInfo;  
	    int index = 0;  
	    for (int i = start; i >= end; i--, index++){  
	        pathInfo = mPathInfo.get(i);  
	        pathInfo.color = ( pathInfo.color | 0xff000000 ) & mFadeColor[index];  
	        mRect.union(pathInfo.rect);  
	    }  
	}  */
 }
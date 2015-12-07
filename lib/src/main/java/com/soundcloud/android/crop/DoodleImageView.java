package com.soundcloud.android.crop;

import java.io.File;
import java.io.FileOutputStream;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class DoodleImageView extends ImageView {

	public DoodleImageView(Context context) {
		super(context);
	}

	public DoodleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DoodleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private Paint paint;
	private Bitmap oriBitmap;
	private Bitmap newBitmap;
	private float clickX = 0, clickY = 0;
	private float startX = 0, startY = 0;
	private boolean isMove = true;
	private boolean isClear = false;
	private int color = Color.RED;
	private float strokeWidth = 20.0f;
	private Matrix matrixScale = null;
	private float scale;
	private int shiftX;
	private int shiftY;
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public void setBitmap(Bitmap bitmap) {
		oriBitmap = Bitmap.createBitmap(bitmap);
		newBitmap = Bitmap.createBitmap(oriBitmap);
	}

	public void clear() {
		isClear = true;
		newBitmap = Bitmap.createBitmap(oriBitmap);
		invalidate();
	}

	public void setstyle(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(HandWriting(newBitmap), shiftX, shiftY, null);
	}

	public Bitmap HandWriting(Bitmap originalBitmap) {
		Canvas canvas = null;
		if (matrixScale == null) {
			int height = this.getHeight();
			int width = this.getWidth();
			int bitmapHeight = oriBitmap.getHeight();
			int bitmapWidth = oriBitmap.getWidth();
			scale = Math.min((float) height / bitmapHeight, (float) width / bitmapWidth);
			matrixScale = new Matrix();
			matrixScale.postScale(scale, scale);
			newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(), newBitmap.getHeight(), matrixScale,
					true);
			originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(),
					originalBitmap.getHeight(), matrixScale, true);
			shiftX = (int)((width - bitmapWidth * scale) / 2);
			shiftY = (int)((height - bitmapHeight * scale) / 2);
		}
		if (isClear) {
			canvas = new Canvas(newBitmap);
		} else {
			canvas = new Canvas(originalBitmap);
		}
		paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStrokeWidth(strokeWidth);
		if (isMove) {
			canvas.drawLine(startX - shiftX, startY - shiftY, clickX - shiftX, clickY - shiftY, paint);
		}
		canvas.drawCircle(clickX - shiftX, clickY - shiftY, strokeWidth/2, paint);

		startX = clickX;
		startY = clickY;

		if (isClear) {
			return newBitmap;
		}
		return originalBitmap;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		clickX = event.getX();
		clickY = event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			isMove = false;
			invalidate();
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

			isMove = true;
			invalidate();
			return true;
		}

		return super.onTouchEvent(event);
	}

	public Bitmap saveBitmap() {
		Bitmap returnBitmap = HandWriting(newBitmap);
		Matrix initMatrix = new Matrix();
		initMatrix.postScale(1/scale, 1/scale);
		returnBitmap = Bitmap.createBitmap(returnBitmap,0,0,returnBitmap.getWidth(),returnBitmap.getHeight(),initMatrix,true);
		return returnBitmap;
	}

	public void gc() {
		// TODO Auto-generated method stub
		if (!oriBitmap.isRecycled()) {
			oriBitmap.recycle(); // 回收图片所占的内存
			System.gc(); // 提醒系统及时回收
		}
		if (!newBitmap.isRecycled()) {
			newBitmap.recycle(); // 回收图片所占的内存
			System.gc(); // 提醒系统及时回收
		}
	}
}

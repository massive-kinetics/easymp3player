package com.massivekinetics.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.massivekinetics.R;

public class StroboView extends View {

	Paint mPaint;
	int firstColor, secondColor;
	boolean isFirst = true;
	String mText;
	int mAscent;

	public StroboView(Context context) {
		super(context);
		initView();
	}

	public StroboView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		TypedArray a = getResources().obtainAttributes(attrs,
				R.styleable.MKViews);

		String text = a.getString(R.styleable.MKViews_text);
		int textSize = a
				.getDimensionPixelSize(R.styleable.MKViews_textSize, 16);
		firstColor = a.getColor(R.styleable.MKViews_firstColor, 0xff0000);
		secondColor = a.getColor(R.styleable.MKViews_secondColor, 0xff0000);

		setView(text, textSize, firstColor, secondColor);
	}

	private void setView(String text, int textSize, int firstColor,
			int secondColor) {
		if (text != null)
			mText = text;
		if (textSize != 0)
			mPaint.setTextSize(textSize);

		if (firstColor != 0 && secondColor != 0)
			mPaint.setColor(isFirst ? firstColor : secondColor);

		requestLayout();
		invalidate();
	}

	private void initView() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		int density = (int) getResources().getDisplayMetrics().scaledDensity;
		mPaint.setColor(android.R.color.white);
		mPaint.setTextSize(16 * density);
		setPadding(3, 3, 3, 3);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		int left = getPaddingLeft() + (isFirst?50:20);
		int top = getPaddingTop() - mAscent + (isFirst?50:20);

		canvas.drawText(mText, left, top, mPaint);

		setNew();
		Rect r = new Rect(left+10, top+10, left+30, top+30);	
		requestRectangleOnScreen(r, true);
		invalidate();
	}

	private void setNew() {

		mPaint.setColor(isFirst ? firstColor : secondColor);

		isFirst = !isFirst;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int w = getResources().getDisplayMetrics().widthPixels;
		int h =getResources().getDisplayMetrics().heightPixels;
		//measureWidth(widthMeasureSpec);
		//measureHeight(heightMeasureSpec);
		setMeasuredDimension(measureWidth(widthMeasureSpec),				measureHeight(heightMeasureSpec));
		//setMeasuredDimension(w, h);
	}

	private int measureWidth(int widthMeasureSpec) {

		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		if (specMode == MeasureSpec.EXACTLY)
			return specSize;
		else {
			int result = 0;
			result = getPaddingLeft() + getPaddingRight()
					+ (int) mPaint.measureText(mText);
			if (specMode == MeasureSpec.AT_MOST)
				result = Math.min(result, specSize);
			return result;
		}
	}

	private int measureHeight(int heightMeasureSpec) {
		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		int specSize = MeasureSpec.getSize(heightMeasureSpec);
		mAscent = (int) mPaint.ascent();
		if (specMode == MeasureSpec.EXACTLY)
			return specSize;
		else {
			int result = 0;
			int descent = (int) mPaint.descent();
			result = getPaddingTop() + getPaddingBottom()
					+ (descent + (-mAscent));
			if (specMode == MeasureSpec.AT_MOST)
				result = Math.min(result, specSize);
			return result;
		}
	}

}

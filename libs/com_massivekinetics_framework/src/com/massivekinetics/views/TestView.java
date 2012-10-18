package com.massivekinetics.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.massivekinetics.R;

public class TestView extends View {

	private Paint mTextPaint;
	private String mText;
	private int mAscent;

	public TestView(Context context) {
		super(context);
		initView();
	}

	public TestView(Context context, AttributeSet attrs) {
		super(context);
		initView();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.MKViews);

		CharSequence text = a.getString(R.styleable.MKViews_text);
		if (text != null)
			setText(text.toString());

		int textSize = a.getDimensionPixelOffset(R.styleable.MKViews_textSize,
				0);

		float textDimen = a.getDimension(R.styleable.MKViews_textSize, 0);

		if (textSize > 0)
			setTextSize(textSize);

		int textColor = a.getColor(R.styleable.MKViews_firstColor, 0xFF00000);
		setTextColor(textColor);

		a.recycle();
	}

	private void initView() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		// Must manually scale the desired text size to match screen density
		float den = getResources().getDisplayMetrics().density;
		mTextPaint.setTextSize(16 * den);
		mTextPaint.setColor(0xFF000000);
		setPadding(3, 3, 3, 3);
	}

	private void setText(String text) {
		mText = text;
		requestLayout();
		invalidate();
	}

	private void setTextColor(int textColor) {
		mTextPaint.setColor(textColor);
		invalidate();
	}

	private void setTextSize(int textSize) {
		mTextPaint.setTextSize(textSize);
		requestLayout();
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureHeight(int heightMeasureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		int specSize = MeasureSpec.getSize(heightMeasureSpec);

		mAscent = (int)mTextPaint.ascent();
		
		float mDescent = mTextPaint.descent();
		
		if (specMode == MeasureSpec.EXACTLY)
			result = specSize;
		else {
			//result = (int) mTextPaint.getTextSize() + getPaddingTop()					+ getPaddingBottom();
			
			result = (int)(-mAscent + mDescent) + getPaddingBottom() + getPaddingTop();
			
			if (specMode == MeasureSpec.AT_MOST)
				result = Math.min(result, specSize);
		}

		return result;
	}

	private int measureWidth(int widthMeasureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);

		if (specMode == MeasureSpec.EXACTLY)
			result = specSize;
		else {
			result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
					+ getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST)
				result = Math.max(result, specSize);
		}

		return result;
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(mText, getPaddingLeft(), getPaddingTop() - mAscent, mTextPaint);
    }


}

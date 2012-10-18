package com.massivekinetics.graphics;

import android.app.Activity;
import android.content.Context;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public class AndroidPaint extends Activity {
	private Paint mPaint;
	private MaskFilter mBlurFilter;
	private MaskFilter mEmbossFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new PaintView(this));
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.SQUARE);
		mPaint.setStrokeWidth(12);
		mEmbossFilter = new EmbossMaskFilter(new float[]{1, 1, 1}, 0.4f, 8, 2);
		
	}

	public class PaintView extends View {

		public PaintView(Context context) {
			super(context);
		}
	}

}

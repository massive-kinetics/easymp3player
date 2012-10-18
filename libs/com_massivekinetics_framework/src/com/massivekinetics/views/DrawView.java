package com.massivekinetics.views;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.massivekinetics.R;

public class DrawView extends View {

	private static int tileSize;
	private static int tileCountX, tileCountY;
	private static int offsetX, offsetY;
	private static List<Bitmap> tileList;
	private static int[][] tileGrid;

	private static int RED = 1, YELLOW = 2, GREEN = 3;

	RedrawHandler uiHandler = new RedrawHandler();
	Paint mPaint = new Paint();
	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		tileCountX = (int) Math.floor(w / tileSize);
		tileCountY = (int) Math.floor(h / tileSize);
		offsetX = (w - tileSize * tileCountX) / 2;
		offsetY = (h - tileSize * tileCountY) / 2;
		tileGrid = new int[tileCountX][tileCountY];
		clearTiles();
		
		update();
	}
	
	private void update() {
		//H letter
		setTile(randColor(), 1, 1);
		setTile(randColor(), 1, 2);
		setTile(randColor(), 1, 3);
		setTile(randColor(), 1, 4);
		setTile(randColor(), 1, 5);
		
		setTile(randColor(), 2, 3);
		setTile(randColor(), 3, 3);
		setTile(randColor(), 4, 3);
		
		setTile(randColor(), 5, 1);
		setTile(randColor(), 5, 2);
		setTile(randColor(), 5, 3);
		setTile(randColor(), 5, 4);
		setTile(randColor(), 5, 5);
		
		//I letter
		setTile(randColor(), 7, 1);
		setTile(randColor(), 7, 2);
		setTile(randColor(), 7, 3);
		setTile(randColor(), 7, 4);
		setTile(randColor(), 7, 5);
		
		setTile(randColor(), 8, 4);
		setTile(randColor(), 9, 3);
		setTile(randColor(), 10, 2);
		
		setTile(randColor(), 11, 1);
		setTile(randColor(), 11, 2);
		setTile(randColor(), 11, 3);
		setTile(randColor(), 11, 4);
		setTile(randColor(), 11, 5);
		
		//K letter
		setTile(randColor(), 13, 1);
		setTile(randColor(), 13, 2);
		setTile(randColor(), 13, 3);
		setTile(randColor(), 13, 4);
		setTile(randColor(), 13, 5);
		
		setTile(randColor(), 14, 3);
		setTile(randColor(), 15, 2);
		setTile(randColor(), 16, 1);
		
		setTile(randColor(), 15, 4);
		setTile(randColor(), 16, 5);
		
		
	}
	
	int randColor(){
		int res = 0;
		while(res==0 || res>3)
			res = (int)(Math.random() * 10);
		return res;
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrs(attrs);
		init();
	}

	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(attrs);
		init();
	}

	void initAttrs(AttributeSet attrs) {
		TypedArray a = getResources().obtainAttributes(attrs,
				R.styleable.MKViews);
		tileSize = a.getInteger(R.styleable.MKViews_tileSize, 12);
		a.recycle();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		for (int i = 0; i < tileCountX; i++)
			for (int j = 0; j < tileCountY; j++) {
				if(tileGrid[i][j]>0)
					canvas.drawBitmap(tileList.get(tileGrid[i][j]), offsetX + i * tileSize,
						offsetY + j * tileSize, mPaint);
			}
		
		uiHandler.sleep(600);
		
	}

	void init() {
		resetTiles();
		Drawable red = getResources().getDrawable(R.drawable.redstar);
		Drawable yellow = getResources().getDrawable(R.drawable.yellowstar);
		Drawable green = getResources().getDrawable(R.drawable.greenstar);
		loadTile(RED, red);
		loadTile(YELLOW, yellow);
		loadTile(GREEN, green);
	}

	void loadTile(int type, Drawable tile) {
		Bitmap bitmap = Bitmap.createBitmap(tileSize, tileSize,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		tile.setBounds(0, 0, tileSize, tileSize);
		tile.draw(canvas);
		tileList.add(type, bitmap);
	}

	void setTile(int bitmapType, int x, int y) {
		tileGrid[x][y] = bitmapType;
	}

	void clearTiles() {
		for (int i = 0; i < tileCountX; i++)
			for (int j = 0; j < tileCountY; j++)
				setTile(0, i, j);
	}

	void resetTiles() {
		tileList = new ArrayList<Bitmap>(4);
		tileList.add(0, null);
	}

	
	
	class RedrawHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			update();
			invalidate();
		}
		
		public void sleep(long mSec){
			removeMessages(0);
			Message msg = obtainMessage(0);
			sendMessageDelayed(msg, mSec);
		}
	}
	
}


package com.massivekinetics.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Scroller;

import com.massivekinetics.interfaces.OnScreenChangeListener;

public class Swyper extends ViewGroup {

	List<OnScreenChangeListener> listeners = new ArrayList<OnScreenChangeListener>();
	
	// Layout constants
	private static final int INVALID_SCREEN = -1;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private final static int SWYPE_VELOCITY = 1000;

	// Android system objects for convenience
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	private int mMaximumVelocity;

	// View screen indexes
	private int mCurrentScreen;
	private int mNextScreen = INVALID_SCREEN;

	// View state
	private int mTouchState = TOUCH_STATE_REST;

	// Last touch X coordinate
	private float lastMotionX;

	private boolean mFirstLayout = true;

	public Swyper(Context context) {
		super(context);
		init();
	}

	public Swyper(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setAdapter(BaseAdapter adapter) {
		if (adapter == null)
			throw new RuntimeException("Swyper adapter cannot be null");
		for (int i = 0; i < adapter.getCount(); i++) {
			addView(adapter.getView(i, null, Swyper.this));
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(widthMeasureSpec);

		int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.measure(widthMeasureSpec, heightMeasureSpec);
		}

		if (mFirstLayout) {
			scrollTo(mCurrentScreen * width, 0);
			mFirstLayout = false;
		}

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int childLeft = 0;

		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {

			View child = getChildAt(i);

			if (child.getVisibility() != View.GONE) {
				int childWidth = child.getMeasuredWidth();
				int childHeight = child.getMeasuredHeight();
				child.layout(childLeft, 0, childLeft + childWidth, childHeight);
				childLeft += childWidth;
			}
		}
	}

	private void init() {
		mScroller = new Scroller(getContext());
		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (getChildCount() == 0)
			return super.onTouchEvent(ev);

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
		final int action = ev.getAction();
		final float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			if (!mScroller.isFinished())
				mScroller.abortAnimation();
			lastMotionX = x;

			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;

		case MotionEvent.ACTION_MOVE:

			int xDiff = Math.abs((int) (x - lastMotionX));
			boolean isScrolling = xDiff > mTouchSlop;
			if (isScrolling)
				mTouchState = TOUCH_STATE_SCROLLING;

			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final int deltaX = (int) (lastMotionX - x);
				lastMotionX = x;
				final int scrollX = getScrollX();

				if (deltaX < 0) {
					if (scrollX > 0) {
						// Scroll to deltaX if left position is bigger; scroll
						// to available otherwise
						int availableToScroll = -Math.min(scrollX,
								Math.abs(deltaX));
						scrollBy(availableToScroll, 0);
					}
				} else if (deltaX > 0) {
					int availableToScroll = getChildAt(getChildCount() - 1)
							.getRight() - scrollX - getWidth();
					scrollBy(Math.min(availableToScroll, deltaX), 0);
				}

			}

			break;

		case MotionEvent.ACTION_UP:

			if (mTouchState == TOUCH_STATE_SCROLLING) {
				mVelocityTracker.computeCurrentVelocity(1000, 6000);
				int velocityX = (int) mVelocityTracker.getXVelocity();

				if (velocityX > SWYPE_VELOCITY && mCurrentScreen > 0) {
					snapToScreen(mCurrentScreen - 1);
					Log.e("ddd", "1");
				} else if (velocityX < -SWYPE_VELOCITY
						&& mCurrentScreen < (getChildCount() - 1)) {
					snapToScreen(mCurrentScreen + 1);
					Log.e("ddd", "2");
				} else {
					snapToDestination();
					Log.e("ddd", "3");
				}
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}

				mTouchState = TOUCH_STATE_REST;
			}
			break;

		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return true;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else if (mNextScreen != INVALID_SCREEN) {
			mCurrentScreen = Math.max(0,
					Math.min(mNextScreen, getChildCount() - 1));
			notifyOnScreenChange(mCurrentScreen);
			mNextScreen = INVALID_SCREEN;
		}
	}

	public void snapToScreen(int screenIndex) {
		if (!mScroller.isFinished()) {
			Log.e("ddd", "4");
			return;
		}

		int whichScreen = Math.max(0,
				Math.min(screenIndex, getChildCount() - 1));
		mNextScreen = whichScreen;

		int newX = whichScreen * getWidth();
		int offsetX = newX - getScrollX();
		mScroller.startScroll(getScrollX(), 0, offsetX, 0,
				Math.abs(offsetX) * 2);
		invalidate();
	}

	private void snapToDestination() {
		int scrollX = getScrollX();
		int width = getWidth();
		int destinationIndex = (scrollX + width / 2) / width;
		snapToScreen(destinationIndex);
	}
	
	public void addOnScreenChangeListener(OnScreenChangeListener listener){		
		listeners.add(listener);
	}
	
	public void removeOnScreenChangeListener(OnScreenChangeListener listener){		
		listeners.remove(listener);
	}
	
	protected void notifyOnScreenChange(int newScreenIndex){
		for(OnScreenChangeListener l : listeners){
			l.onScreenChage(newScreenIndex);
		}
	}
	
	
}

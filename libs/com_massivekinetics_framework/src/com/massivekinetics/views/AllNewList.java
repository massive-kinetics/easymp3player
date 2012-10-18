package com.massivekinetics.views;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;

public class AllNewList extends AdapterView<Adapter> {

	// List of possible states
	private final static int TOUCH_STATE_RESTING = 0;
	private final static int TOUCH_STATE_CLICK = 1;
	private final static int TOUCH_STATE_SCROLL = 2;

	/** Current touch state */
	private int mTouchState = TOUCH_STATE_RESTING;

	/** Distance to drag before we intercept touch events */
	private static final int TOUCH_SCROLL_THRESHOLD = 10;

	/** Used to check for long press actions */
	private Runnable mLongPressRunnable;

	/** Represents an invalid child index */
	private static final int INVALID_INDEX = -1;

	private final static int MODE_ABOVE = 0, MODE_BELOW = -1;

	private Adapter mAdapter;

	private int mListTop = 0;
	private float mListFirstItemTop;

	private int mListTopOffset;
	private int mFirstItemPosition;
	private int mLastItemPosition;

	LinkedList<View> viewCache = new LinkedList<View>();

	// Touch events
	private float mStartTouchX, mStartTouchY;

	private Rect mRect;

	public AllNewList(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (mAdapter == null)
			return;

		if (getChildCount() == 0) {
			mLastItemPosition = -1;
			fillListDown(mListTop, 0);
		}

		else {
			int top1 = getChildAt(0).getTop();
			int offset = mListTop + mListTopOffset - top1;
			removeNonVisibleViews(offset);
			fillList(offset);
		}

		positionItems();
	}

	private void removeNonVisibleViews(int offset) {
		int childCount = getChildCount();

		if ((mLastItemPosition != (mAdapter.getCount() - 1)) && childCount > 1) {
			View firstChild = getChildAt(0);
			while (firstChild != null && (firstChild.getBottom() + offset) < 0) {
				removeViewInLayout(firstChild);
				putViewToCache(firstChild);
				mFirstItemPosition++;

				childCount--;

				mListTopOffset += firstChild.getMeasuredHeight();
				if (childCount > 1)
					firstChild = getChildAt(0);
				else
					firstChild = null;
			}
		}

		if (mFirstItemPosition != 0 && childCount > 1) {
			View lastChild = getChildAt(childCount - 1);
			while (lastChild != null
					&& (lastChild.getTop() + offset) > getHeight()) {
				removeViewInLayout(lastChild);
				putViewToCache(lastChild);

				mLastItemPosition--;
				childCount--;
				if (childCount > 1)
					lastChild = getChildAt(childCount - 1);
				else
					lastChild = null;
			}
		}
	}

	void fillList(int offset) {
		final int bottomEdge = getChildAt(getChildCount() - 1).getBottom();
		final int topEdge = getChildAt(0).getTop();

		fillListDown(bottomEdge, offset);
		fillListUp(topEdge, offset);
	}

	void fillListDown(int bottomEdge, int offset) {
		while (bottomEdge + offset < getHeight()
				&& mLastItemPosition < mAdapter.getCount() - 1) {
			mLastItemPosition++;

			View childView = mAdapter.getView(mLastItemPosition,
					getViewFromCache(), this);
			addAndMeasureChilds(childView, MODE_BELOW);
			bottomEdge += childView.getMeasuredHeight();
		}
	}

	void fillListUp(int topEdge, int offset) {
		while (topEdge + offset > 0 && mFirstItemPosition > 0) {
			mFirstItemPosition--;

			View childView = mAdapter.getView(mFirstItemPosition,
					getViewFromCache(), this);
			addAndMeasureChilds(childView, MODE_ABOVE);
			topEdge -= childView.getMeasuredHeight();
			mListTopOffset -= childView.getMeasuredHeight();
		}
	}

	void positionItems() {

		final int layoutWidth = getWidth();
		int top = mListTop + mListTopOffset;

		for (int i = 0; i < getChildCount(); i++) {
			View childView = getChildAt(i);
			int childWidth = childView.getMeasuredWidth();
			int childHeight = childView.getMeasuredHeight();
			int left = (layoutWidth - childWidth) / 2;
			int right = left + childWidth;
			int bottom = top + childHeight;
			childView.layout(left, top, right, bottom);
			top += childHeight;
		}
	}

	void addAndMeasureChilds(final View child, int mode) {
		LayoutParams params = child.getLayoutParams();
		if (params == null)
			params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

		int width = getWidth();
		addViewInLayout(child, mode, params, true);
		child.measure(MeasureSpec.EXACTLY | width, MeasureSpec.UNSPECIFIED);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mAdapter == null || getChildCount() == 0)
			return false;

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN: {
			startTouch(event);

			break;
		}
		case MotionEvent.ACTION_MOVE: {

			if (mTouchState == TOUCH_STATE_CLICK) {
				startScrollIfNeeded(event);
			}

			if (mTouchState == TOUCH_STATE_SCROLL) {
				scrollList((int)(event.getY() - mStartTouchY));
			}
			
			break;
		}

		case MotionEvent.ACTION_UP: {
			if (mTouchState == TOUCH_STATE_CLICK) {
				//clickChildAt(event.getX(), event.getY());
			}
			//endTouch();
			break;
		}

		default:
			//endTouch();
			break;

		}

		return true;
	}

	private void scrollList(int scrolledDistance) {
		mListTop = (int)mListFirstItemTop + scrolledDistance;
		requestLayout();
	}

	private void startTouch(MotionEvent event) {
		mStartTouchX = event.getX();
		mStartTouchY = event.getY();
		mListFirstItemTop = getChildAt(0).getTop() - mListTopOffset;

		startLongPressCheck();

		mTouchState = TOUCH_STATE_CLICK;
	}

	private void startLongPressCheck() {
		if (mLongPressRunnable == null) {
			mLongPressRunnable = new Runnable() {
				@Override
				public void run() {
					if (mTouchState == TOUCH_STATE_CLICK) {
						final int index = getContainingChildIndex(mStartTouchX,
								mStartTouchY);
						if (index != INVALID_INDEX) {
							Toast.makeText(getContext(), "Index " + (mFirstItemPosition + index), Toast.LENGTH_SHORT).show();
							longClickChild(index);
						}
					}
				}
			};
		}
		postDelayed(mLongPressRunnable, 1000);
	}

	protected int getContainingChildIndex(float x, float y) {
		if (mRect == null)
			mRect = new Rect();

		int childIndex = INVALID_INDEX;

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.getHitRect(mRect);
			if (mRect.contains((int) x, (int) y)) {
				childIndex = i;
			}
		}

		return childIndex;
	}

	protected void longClickChild(final int index) {
		final View itemView = getChildAt(index);
		final int position = mFirstItemPosition + index;
		final long id = mAdapter.getItemId(position);
		final OnItemLongClickListener listener = getOnItemLongClickListener();
		if (listener != null) {
			listener.onItemLongClick(this, itemView, position, id);
		}
	}

	private boolean startScrollIfNeeded(MotionEvent event) {
		final int xPos = (int) event.getX();
		final int yPos = (int) event.getY();
		if (xPos > (mStartTouchX + TOUCH_SCROLL_THRESHOLD)
				|| xPos < (mStartTouchX - TOUCH_SCROLL_THRESHOLD)
				|| yPos > (mStartTouchY + TOUCH_SCROLL_THRESHOLD)
				|| yPos < (mStartTouchY - TOUCH_SCROLL_THRESHOLD)) {

			removeCallbacks(mLongPressRunnable);
			mTouchState = TOUCH_STATE_SCROLL;
			return true;
		}
		return false;
	}

	private void putViewToCache(View invisibleView) {
		viewCache.add(invisibleView);
	}

	private View getViewFromCache() {
		if (viewCache.isEmpty())
			return null;
		else
			return viewCache.remove();
	}

	@Override
	public void setAdapter(Adapter adapter) {
		if (adapter != null) {
			mAdapter = adapter;
			removeAllViewsInLayout();
			requestLayout();
		}
	}

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public void setSelection(int position) {
		throw new RuntimeException("unimplemented");
	}

}

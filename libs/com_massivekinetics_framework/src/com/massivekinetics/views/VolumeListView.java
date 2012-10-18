package com.massivekinetics.views;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;

public class VolumeListView extends AdapterView<Adapter> {
	private Adapter mAdapter;
	private int mListTop = 0;

	private int mListTopOffset;

	private int mFirstItemPosition = 0, mLastItemPosition = -1;
	private float mTouchStart, mListTopStart;

	/** Used to check for long press actions */
	private Runnable mLongPressRunnable;

	/** Reusable rect */
	private Rect mRect;

	/** A list of cached (re-usable) item views */
	private final LinkedList<View> mCachedItemViews = new LinkedList<View>();

	public VolumeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mAdapter == null)
			return;

		if (getChildCount() == 0) {
			mLastItemPosition = -1;
			fillListDown(mListTop, 0);
		} else {
			int offset = mListTop + mListTopOffset - getChildAt(0).getTop();
			removeNonVisibleViews(offset);
			fillList(offset);
		}
		positionItems();
		invalidate();
	}

	private void addAndMeasureChild(View child, int mode) {
		LayoutParams lp = child.getLayoutParams();
		if (lp == null) {
			lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		}
		addViewInLayout(child, mode, lp, true);
		int itemWidth = getWidth();
		child.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.UNSPECIFIED);
	}

	private void positionItems() {
		int top = mListTop;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();

			int left = (getWidth() - width) / 2;
			child.layout(left, top, left + width, top + height);

			top += height;

		}
	}

	private void fillList(final int offset) {
		int bottomEdge = getChildAt(getChildCount() - 1).getBottom();
		fillListDown(bottomEdge, offset);

		int topEdge = getChildAt(0).getTop();
		fillListUp(topEdge, offset);

	}

	private void fillListDown(int bottomEdge, int offset) {
		while (bottomEdge + offset < getHeight()
				&& mLastItemPosition < mAdapter.getCount()-1) {
			mLastItemPosition++;
			final View childView = mAdapter.getView(mLastItemPosition,
					getCachedView(), this);
			addAndMeasureChild(childView, -1);
			bottomEdge += childView.getMeasuredHeight();
		}
	}

	private void fillListUp(int topEdge, int offset) {
		while (topEdge + offset > 0 && mFirstItemPosition > 0) {
			mFirstItemPosition--;
			final View childView = mAdapter.getView(mFirstItemPosition,
					getCachedView(), this);
			addAndMeasureChild(childView, 0);
			int childHeight = childView.getMeasuredHeight();
			topEdge -= childHeight;
			mListTopOffset-=childHeight;
		}
	}

	private void removeNonVisibleViews(int offset) {

		int childCount = getChildCount();

		if (mLastItemPosition != (mAdapter.getCount() - 1) && childCount > 1) {
			View firstChild = getChildAt(0);
			while (firstChild != null && firstChild.getBottom() + offset < 0) {
				removeViewInLayout(firstChild);
				childCount--;
				mCachedItemViews.add(firstChild);
				mFirstItemPosition++;
				
				mListTopOffset+=firstChild.getMeasuredHeight();
				if(childCount>1)
					firstChild = getChildAt(0);
				else
					firstChild = null;
			}

		}
		
		// if we are not at the top of the list and have more than one child
        if (mFirstItemPosition != 0 && childCount > 1) {
            // check if we should remove any views in the bottom
            View lastChild = getChildAt(childCount - 1);
            while (lastChild != null && lastChild.getTop() + offset > getHeight()) {
                // remove the bottom view
                removeViewInLayout(lastChild);
                childCount--;
                mCachedItemViews.addLast(lastChild);
                mLastItemPosition--;

                // Continue to check the next child only if we have more than
                // one child left
                if (childCount > 1) {
                    lastChild = getChildAt(childCount - 1);
                } else {
                    lastChild = null;
                }
            }
        }

	}

	private View getCachedView() {
		return null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void setSelection(int position) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public View getSelectedView() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getChildCount() == 0)
			return false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			mTouchStart = event.getY();
			mListTopStart = getChildAt(0).getTop();
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			float scrollDistance = event.getY() - mTouchStart;
			mListTop = (int) (mListTopStart + scrollDistance);
			requestLayout();
			break;
		}
		}
		return true;

	}

}

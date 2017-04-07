package com.znvoid.demo1.bookReading;

import com.znvoid.demo1.imf.BookCenterAreaTouchListener;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;

/**
 * Created by zn on 2016/12/12.
 */

public class BookView extends View {
	private BookFactory mBookFactory;
	public static final int PAGE_NONE = 0;
	private static final int PAGE_NEXT = 1;
	private static final int PAGE_BACK = 2;
	private static final int PAGE_UPDATA = 3;

	private boolean beginTouch;

	private RectF centerF = new RectF();

	private int mMode = 3;
	private boolean menushowing;
	private Point startPoint = new Point();

	private float bondline;
	Bitmap mCurPageBitmap = null;
	Bitmap mNextPageBitmap = null;
	private int width;
	private int heigth;
	private boolean isOnAnimation;
	private BookCenterAreaTouchListener bookCenterAreaTouchListener;
	private int scaledTouchSlop;
	

	public BookView(Context context) {
		this(context, null);
	}

	public BookView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BookView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		 
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		init();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void init() {
		centerF.set(getWidth() * 2 / 5, getHeight() * 2 / 5, getWidth() * 3 / 5, getHeight() * 3 / 5);
		width = getWidth();
		heigth = getHeight();

	}

	public void setBookFactory(BookFactory bookFactory) {
		this.mBookFactory = bookFactory;
		bookFactory.setTarge(this);
		mCurPageBitmap = mBookFactory.getCurPage();
		// width=mCurPageBitmap.getWidth();
		// heigth=mCurPageBitmap.getHeight();
		// setLayoutParams(new ViewGroup.LayoutParams(width,heigth));
		// init();
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mMode == PAGE_UPDATA) {
			if (mCurPageBitmap != null)
				canvas.drawBitmap(mCurPageBitmap, 0, 0, null);
			mMode = PAGE_NONE;

			return;
		}

		if (mMode == PAGE_BACK) {
			// canvas.drawBitmap(mCurPageBitmap, new Rect((int) bondline, 0,
			// width, heigth), new Rect((int) bondline, 0, width, heigth),
			// null);
			canvas.drawBitmap(mCurPageBitmap, 0, 0, null);

			canvas.drawBitmap(mNextPageBitmap, bondline - width, 0, null);
		}

		if (mMode == PAGE_NEXT) {

			// canvas.drawBitmap(mNextPageBitmap,new
			// Rect((int)bondline,0,width,heigth),new
			// Rect((int)bondline,0,width,heigth),null);

			canvas.drawBitmap(mNextPageBitmap, 0, 0, null);
			canvas.drawBitmap(mCurPageBitmap, bondline - width, 0, null);

		}
		if (mMode == PAGE_NONE) {
			if (mCurPageBitmap != null)
				canvas.drawBitmap(mCurPageBitmap, 0, 0, null);
			if (mNextPageBitmap != null)
				canvas.drawBitmap(mNextPageBitmap, 0, 0, null);

		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 执行动画过程中不允许手势操作
		if (isOnAnimation) {
			return false;
		}

		float x0 = event.getX();
		float y0 = event.getY();
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		switch (action) {

		case MotionEvent.ACTION_DOWN:

			if (!menushowing) {

				if (isOnCenterArea(x0, y0)) {
					if (bookCenterAreaTouchListener != null) {
						bookCenterAreaTouchListener.onAreaTouch();
						menushowing = true;
					}

					beginTouch = false;

					return false;
				}
				beginTouch = true;
				startPoint.set((int) event.getX(), (int) event.getY());
				mMode = 0;

			} else {
				menushowing = false;
				if (bookCenterAreaTouchListener != null) {
					bookCenterAreaTouchListener.onOutSideAreaTouch();
				}

			}

			break;

		case MotionEvent.ACTION_MOVE:

			if (!menushowing && beginTouch && !isOnAnimation) {
				float diff = Math.abs(event.getX() - startPoint.x);
				if (diff > scaledTouchSlop) {
					doMove(event);
				}

			}

			break;

		case MotionEvent.ACTION_UP:

			if (!menushowing && beginTouch && !isOnAnimation) {
				doActionUp();
				beginTouch = false;
			}

			break;

		}

		return true;

	}

	private boolean isOnCenterArea(float x, float y) {
		return centerF.contains(x, y);

	}

	private void doActionUp() {

		switch (mMode) {

		case PAGE_NONE:

			if (startPoint.x > width / 2) {
				mMode = PAGE_NEXT;
				mCurPageBitmap = mBookFactory.getCurPage();
				mNextPageBitmap = mBookFactory.getNextPage();
				if (mBookFactory.islastPage()) {

					mMode = PAGE_NONE;
					beginTouch = false;
					break;
				}

				bondline = width;

				DoNextPageAnimation();

			} else {

				mMode = PAGE_BACK;
				mCurPageBitmap = mBookFactory.getCurPage();
				mNextPageBitmap = mBookFactory.getPrePage();
				if (mBookFactory.isfirstPage()) {

					mMode = PAGE_NONE;
					beginTouch = false;
					break;
				}
				bondline = 0.1f;
				DoPrePageAnimation();

			}
			//
			//
			// break;
		case PAGE_BACK:

			DoPrePageAnimation();
			break;
		case PAGE_NEXT:

			DoNextPageAnimation();
			break;

		}

	}

	private void doMove(MotionEvent event) {
		
		if (mMode == PAGE_NONE) {
			if (event.getX() - startPoint.x > 0) {

				mMode = PAGE_BACK;
				mCurPageBitmap = mBookFactory.getCurPage();
				mNextPageBitmap = mBookFactory.getPrePage();
				if (mBookFactory.isfirstPage()) {

					mMode = PAGE_NONE;
					beginTouch = false;
					return;
				}

			} else {

				mMode = PAGE_NEXT;
				mCurPageBitmap = mBookFactory.getCurPage();
				mNextPageBitmap = mBookFactory.getNextPage();
				if (mBookFactory.islastPage()) {
					mMode = PAGE_NONE;
					beginTouch = false;
					return;
				}

			}
		}
		if (mMode == PAGE_BACK) {

			bondline = event.getX() - startPoint.x;

		}
		if (mMode == PAGE_NEXT) {
			bondline = event.getX() - startPoint.x + width;

		}
		if (0 <= bondline && bondline <= width) {

			postInvalidate();
		}

	}

	private void DoPrePageAnimation() {
		isOnAnimation = true;

		ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
		animator.setDuration(800);
		animator.setInterpolator(new LinearInterpolator());
		final float leftwith = width - bondline;
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float f = animation.getAnimatedFraction();
				float ls = leftwith * f;

				if (bondline < width) {
					bondline = bondline + ls;
					postInvalidate();
				} else {
					bondline = width;
					postInvalidate();
					animation.cancel();

					isOnAnimation = false;
				}

			}

		});

		animator.start();

	}

	private void DoNextPageAnimation() {
		isOnAnimation = true;

		ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
		animator.setDuration(800);
		animator.setInterpolator(new LinearInterpolator());
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float f = 1 - animation.getAnimatedFraction();
				float p = bondline * f;

				if (p > 0) {
					bondline = (int) p;
					postInvalidate();
				} else {
					bondline = 0;
					postInvalidate();
					animation.cancel();

					isOnAnimation = false;

				}
			}
		});

		animator.start();

	}

	/*
	 * 用于来自factory的更新，字体颜色等在factory内调用
	 */
	public void handleChange(Bitmap bitmap) {
		mCurPageBitmap = bitmap;
		mMode = PAGE_UPDATA;

		postInvalidate();

	}

	public void setBookCenterAreaTouchListener(BookCenterAreaTouchListener bookCenterAreaTouchListener) {
		this.bookCenterAreaTouchListener = bookCenterAreaTouchListener;
	}
	
}

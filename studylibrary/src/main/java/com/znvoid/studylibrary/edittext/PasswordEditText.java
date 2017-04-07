package com.znvoid.studylibrary.edittext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.Toast;

import com.znvoid.studylibrary.R;

import java.lang.reflect.Field;

public class PasswordEditText extends EditText {

	private Paint mPaint;
	private Paint mPaintContent;
	private Paint mPaintArc;
	private int raius;
	private int circleRadiu;
	private float mPadding;
	private int maxLineSize;
	private boolean isAddText;
	private int textLegth;
	private float interpllatorTime;
	private PaintLastAnim paintLastAnim;
	private IPasswordCallback iPasswordCallback;
	private String content;

	public IPasswordCallback getiPasswordCallback() {
		return iPasswordCallback;
	}

	public void setiPasswordCallback(IPasswordCallback iPasswordCallback) {
		this.iPasswordCallback = iPasswordCallback;
	}

	public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inintPaint();
	}

	public PasswordEditText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PasswordEditText(Context context) {
		this(context, null);
	}

	private void inintPaint() {
		setCursorVisible(false);
		setFocusableInTouchMode(true);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.GRAY);
		mPaint.setStyle(Paint.Style.STROKE);

		mPaintContent = new Paint();
		mPaintContent.setAntiAlias(true);
		mPaintContent.setColor(Color.WHITE);
		mPaintContent.setStyle(Paint.Style.FILL);

		mPaintArc = new Paint();
		mPaintArc.setAntiAlias(true);
		mPaintArc.setStyle(Paint.Style.FILL);

		raius = dp2px(6);
		circleRadiu = dp2px(6);
		 
		isAddText=true;
		 textLegth=0;
		
		maxLineSize = getMaxLenght();
		if (maxLineSize==0) {
			maxLineSize=6;
//			setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
			newInputFilter();
		}
		paintLastAnim = new PaintLastAnim();
		paintLastAnim.setDuration(200);
		paintLastAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
				if (textLegth==maxLineSize) {
					if (iPasswordCallback!=null) {
						iPasswordCallback.completeText(content);
					}
				}
				
				
				
			}
		});
	}

	private int dp2px(int i) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (i * scale + 0.5);

	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 白色背景
		RectF rectF = new RectF(mPadding, mPadding, getMeasuredWidth() - mPadding, getMeasuredHeight() - mPadding);
		canvas.drawRoundRect(rectF, raius, raius, mPaintContent);
		// 线框
		mPaint.setStyle(Paint.Style.STROKE);
		canvas.drawRoundRect(rectF, raius, raius, mPaint);

		float cx;
		float cy = getMeasuredHeight() / 2;

		float half = getMeasuredWidth() / maxLineSize / 2;
		mPaint.setStrokeWidth(0.5f);
		// 画框
		for (int i = 1; i < maxLineSize; i++) {
			float x = getMeasuredWidth() / maxLineSize * i;
			canvas.drawLine(x, 0, x, getMeasuredHeight(), mPaint);

		}

		// 画小点
		for (int i = 0; i < maxLineSize; i++) {
			
			float x = getMeasuredWidth() / maxLineSize*i + half;
			if (isAddText) {

				if (i < textLegth - 1) {
					canvas.drawCircle(x, cy, circleRadiu, mPaintArc);
				} else if (i == textLegth - 1) {
					canvas.drawCircle(x, cy, circleRadiu * interpllatorTime, mPaintArc);
				}
			} else {

				if (i < textLegth) {
					canvas.drawCircle(x, cy, circleRadiu, mPaintArc);
				} else if (i == textLegth ) {
					
					canvas.drawCircle(x, cy, circleRadiu * (1 - interpllatorTime), mPaintArc);
				}

			}

		}

	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {

		if (getText().toString().length() > this.textLegth) {
			isAddText = true;
			
		} else {
			isAddText = false;
		}
		
		if (textLegth <= maxLineSize) {
//		if (text.length() <= maxLineSize) {

			textLegth=text.length();
			content=text.toString();
			
			if (paintLastAnim != null) {
				clearAnimation();
				startAnimation(paintLastAnim);

			} else {
				invalidate();
			}
			super.onTextChanged(text, start, lengthBefore, lengthAfter);
		}


	
		
		
		
		
		
	}

	private class PaintLastAnim extends Animation {

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			super.applyTransformation(interpolatedTime, t);

			PasswordEditText.this.interpllatorTime = interpolatedTime;
			postInvalidate();

		}

	}

	// 得到布局文件中定义的最长字符数

	private int getMaxLenght() {

		int length = 0;
		InputFilter[] inputFilters = getFilters();

		for (InputFilter inputFilter : inputFilters) {
			Class<?> c = inputFilter.getClass();

			if (c.getName().equals("android.text.InputFilter$LengthFilter")) {
				Field[] fields = c.getDeclaredFields();

				for (Field field : fields) {
					if (field.getName().equals("mMax")) {
						field.setAccessible(true);
						try {
							length = field.getInt(inputFilter);

						} catch (Exception e) {

						}
					}
				}

			}

		}
		return length;
	}

	interface IPasswordCallback{
		
		void completeText(String content); 
	}
	long toastTime;
	private void newInputFilter(){
		InputFilter[] filters = new InputFilter[1];

		filters[0] = new InputFilter.LengthFilter(6) {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
									   Spanned dest, int dstart, int dend) {
				if (source.length() > 0 && dest.length() == 6) {
					if ((System.currentTimeMillis() - toastTime) > 3000) {
						toastTime = System.currentTimeMillis();
						Toast.makeText(getContext(),
										R.string.edit_content_limit,
										Toast.LENGTH_SHORT).show();
					}
				}


				return super.filter(source, start, end, dest, dstart, dend);
			}
		};
		setFilters(filters);

	}

}
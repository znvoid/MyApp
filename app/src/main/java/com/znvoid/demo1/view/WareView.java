package com.znvoid.demo1.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.znvoid.demo1.R;

/**
 * Created by zn on 2017/3/2.
 */

public class WareView extends View {
    /**
     * 振幅高度
     */
    private float mAmplitude;
    /**
     * 水平面
     */
    private float mWaterLevel;
    /**
     * 波长
     */
    private float mWaveLength;
    /**
     * 频率
     */
    private double mFrequency;
    /**
     * 水的画笔
     */
    private Paint mViewPaint;

    private int mColor;
    /**
     * 边界画笔
     */
    private Paint mBorderPaint;

    private int mWidth;
    private int mHeight;

    public static final int DEFAULT_BEHIND_WAVE_COLOR = Color.parseColor("#28FFFFFF");
    public static final int DEFAULT_FRONT_WAVE_COLOR = Color.parseColor("#3CFFFFFF");
    private BitmapShader mWaveShader;
    private Matrix mShaderMatrix;
    private float mWaveLengthRatio=1;
    private float mAmplitudeRatio=1;
    private float mWaterLevelRatio=1;
    private float mWaveShiftRatio=1;

    private float mRotateDegrees=0;
	private ValueAnimator valueAnimator;

    public void setmRotateDegrees(float mRotateDegrees) {
        this.mRotateDegrees = mRotateDegrees;
    }

    public void setmWaveShiftRatio(float mWaveShiftRatio) {
        this.mWaveShiftRatio = mWaveShiftRatio;
    }

    public void setmWaterLevelRatio(float mWaterLevelRatio) {
        this.mWaterLevelRatio = mWaterLevelRatio;
    }

    public WareView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WareView(Context context) {
        this(context, null);
    }

    public WareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WareView);

        mWaterLevel = a.getFraction(R.styleable.WareView_waterLevel, 0, 100, 50);

        mAmplitude = a.getFloat(R.styleable.WareView_amplitude, -1);
        mWaveLength = a.getFloat(R.styleable.WareView_waveLength, -1);
        mFrequency = a.getFloat(R.styleable.WareView_frequency, -1);
        mColor = a.getColor(R.styleable.WareView_waterColor, DEFAULT_FRONT_WAVE_COLOR);
        a.recycle();
        mShaderMatrix=new Matrix();
        mViewPaint = new Paint();
        mViewPaint.setAntiAlias(true);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();

        if (mAmplitude == -1)
            mAmplitude = mHeight * 0.05f;
        if (mWaveLength == -1)
            mWaveLength = mWidth;
        if (mFrequency == -1)
            mFrequency = 2.0f * Math.PI / getWidth();

        createShader();
        postInvalidate();

    }

    public float getmWaterLevel() {
        return mWaterLevel;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWaveShader != null) {
//            mShaderMatrix.setScale(
//                    mWaveLengthRatio,
//                    mAmplitudeRatio / 0.05f,
//                    0,
//                    mWaveLength);
            float dy=0.5f* (1- mWaterLevelRatio)*mHeight;
            mWaterLevel=mWaterLevel- dy/mHeight*100;
            if (mWaterLevel>100){
                mWaterLevel=100;
                dy=0;
            }else if (mWaterLevel<0){
                mWaterLevel=0;
                dy=0;

            }


            mShaderMatrix.postTranslate(
                    mWidth*(1-mWaveShiftRatio)*0.02f ,
                    dy);

            mWaveShader.setLocalMatrix(mShaderMatrix);
            canvas.save();
            canvas.rotate(mRotateDegrees,mWidth / 2f, mHeight / 2f);
            float radius=mWidth>mHeight?mHeight:mWidth;
            canvas.drawCircle(mWidth / 2f, mHeight / 2f, radius / 2f, mViewPaint);
            canvas.restore();
        }

    }

    private void createShader() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint gPaint=new Paint();
        gPaint.setStrokeWidth(2);
        gPaint.setAntiAlias(true);
        final int endX = mWidth + 1;
        final int endY = mHeight + 1;

        float[] waveY = new float[endX];
        gPaint.setColor(getBehindColor(mColor));
        for (int beginX = 0; beginX < endX; beginX++) {
            double wx = beginX * mFrequency;
            float beginY = (float) (mWaterLevel*mHeight/100 + mAmplitude * Math.sin(wx));
            canvas.drawLine(beginX, beginY, beginX, endY, gPaint);
            waveY[beginX] = beginY;
        }

        gPaint.setColor(getFrontColor(mColor));
        final int wave2Shift = (int) (mWaveLength / 4);
        for (int beginX = 0; beginX < endX; beginX++) {
//            if (waveY[(beginX + wave2Shift) % endX]>waveY[beginX]) {
//
//                canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX], beginX, waveY[beginX], mViewPaint);
//            }
            canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX], beginX, endY, gPaint);
        }
        mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        mViewPaint.setShader(mWaveShader);
    }

    private int getFrontColor(int color) {

        return Color.argb(Color.alpha(DEFAULT_FRONT_WAVE_COLOR), Color.red(color), Color.green(color), Color.blue(color));
    }

    private int getBehindColor(int color) {

        return Color.argb(Color.alpha(DEFAULT_BEHIND_WAVE_COLOR), Color.red(color), Color.green(color), Color.blue(color));
    }

    public int clcMixtureColor(int color) {


        int a = (int) (255 - (255 - Color.alpha(DEFAULT_FRONT_WAVE_COLOR)) * (1 - Color.alpha(DEFAULT_BEHIND_WAVE_COLOR)*1.0f/255));
       return 0;




    }
    public void startAnimt(){
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(6000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                postInvalidate();

            }
        });
        valueAnimator.start();

    }

    public void stopAnimt() {
    	if (valueAnimator!=null) {
    		valueAnimator.cancel();
		}
    	
	}
}

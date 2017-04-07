package com.znvoid.demo1.view;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 图片缩放显示
 * Created by zn on 2016/12/10.
 */

public class MatrixImageView extends ImageView {
    private GestureDetector mGestureDetector;
    private Matrix mMatrix = new Matrix();
    /**
     * 图片长度
     */
    private float mImageWidth;
    /**
     * 图片高度
     */
    private float mImageHeight;
    /**
     * 拖拉照片模式
     */
    private static final int MODE_DRAG = 1;
    /**
     * 放大缩小照片模式
     */
    private static final int MODE_ZOOM = 2;
    /**
     * 禁止模式模式
     */
    private static final int MODE_UNABLE = 3;

    /**
     * 最大缩放级别
     */
    float mMaxScale;
    //初始时图片缩放系数
    private float mScale;


   //模式
    private int mMode = 0;//

    /**
     * 当前Matrix
     */
    private Matrix mCurrentMatrix = new Matrix();

    /**
     * 用于记录开始时候的坐标位置
     */
    private PointF startPoint = new PointF();


    private float beforeLenght, afterLenght;

    private int current_x, current_y;

    public MatrixImageView(Context context) {

        this(context,null);

    }



    public MatrixImageView(Context context, AttributeSet attrs) {
        this(context,attrs,0);

    }

    public MatrixImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        MyListener mlistener = new MyListener();
        mGestureDetector = new GestureDetector(getContext(), mlistener);


        //背景设置为balck
        setBackgroundColor(Color.BLACK);
        //将缩放类型设置为FIT_CENTER，表示把图片按比例扩大/缩小到View的宽度，居中显示
        setScaleType(ScaleType.FIT_CENTER);

    }


    @Override
    public void setImageBitmap(Bitmap bm) {
        setScaleType(ScaleType.FIT_CENTER);
        super.setImageBitmap(bm);
        mImageWidth = 0;
       

    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                if (getScaleType() != ScaleType.MATRIX)
                    setScaleType(ScaleType.MATRIX);

                if (mImageWidth == 0) {
                    mMatrix.set(getImageMatrix());
                    float[] values = new float[9];
                    mMatrix.getValues(values);
                    mImageWidth = (getWidth()- values[Matrix.MTRANS_X] * 2) / values[Matrix.MSCALE_X];
                    mImageHeight = (getHeight() - values[Matrix.MTRANS_Y] * 2) / values[Matrix.MSCALE_Y];
                    mScale = values[Matrix.MSCALE_X];
                    mMaxScale = mScale * 6;


                }


                //设置拖动模式
                mMode = MODE_DRAG;

                startPoint.set(event.getX(), event.getY());

                current_x = (int) event.getX();
                current_y = (int) event.getY();

                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                if (event.getPointerCount() == 2) {
                    mMode = MODE_ZOOM;
                    beforeLenght = getDistance(event);// 获取两点的距离
                }
                break;

            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                mMode = MODE_UNABLE;
                break;

            // 多点松开
            case MotionEvent.ACTION_POINTER_UP:

                mMode = MODE_DRAG;

                break;


        }


        return mGestureDetector.onTouchEvent(event);
    }

    private void onTouchMove(MotionEvent event) {

        if (mMode == MODE_DRAG) {
            current_x = (int) event.getX();
            current_y = (int) event.getY();

            float distanceX = current_x - startPoint.x;
            float distanceY = current_y - startPoint.y;


            if (Math.sqrt(distanceX * distanceX + distanceY * distanceY) > 10f) {

                startPoint.set(event.getX(), event.getY());
                //在当前基础上移动
                mCurrentMatrix.set(getImageMatrix());
                float[] values = new float[9];
                mCurrentMatrix.getValues(values);
                distanceX = checkDxBound(values, distanceX);
                distanceY = checkDyBound(values, distanceY);

                mCurrentMatrix.postTranslate(distanceX, distanceY);
                setImageMatrix(mCurrentMatrix);
            }


        } else if (mMode == MODE_ZOOM) {

            afterLenght = getDistance(event);// 获取两点的距离

            float gapLenght = afterLenght - beforeLenght;// 变化的长度

            if (Math.abs(gapLenght) > 5f) {
                float scale_temp = afterLenght / beforeLenght;

                this.setScale(scale_temp);

                beforeLenght = afterLenght;
            }


        }


    }

    private float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);


    }

    /**
     * 处理缩放
     **/
    void setScale(float scale) {
        mCurrentMatrix.set(getImageMatrix());//初始化Matrix
        float[] values = new float[9];
        mCurrentMatrix.getValues(values);
        scale = checkScale(scale, values);
        if (scale > 1) {//放大

            mCurrentMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);

        }else if (scale<1){//缩小
            float tScaleX=values[Matrix.MSCALE_X];
            float dx=(tScaleX*mImageWidth/2-getWidth()/2+values[Matrix.MTRANS_X])*mScale/tScaleX;
            float dy=(tScaleX*mImageHeight/2-getHeight()/2+values[Matrix.MTRANS_Y])*mScale/tScaleX;

            mCurrentMatrix.postTranslate(-dx,-dy);

            mCurrentMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);

        }


        setImageMatrix(mCurrentMatrix);

    }

    /**
     * 检验scale，使图像缩放后不会超出最大倍数
     *
     * @param scale
     * @param values
     * @return
     */
    private float checkScale(float scale, float[] values) {
        if (scale * values[Matrix.MSCALE_X] > mMaxScale) {
            scale = mMaxScale / values[Matrix.MSCALE_X];

        } else if (scale * values[Matrix.MSCALE_X] < mScale) {
            scale = mScale / values[Matrix.MSCALE_X];
        }
        return scale;
    }

    private float checkDyBound(float[] values, float dy) {
        float height = getHeight();
        if (mImageHeight * values[Matrix.MSCALE_Y] < height)
            return 0;
        if (values[Matrix.MTRANS_Y] + dy > 0)
            dy = -values[Matrix.MTRANS_Y];
        else if (values[Matrix.MTRANS_Y] + dy < -(mImageHeight * values[Matrix.MSCALE_Y] - height))
            dy = -(mImageHeight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
        return dy;
    }


    private float checkDxBound(float[] values, float dx) {
        float width = getWidth();

        if (mImageWidth * values[Matrix.MSCALE_X] < width) {

            return 0;
        }
        if (values[Matrix.MTRANS_X] + dx > 0)
            dx = -values[Matrix.MTRANS_X];
        else if (values[Matrix.MTRANS_X] + dx < -(mImageWidth * values[Matrix.MSCALE_X] - width))
            dx = -(mImageWidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
        return dx;
    }

    class MyListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //双击事件

            float[] values = new float[9];
            getImageMatrix().getValues(values);

            //获取当前X轴缩放级别
            float scale = values[Matrix.MSCALE_X];
            System.out.println(scale);


            if (scale < mScale*2) {
                scale=2;

            } else if (scale<mScale*4){

                scale = 4;

            }else {
                scale=1;
            }
            mCurrentMatrix.set(mMatrix);
            mCurrentMatrix.postScale(scale,scale,startPoint.x,startPoint.y);

            setImageMatrix(mCurrentMatrix);


            return true;
        }


    }



}

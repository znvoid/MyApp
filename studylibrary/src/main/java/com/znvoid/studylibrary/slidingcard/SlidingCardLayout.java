package com.znvoid.studylibrary.slidingcard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.znvoid.studylibrary.R;

/**
 * Created by zn on 2017/3/25.
 */
@CoordinatorLayout.DefaultBehavior(SlidingCardBehavior.class)
public class SlidingCardLayout extends FrameLayout {
    private TextView head;
    private int mHeadHeight;//头部高度
    public boolean canSling;

    String[] list={"电脑","电视","冰箱","风扇","手机","电脑1","电视1","冰箱1","风扇1","手机1"};
    public SlidingCardLayout(Context context) {

       this(context,null);
    }



    public SlidingCardLayout(Context context, AttributeSet attrs) {
       this(context,attrs,0);
    }

    public SlidingCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.slidingcard,this);
        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.sliding_recyclerview);
         head= (TextView) findViewById(R.id.sliding_head);
        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.SlidingCardLayout);
        head.setText(array.getString(R.styleable.SlidingCardLayout_android_text));
        head.setBackgroundColor( array.getColor(R.styleable.SlidingCardLayout_android_colorBackground, Color.BLUE));
        canSling=array.getBoolean(R.styleable.SlidingCardLayout_canSliding,true);
        array.recycle();

//        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(linearLayoutManager);
        SampleAdapter adapter=new SampleAdapter(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w!=oldw||h!=oldh){
            mHeadHeight=findViewById(R.id.sliding_head).getMeasuredHeight();
        }
    }

    public int getHeadHeight(){
        return mHeadHeight;
    }


    public boolean isInhead=false;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                isInhead=isOnhead(ev.getX(),ev.getY());

                break;
            case MotionEvent.ACTION_UP:
                isInhead=false;
                break;

        }


        return super.dispatchTouchEvent(ev);
    }


    private boolean isOnhead(float x,float y){

        return x>head.getLeft()&&x<head.getRight()&&y<head.getBottom()&&y>head.getTop();

    }

}

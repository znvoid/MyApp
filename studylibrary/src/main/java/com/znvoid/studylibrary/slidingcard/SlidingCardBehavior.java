package com.znvoid.studylibrary.slidingcard;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by zn on 2017/3/25.
 */
public class SlidingCardBehavior extends CoordinatorLayout.Behavior<SlidingCardLayout> {

    private int mInitTop;

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, SlidingCardLayout child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        int offset = getChildMeasureOffset(parent, child);
        int height = View.MeasureSpec.getSize(parentHeightMeasureSpec) - offset;
        child.measure(parentWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));

        return true;

    }

    private int getChildMeasureOffset(CoordinatorLayout parent, SlidingCardLayout child) {
        int offset = 0;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view != child && view instanceof SlidingCardLayout) {
                offset += ((SlidingCardLayout) view).getHeadHeight();

            }

        }
        return offset;
    }



    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, SlidingCardLayout child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);

//        SlidingCardLayout previous = getPreviousChild(parent, child);
//        if (previous != null) {
//            int offset = previous.getTop() + previous.getHeadHeight();
//            child.offsetTopAndBottom(offset);
//        }
//        mInitTop = child.getTop();
//        int offset=child.getHeadHeight()*parent.indexOfChild(child);
        int offset=getPreviousHeadHeight(parent,child);
        child.offsetTopAndBottom(offset);

        mInitTop=child.getTop();
        return true;
    }

    private int  getPreviousHeadHeight(CoordinatorLayout parent, SlidingCardLayout child) {
       int height=0;
        int index = parent.indexOfChild(child);
        for (int i = index-1; i >= 0; i--) {
            View view = parent.getChildAt(i);
            if (view instanceof SlidingCardLayout) {
                height+=((SlidingCardLayout)view).getHeadHeight();
            }
        }
        return height;

    }
    private SlidingCardLayout getPreviousChild(CoordinatorLayout parent, SlidingCardLayout child) {
        int index = parent.indexOfChild(child);
        for (int i = index-1; i >= 0; i--) {
            View view = parent.getChildAt(i);
            if (view instanceof SlidingCardLayout) {
                return (SlidingCardLayout) view;
            }
        }
        return null;

    }

    private SlidingCardLayout getNextChild(CoordinatorLayout parent, SlidingCardLayout child) {

        for (int i = parent.indexOfChild(child)+1; i <=parent.getChildCount(); i++) {
            View view=parent.getChildAt(i);
            if (view!=null&& view instanceof SlidingCardLayout){
                return (SlidingCardLayout) view;
            }
        }
        return null;
    }
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, SlidingCardLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        boolean isVertical = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;

        return isVertical  && child== directTargetChild&&child.isInhead&&child.canSling;
//        return isVertical  && child== directTargetChild&&child.isInhead&&coordinatorLayout.indexOfChild(child)!=0;

    }






    @Override
    public void onNestedPreScroll(CoordinatorLayout parent, SlidingCardLayout child, View target, int dx, int dy, int[] consumed) {

//        child.offsetTopAndBottom(-dy);
        consumed[1]= scoll(child,dy,mInitTop,mInitTop+child.getHeight()-child.getHeadHeight());
        shiftSliding(consumed[1],parent,child);
    }

    private void shiftSliding(int shift,CoordinatorLayout parent, SlidingCardLayout child) {

        if (shift==0)
            return;
        if (shift<0){//往上滑动
            SlidingCardLayout current=child;
            SlidingCardLayout previous=getPreviousChild(parent,current);
            while (previous!=null){
                int offset=getHeaderOverlap(previous,current);
                if (offset>0)
                    previous.offsetTopAndBottom(-offset);
                current=previous;
                previous=getPreviousChild(parent,current);
            }


        }else {

            SlidingCardLayout current=child;
            SlidingCardLayout next=getNextChild(parent,child);
            while (next!=null){
                int offset=getHeaderOverlap(current,next);
                if (offset>0)
                    next.offsetTopAndBottom(offset);
                current=next;
                next=getNextChild(parent,current);
            }

        }

    }



    private int getHeaderOverlap(SlidingCardLayout above, SlidingCardLayout below) {



        return above.getTop()+above.getHeadHeight()-below.getTop();
    }

    private int scoll(SlidingCardLayout child,int dy,int minOff,int maxOff){
        int inintoff=child.getTop();
        int offset=clap(inintoff-dy,minOff,maxOff)-inintoff;
        child.offsetTopAndBottom(offset);
        return offset;

    }

    private int clap(int i, int minOff, int maxOff) {
        if (i<minOff)
            return minOff;
        if (i>maxOff)
            return maxOff;
        return i;

    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, SlidingCardLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {


    }



}

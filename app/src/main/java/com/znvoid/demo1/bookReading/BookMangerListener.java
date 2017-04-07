package com.znvoid.demo1.bookReading;



import android.graphics.Bitmap;
import android.graphics.Paint;

/**
 * Created by zn on 2016/12/14.
 */

public interface BookMangerListener {

/*
paint 变化时code加1
progress 变化时code加10
背景 变化时code加100


 */
    void onChange(Paint paint, int progress, int color, Bitmap bitmap);

    int getProgress();
}

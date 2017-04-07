package com.znvoid.demo1.bookReading;



import android.graphics.Bitmap;
import android.graphics.Paint;

import java.nio.MappedByteBuffer;

/**
 * Created by zn on 2016/12/14.
 */

public interface BookManger {

   void  openBook(String path);
    /*
    设置字体大小
     */
    void setTextSize(int size);


    /*
    设置字体颜色
    */
    void setTextColor(int color);
    /*
    设置背景(颜色，bitmap)
     */
    void setBackgroud(Bitmap bitmap);
    void setBackgroud(int color);

    /*
    设置进度
     */
    void setProgress(int progress);
    /*
    设置进度百分比
     */
    void setProgress(float progress);
    /*
    获得进度
     */
    int getProgress();
    float getProgressPercent();

    void notifChange();

    void addListener(BookMangerListener listener);

    String getBookName();
    int getBookLength();
    MappedByteBuffer getBookBuffer();

    String getBookCharsetName();
    Paint getPaint();


}

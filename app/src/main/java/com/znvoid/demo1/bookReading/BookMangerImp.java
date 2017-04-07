package com.znvoid.demo1.bookReading;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.znvoid.demo1.util.FileUtils;
import com.znvoid.demo1.util.Utils;

/**
 * Created by zn on 2016/12/14.
 */

public class BookMangerImp implements BookManger {
    private File book_file;
    private String bookCharset="GBK";
    private MappedByteBuffer buf;
    private Paint mPaint=new Paint();
    private BookMangerListener mlistener;
    private int progress=0;
    private long bookLen=0;
    private int bgColor=0xffff9e85;
    private Bitmap bgBitmap;

    private  boolean mPaintChange=false;
    private  boolean progressChange=false;
    private  boolean bgColorChange=false;
    private  boolean bgBitmapChange=false;


    public BookMangerImp(String path){

        openBook(path);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(24);
        mPaint.setColor(Color.BLACK);


    }



    @Override
    public  void openBook(String path) {
        //File s= Environment.getExternalStorageDirectory();
        book_file = new File(path);
        
		FileUtils fileUtils = new FileUtils();
		try {
			bookCharset = fileUtils.guessFileEncoding(book_file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        bookLen = book_file.length();

        try {
            buf = new RandomAccessFile(book_file, "r").getChannel().map(FileChannel.MapMode.READ_ONLY, 0, bookLen);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setTextSize(int size) {
    	mPaint.setTextSize(Utils.dip2px(size));
        mPaintChange=true;

    }

    @Override
    public void setTextColor(int color) {
        mPaint.setColor(color);
        mPaintChange=true;
    }

    @Override
    public void setBackgroud(Bitmap bitmap) {
            bgBitmap=bitmap;
        bgBitmapChange=true;
    }

    @Override
    public void setBackgroud(int color) {
        bgColor=color;
        bgColorChange=true;
    }

    @Override
    public void setProgress(int progress) {
        this.progress=progress;
        progressChange=true;
    }

    @Override
    public void setProgress(float progress) {
        if (progress>=0&&progress<=1){
            this.progress = (int) (progress * bookLen);
            progressChange=true;
        }
    }

    @Override
    public int getProgress() {
        return mlistener.getProgress();
    }

    @Override
    public void notifChange() {
        Paint cPaint=mPaintChange==true?mPaint:null;
        int p=progressChange==true?progress:-1;
        int c=bgColorChange==true?bgColor:-1;
        Bitmap b=bgBitmapChange==true?bgBitmap:null;

       // System.out.println("notifChange");


        mlistener.onChange(cPaint,p,c,b);

        mPaintChange=false;
         progressChange=false;
        bgColorChange=false;
        bgBitmapChange=false;
    }

    @Override
    public void addListener(BookMangerListener listener) {
            mlistener=listener;
    }

    @Override
    public String getBookName() {
    	String name=book_file.getName();
    	int index=name.lastIndexOf(".");
    	if (index!=-1) {
    		name=name.substring(0,index);
		}
    	
        return name;
    }

    @Override
    public int getBookLength() {
        return (int) bookLen;
    }

    @Override
    public MappedByteBuffer getBookBuffer() {
        return buf;
    }

    @Override
    public String getBookCharsetName() {
        return bookCharset;
    }

    @Override
    public Paint getPaint() {
        return mPaint;
    }



	@Override
	public float getProgressPercent() {
	
		return getProgress()/(bookLen*1.0f);
	}
	
	public void releaseSource() {
		  
		
	}
}

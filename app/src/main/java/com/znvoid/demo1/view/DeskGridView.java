package com.znvoid.demo1.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.GridView;

import com.znvoid.demo1.R;

public class DeskGridView extends GridView {
	private Bitmap background;
	

	public DeskGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
	}

	public DeskGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.DeskGridView); 
		
		int id= a.getResourceId(R.styleable.DeskGridView_mbackground, -1);
		
		if (id==-1) {
			background = BitmapFactory.decodeResource(getResources(),
			          R.drawable.bookshelf_layer_center1);
		}else {
			background = BitmapFactory.decodeResource(getResources(),
			         id);
		}
		
	}

	public DeskGridView(Context context) {
		super(context);
		
	}
	
     @Override
	     protected void dispatchDraw(Canvas canvas) {
	         int count = getChildCount();
	         int top = count > 0 ? getChildAt(0).getTop() : 0;
	         int backgroundWidth = background.getWidth();
	         int backgroundHeight = background.getHeight()+2;
	         int width = getWidth();
	         int height = getHeight();

	        for (int y = top; y < height; y += backgroundHeight) {
	             for (int x = 0; x < width; x += backgroundWidth) {
	                canvas.drawBitmap(background, x, y, null);
	             }
	        }
	
	        super.dispatchDraw(canvas);
	     }
	

}

package com.znvoid.demo1.menu;

import com.znvoid.demo1.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ReaderTextSize extends PopupWindow {
	
	private int mWindow_With;
	private int mWindow_Heigh;
	private ReaderTextSizeChangeListener mListener;
	public ReaderTextSize(Context mContext,int textSize) {
		super();
		
		inite(mContext,textSize);
	}
	private void inite(Context mContext,int textSize) {
		WindowManager m = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		m.getDefaultDisplay().getMetrics(metrics);

		mWindow_With = metrics.widthPixels;
		mWindow_Heigh = metrics.heightPixels;

		int rootwith = mWindow_With;
		int rootheigh = mWindow_Heigh / 7;
		
		LinearLayout layout = (LinearLayout) LinearLayout.inflate(mContext,R.layout.reader_control_fontsize, null);

		this.setWidth(rootwith);
		this.setHeight(rootheigh);
		this.setFocusable(false);
		this.setOutsideTouchable(false);
		this.setContentView(layout);
		ColorDrawable dw = new ColorDrawable(Color.parseColor("#88000000"));
		this.setBackgroundDrawable(dw);
		
		SeekBar seekBar=(SeekBar) layout.findViewById(R.id.reader_control_fontsize_seekbar);
		seekBar.setProgress(textSize-12);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				
				mListener.onTextSizeChange(progress+12);
				
				
				
			}
		});
		
	}
	
	public interface ReaderTextSizeChangeListener{
		
		void onTextSizeChange(int size);
		
	}
	public void setReaderTextSizeChangeListener(ReaderTextSizeChangeListener listener) {
		mListener=listener;
		
	}
	
}

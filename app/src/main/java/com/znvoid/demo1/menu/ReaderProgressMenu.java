package com.znvoid.demo1.menu;

import java.text.DecimalFormat;

import com.znvoid.demo1.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ReaderProgressMenu extends PopupWindow implements OnClickListener {
	
	private int mWindow_With;
	private int mWindow_Heigh;
	private ReaderProgressChangeListener mListener;
	private TextView mTextView;
	private EditText mEditText;
	SeekBar seekBar;
	
	private boolean fistFalg;
	public ReaderProgressMenu(Context mContext) {
		super();
		
		inite(mContext);
	}
	private void inite(Context mContext) {
		WindowManager m = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		m.getDefaultDisplay().getMetrics(metrics);

		mWindow_With = metrics.widthPixels;
		mWindow_Heigh = metrics.heightPixels;

		int rootwith = mWindow_With;
		int rootheigh = mWindow_Heigh / 7;
		
		LinearLayout layout = (LinearLayout) LinearLayout.inflate(mContext,R.layout.reader_control_jump, null);

		this.setWidth(rootwith);
		this.setHeight(rootheigh);
		this.setFocusable(false);
		this.setOutsideTouchable(false);
		this.setContentView(layout);
		ColorDrawable dw = new ColorDrawable(Color.parseColor("#88000000"));
		this.setBackgroundDrawable(dw);
		
		seekBar=(SeekBar) layout.findViewById(R.id.reader_control_jump_seekbar);
		mEditText=(EditText) layout.findViewById(R.id.reader_control_jump_edittext);
		mTextView=(TextView) layout.findViewById(R.id.reader_control_jump_percent);
		Button mButton=(Button) layout.findViewById(R.id.reader_control_jump_button);
		Button mButtonadd=(Button) layout.findViewById(R.id.reader_control_jump_add);
		Button mButtonreduced=(Button) layout.findViewById(R.id.reader_control_jump_reduced);
		mButton.setOnClickListener(this);
		mButtonadd.setOnClickListener(this);
		mButtonreduced.setOnClickListener(this);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fistFalg) {
					fistFalg=false;
					return;
				}
				mListener.onProgressChange(progress/1000f);
				mTextView.setText(new DecimalFormat("#0.0").format(progress /10.0f)+"%");
				
				
			}
		});
		
	}
	
	public interface ReaderProgressChangeListener{
		
		void onProgressChange(float progress);
		
	}
	public void setReaderProgressChangeListener(ReaderProgressChangeListener listener) {
		mListener=listener;
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reader_control_jump_button:
			String lString=mEditText.getText().toString().trim();
			if (lString=="") {
				break;
			}
			float p=Float.valueOf(lString);
			if (p>=0&&p<=100) {
				
				
				seekBar.setProgress((int) p*10);
				
			}
			
			break;
		case R.id.reader_control_jump_add:
			int p1=seekBar.getProgress();
			p1+=1;
			
			seekBar.setProgress(p1);
			break;
		case R.id.reader_control_jump_reduced:
			int p2=seekBar.getProgress();
			p2-=1;
			seekBar.setProgress(p2);
			break;

		
		}
		
	}
	public void show(float p,View parent) {
		fistFalg=true;
		seekBar.setProgress((int) (p*1000));
		DecimalFormat df = new DecimalFormat("#0.0");
        String strPercent = df.format(p * 100) + "%";
		mTextView.setText(strPercent);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		
	}
}

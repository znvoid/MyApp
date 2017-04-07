package com.znvoid.demo1.menu;

import com.znvoid.demo1.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class ReaderStyleMenu extends PopupWindow {

	public static final int STYLE_DEFOULT = R.drawable.reader__themes__paper;
	public static final int STYLE_WHITE = R.drawable.reader__themes__white;
	public static final int STYLE_GREED = R.drawable.reader__themes__green;
	public static final int STYLE_YELLOW = R.drawable.reader__themes__yellow;
	public static final int STYLE_DARK = R.drawable.reader__themes__dark;

	private int mWindow_With;
	private int mWindow_Heigh;
	private int mSelectedposition;
	private View SelectedTag;
	private onReaderStyleChangeListener mListener;
	private View slid1;
	private View slid2;
	private View slid3;
	private View slid4;
	private View slid5;

	public ReaderStyleMenu(Context context) {

		init(context);
	}

	public void setReaderStyleChangeListener(onReaderStyleChangeListener listener) {
		mListener = listener;
	}

	private void init(Context context) {
		WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		m.getDefaultDisplay().getMetrics(metrics);

		mWindow_With = metrics.widthPixels;
		mWindow_Heigh = metrics.heightPixels;

		int rootwith = mWindow_With;
		int rootheigh = mWindow_Heigh / 7;

		LinearLayout layout = (LinearLayout) LinearLayout.inflate(context, R.layout.reader_control_style, null);

		this.setWidth(rootwith);
		this.setHeight(rootheigh);
		this.setFocusable(false);
		this.setOutsideTouchable(false);
		this.setContentView(layout);
		ColorDrawable dw = new ColorDrawable(Color.parseColor("#88000000"));

		RelativeLayout s_layout1 = (RelativeLayout) layout.findViewById(R.id.txtstyle1_layout);
		RelativeLayout s_layout2 = (RelativeLayout) layout.findViewById(R.id.txtstyle2_layout);
		RelativeLayout s_layout3 = (RelativeLayout) layout.findViewById(R.id.txtstyle3_layout);
		RelativeLayout s_layout4 = (RelativeLayout) layout.findViewById(R.id.txtstyle4_layout);
		RelativeLayout s_layout5 = (RelativeLayout) layout.findViewById(R.id.txtstyle5_layout);

		ImageView view1 = (ImageView) layout.findViewById(R.id.txtstyle1);
		ImageView view2 = (ImageView) layout.findViewById(R.id.txtstyle2);
		ImageView view3 = (ImageView) layout.findViewById(R.id.txtstyle3);
		ImageView view4 = (ImageView) layout.findViewById(R.id.txtstyle4);
		ImageView view5 = (ImageView) layout.findViewById(R.id.txtstyle5);

		slid1 = layout.findViewById(R.id.txtstyle1_tag);
		slid2 = layout.findViewById(R.id.txtstyle2_tag);
		slid3 = layout.findViewById(R.id.txtstyle3_tag);
		slid4 = layout.findViewById(R.id.txtstyle4_tag);
		slid5 = layout.findViewById(R.id.txtstyle5_tag);

		view1.setBackgroundResource(STYLE_DEFOULT);
		view2.setBackgroundResource(STYLE_WHITE);
		view3.setBackgroundResource(STYLE_GREED);
		view4.setBackgroundResource(STYLE_YELLOW);
		view5.setBackgroundResource(STYLE_DARK);

		mSelectedposition = 1;
		SelectedTag = slid1;

		s_layout1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (mSelectedposition != 1) {

					mListener.onStyleChange(STYLE_DEFOULT);
					hideSlidtag(SelectedTag);
					SelectedTag = slid1;
					mSelectedposition = 1;
					showSlidTag(SelectedTag);
				}

			}
		});

		s_layout2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (mSelectedposition != 2) {
					mListener.onStyleChange(STYLE_WHITE);
					hideSlidtag(SelectedTag);
					SelectedTag = slid2;
					mSelectedposition = 2;
					showSlidTag(SelectedTag);
				}

			}
		});

		s_layout3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mSelectedposition != 3) {
					mListener.onStyleChange(STYLE_GREED);
					hideSlidtag(SelectedTag);
					SelectedTag = slid3;
					mSelectedposition = 3;
					showSlidTag(SelectedTag);
				}

			}
		});

		s_layout4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mSelectedposition != 4) {
					mListener.onStyleChange(STYLE_YELLOW);
					hideSlidtag(SelectedTag);
					SelectedTag = slid4;
					mSelectedposition = 4;
					showSlidTag(SelectedTag);
				}

			}
		});

		s_layout5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mSelectedposition != 5) {
					mListener.onStyleChange(STYLE_DARK);
					hideSlidtag(SelectedTag);
					SelectedTag = slid5;
					mSelectedposition = 5;
					showSlidTag(SelectedTag);
				}

			}
		});

		this.setBackgroundDrawable(dw);

	}

	private void hideSlidtag(View mSelectedTag) {
		mSelectedTag.setVisibility(View.INVISIBLE);
	}

	private void showSlidTag(View mSelectedTag) {
		mSelectedTag.setVisibility(View.VISIBLE);

	}

	public interface onReaderStyleChangeListener {
		public void onStyleChange(int stylecolor);

	}

	public void show(int sty, View v) {
		switch (sty) {
		case STYLE_DEFOULT:
			hideSlidtag(SelectedTag);
			SelectedTag = slid1;
			mSelectedposition = 1;
			showSlidTag(SelectedTag);
			break;
		case STYLE_WHITE:
			hideSlidtag(SelectedTag);
			SelectedTag = slid2;
			mSelectedposition = 2;
			showSlidTag(SelectedTag);
			break;
		case STYLE_GREED:
			hideSlidtag(SelectedTag);
			SelectedTag = slid3;
			mSelectedposition = 3;
			showSlidTag(SelectedTag);
			break;
		case STYLE_YELLOW:
			hideSlidtag(SelectedTag);
			SelectedTag = slid4;
			mSelectedposition = 4;
			showSlidTag(SelectedTag);
			break;
		case STYLE_DARK:
			hideSlidtag(SelectedTag);
			SelectedTag = slid5;
			mSelectedposition = 5;
			showSlidTag(SelectedTag);
			break;

		}
		showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}

}

package com.znvoid.demo1.popup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.znvoid.demo1.R;
import com.znvoid.demo1.view.MatrixImageView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

public class ShowImagePopup extends PopupWindow {
	private MatrixImageView imageView;
	private Activity context;
	private DisplayImageOptions options;
	public ShowImagePopup(Activity context) {
		super(context);
		this.context = context;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen","android");
	    int height = context.getResources().getDimensionPixelSize(resourceId);
	    
	    setWidth(metrics.widthPixels);
		setHeight(metrics.heightPixels);
		View rootView = LayoutInflater.from(context).inflate(R.layout.showimage, null);

		init(rootView);

		options= new DisplayImageOptions.Builder()   
	                .cacheInMemory(true)  
	                .cacheOnDisk(true)  
	                .bitmapConfig(Bitmap.Config.RGB_565)  
	                .build();

		
	}

	public void init(View rootView) {

		ImageLoaderConfiguration configuration = ImageLoaderConfiguration
				.createDefault(context);

		
		 imageView = (MatrixImageView) rootView.findViewById(R.id.showimage_image);
		
		rootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						dismiss();

					}
				}, 300);

			}
		});

		
		setContentView(rootView);

		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable());

	}

	public void Show(String path) {
		if (isShowing()) {
			dismiss();
		}
		if (path!=null) {
			
			ImageLoader.getInstance().displayImage("file://"+path, imageView,options);
			showAtLocation(context.getWindow().getDecorView(), Gravity.TOP, 0,0);
		
			
		}
		
	}

	@Override
	public void dismiss() {
		imageView.setImageResource(R.drawable.default_error);
		super.dismiss();
	}
}

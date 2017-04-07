package com.znvoid.demo1.popup;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.znvoid.demo1.R;
import com.znvoid.demo1.adapt.SelectorPopupAdapt;
import com.znvoid.demo1.adapt.SelectorPopupAdapt.CheckBoxClickListener;
import com.znvoid.demo1.daim.ImageBean;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;

public class SelectorPopup extends PopupWindow implements  OnClickListener, CheckBoxClickListener {
	private Activity context;
	private GridView mGridView;
	private CallbackListener listener;
	private Button mButton;
	private SelectorPopupAdapt adapt;
	
	private List<String> selectList=new ArrayList<String>();
	public SelectorPopup(Activity context) {
		super(context);
		this.context=context;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen","android");
	    int height = context.getResources().getDimensionPixelSize(resourceId);
	    
	    setWidth(metrics.widthPixels);
		setHeight(metrics.heightPixels-height);
		
		View rootView = LayoutInflater.from(context).inflate(R.layout.photoselector, null);
		init(rootView);
	}

	private void init(View rootView) {
		
		mGridView=(GridView) rootView.findViewById(R.id.photoseletor_gv);
		mButton=(Button) rootView.findViewById(R.id.photoseletor_bt);
		
		adapt=new SelectorPopupAdapt(context,mGridView);
		adapt.setcheckBoxClickListener(this);
		mGridView.setAdapter(adapt);
		mButton.setOnClickListener(this);
		setContentView(rootView);
		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable());
		  
		mGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));  

	}

	
	public void show(List<ImageBean> list) {
		mButton.setEnabled(false);
		selectList.clear();
		adapt.setdata(list);
		showAtLocation(context.getWindow().getDecorView(), Gravity.TOP, 0,0);
	}
	
	
	
	
	public void  setCallbackListener(CallbackListener listener) {
		this.listener=listener;
	}
	
	
	
	public interface CallbackListener{
		
		
		void onComplete(List<String> list);
		
		
	}





	@Override
	public void onClick(View v) {
		if (listener!=null) {
			listener.onComplete(selectList);
		}
		
		dismiss();
	}
	
	private void savePaht(String path) {
		
		if(selectList.contains(path)){
			selectList.remove(path);
			
		}else {
			selectList.add(path);
		}
		
		if (selectList.size()==0) {
			mButton.setEnabled(false);
		}else {
			mButton.setEnabled(true);
		}
		
		
	}

	@Override
	public void checkBoxOnClick(String path) {
		savePaht( path);
		
	}
}

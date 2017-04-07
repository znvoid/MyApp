package com.znvoid.demo1.adapt;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.znvoid.demo1.R;
import com.znvoid.demo1.daim.ImageBean;
import com.znvoid.demo1.popup.ShowImagePopup;

import java.util.ArrayList;
import java.util.List;

public class SelectorPopupAdapt extends BaseAdapter implements OnCheckedChangeListener, OnClickListener {

	private List<ImageBean> list=new ArrayList<ImageBean>();
	
	private GridView mGridView;
	protected LayoutInflater mInflater;
	private ShowImagePopup showImagePopup;
	
	private DisplayImageOptions options;
	private CheckBoxClickListener listener;

	public SelectorPopupAdapt(Activity context, GridView gridView) {
		this.mGridView=gridView;
		mInflater = LayoutInflater.from(context);
		showImagePopup=new ShowImagePopup(context);
		options= new DisplayImageOptions.Builder()   
                .cacheInMemory(true)  
                .cacheOnDisk(true)  
                .bitmapConfig(Bitmap.Config.RGB_565)  
                .build();

	}

	
public void setdata(List<ImageBean> list) {
	this.list.clear();
	this.list.addAll(list);
	notifyDataSetChanged();
}
	
	public void setcheckBoxClickListener(CheckBoxClickListener listener ) {
		this.listener=listener;
	}
	public interface CheckBoxClickListener{
		
		void checkBoxOnClick(String path);
		
		
	}
	@Override
	public int getCount() {
		
		return list.size();
	}

	@Override
	public ImageBean getItem(int position) {
		
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final ViewHolder viewHolder;
		ImageBean mImageBean = list.get(position);
		String path = mImageBean.getPath();

		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.select_item, null);
			viewHolder.mImageView=(ImageView) convertView.findViewById(R.id.select_item_image);
			viewHolder. mCheckBox= (CheckBox) convertView.findViewById(R.id.select_item_checkbox);
			
			viewHolder.maskView=convertView.findViewById(R.id.select_item_mask);
			
		
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			
		}
		viewHolder. mCheckBox.setChecked(mImageBean.isChoosed());
		viewHolder. mCheckBox.setTag(position);
		viewHolder.mImageView.setTag(path);
		viewHolder.maskView.setTag(convertView);
//		if (position==0) {
//			viewHolder.mImageView.setImageResource(R.drawable.default_error);;
//		} else {
		ImageLoader.getInstance().displayImage("file://"+path, viewHolder.mImageView,options);	
//		}
		
//		 viewHolder.mImageView.setImageBitmap(BitmapFactory.decodeFile(path));
		viewHolder.update();
		viewHolder.mCheckBox.setOnCheckedChangeListener(this);
		viewHolder.mImageView.setOnClickListener(this);
		
		return convertView;
	}
	
	public class ViewHolder{
		public ImageView mImageView;
		public CheckBox mCheckBox;
		public View maskView;
		public void update() {
			maskView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				
				@Override
				public void onGlobalLayout() {
					View v = (View) maskView.getTag();
					int width = v.getWidth();
					v.setLayoutParams(new GridView.LayoutParams(
							GridView.LayoutParams.FILL_PARENT,
							width));
					

					
				}
			});
			
			
			
		}

		
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int position=(Integer) buttonView.getTag();
		if (list.get(position).isChoosed()!=isChecked) {
			listener.checkBoxOnClick(list.get(position).getPath());
			list.get(position).setChoosed(isChecked);
		}
		
	}


	@Override
	public void onClick(View v) {
		String path=(String) v.getTag();
		Log.e("Light", path);
		showImagePopup.Show(path);
	}
}

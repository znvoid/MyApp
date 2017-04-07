package com.znvoid.demo1.adapt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class MyAdapt<T> extends BaseAdapter {

	private Context context;
	// 资源布局xml文件
	private int layoutid;
	private List<T> datalist = new ArrayList<T>();

	public MyAdapt(Context context, int resId) {
		super();
		this.context = context;
		layoutid = resId;
	}
//增加条目
	public void add(T item) {
		datalist.add(item);
		notifyDataSetChanged();
	}
	//删除条目
public void remove(int position) {
	datalist.remove(position);
	notifyDataSetChanged();
}
//设置数据
public void setdata(List<T> list) {
	datalist.clear();
	datalist.addAll(list);
	notifyDataSetChanged();
}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datalist.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return datalist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView==null) {
			convertView=LayoutInflater.from(context).inflate(layoutid, null);
		}
		initlistcell(position, convertView,  parent);
		return convertView;
	}
	/*
	 * 初始化listview条目
	 */
protected abstract void initlistcell(int position, View listcellview, ViewGroup parent);

}

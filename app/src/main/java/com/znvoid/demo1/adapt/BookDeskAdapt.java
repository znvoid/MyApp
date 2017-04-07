package com.znvoid.demo1.adapt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.znvoid.demo1.R;
import com.znvoid.demo1.daim.BookImf;

import java.util.ArrayList;
import java.util.List;

public class BookDeskAdapt extends BaseAdapter {
	private List<BookImf> datalist = new ArrayList<BookImf>();
	private Context context;
	
	//增加条目
		public void add(BookImf item) {
			datalist.add(item);
			notifyDataSetChanged();
		}
		//删除条目
	public void remove(int position) {
		datalist.remove(position);
		notifyDataSetChanged();
	}
	//设置数据
	public void setdata(List<BookImf> list) {
		datalist.clear();
		datalist.addAll(list);
		notifyDataSetChanged();
	}
	public BookDeskAdapt(Context context) {
		super();
		this.context = context;
	}

	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datalist.size()+1;
	}

	@Override
	public Object getItem(int position) {
		if (position>datalist.size()-1) {
			return new BookImf("deful");
		}
		return datalist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.book_item, null);
		}
		TextView textView=(TextView) convertView.findViewById(R.id.book_tv);
		TextView tv_path=(TextView) convertView.findViewById(R.id.book_tv_path);
		
		if (position>datalist.size()-1) {
			textView.setText("");
			textView.setBackgroundResource(R.drawable.book_add);
			tv_path.setText("");
		}else {
			textView.setText(datalist.get(position).getName());
			tv_path.setText(datalist.get(position).getPath());
			if ("deful".equals(datalist.get(position).getIc_path())) {
				textView.setBackgroundResource(R.drawable.book_default_cover);
			}else {//待处理
				textView.setBackgroundResource(R.drawable.book_default_cover);
			}
			
			
			
		}
		
		return convertView;
	}

}

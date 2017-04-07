package com.znvoid.demo1.adapt;


import java.util.List;


import com.znvoid.demo1.R;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiFragmListviewAdapt extends BaseAdapter {
	private Context context;
	private List<ScanResult> mWifiList; 
	
	
	public WifiFragmListviewAdapt(Context context, List<ScanResult> mWifiList) {
		super();
		this.context = context;
		this.mWifiList = mWifiList;
	}

	public WifiFragmListviewAdapt(Context context) {
		super();
		this.context = context;
	}

	public List<ScanResult> getmWifiList() {
		return mWifiList;
	}

	public void setmWifiList(List<ScanResult> mWifiList) {
		if (this.mWifiList==null||this.mWifiList.isEmpty()) {
			this.mWifiList = mWifiList;
		}else {
			this.mWifiList.clear();
			this.mWifiList.addAll(mWifiList);
			
		}
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mWifiList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mWifiList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View  view=inflater.inflate(R.layout.wifilistview,null);
		TextView textView1=(TextView) view.findViewById(R.id.wifi_tv1);
		TextView textView2=(TextView) view.findViewById(R.id.wifi_tv2);
		ImageView imageView=(ImageView) view.findViewById(R.id.imageView_wifi);
		
		imageView.setImageLevel(mWifiList.get(position).level+100);
		textView1.setText(mWifiList.get(position).SSID);
		textView2.setText(mWifiList.get(position).BSSID);
		return view;
	}

}

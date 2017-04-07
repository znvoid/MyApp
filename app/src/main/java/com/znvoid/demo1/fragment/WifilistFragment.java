package com.znvoid.demo1.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.znvoid.demo1.R;
import com.znvoid.demo1.adapt.WifiFragmListviewAdapt;
import com.znvoid.demo1.util.WifiUtil;

import java.util.List;

public class WifilistFragment extends Fragment {
	private TextView tv_ownip;
	private ListView lv;
	private Context context;
	private ImageView mImageView_refresh;
	private Animation animation;
	private WifiUtil wifiUtil;
	private WifiFragmListviewAdapt adapt;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.wifilistfragment, null);
		context=getActivity();
		lv=(ListView) view.findViewById(R.id.wifi_listView);
		tv_ownip=(TextView) view.findViewById(R.id.tv_own_wife);
		mImageView_refresh= (ImageView) view.findViewById(R.id.imageView_refresh);
		wifiUtil=new WifiUtil(context);
		//adapt=new WifiFragmListviewAdapt(context,refreshdata());
	
		adapt=new WifiFragmListviewAdapt(context);
		adapt.setmWifiList(refreshdata());
		lv.setAdapter(adapt);
		
		tv_ownip.setText("本机ip："+wifiUtil.getIP()+"\nmac地址:"+wifiUtil.getMacAddress());
		mImageView_refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				refreshanimation();
				adapt.setmWifiList(refreshdata());
				adapt.notifyDataSetChanged();
			}
		});
		
		return view;
	}
	public void refreshanimation(){
		animation=AnimationUtils.loadAnimation(context, R.anim.wifirefreshrotate);  
//		LinearInterpolator lin = new LinearInterpolator();
//		animation.setInterpolator(lin);
		mImageView_refresh.startAnimation(animation);
		
		
	}
	//刷新网络获得wifi信息
	public List<ScanResult> refreshdata(){
		
		
		wifiUtil.startScan();
		return wifiUtil.getWifiList();
	}
	
	
}

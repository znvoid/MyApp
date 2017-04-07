package com.znvoid.demo1.fragment;



import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.znvoid.demo1.adapt.MyAdapt;
import com.znvoid.demo1.daim.ClientScanResultSO;
import com.znvoid.demo1.net.Ping;
import com.znvoid.demo1.net.SearchThread;
import com.znvoid.demo1.util.Utils;
import com.znvoid.demo1.util.WifiUtil;
import com.znvoid.demo1.view.CircleImageView;

public class NetFragment extends Fragment {
	private final int MSG_OVER = 0x10001;
	private final int MSG_STOP = 0x10002;
	private final int MSG_TIMEOUT = 0x10003;
	private boolean flagrefreshdate=true;
	private TextView tv_ownip;
	private TextView tv1;
	private ListView lv;
	private Context context;
	private ImageView mImageView_refresh;
	private Animation animation;
	private WifiUtil wifiUtil;
	private MyAdapt<ClientScanResultSO> adapt;
	private Handler handle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			
			case MSG_STOP:
				mImageView_refresh.clearAnimation();
				break;
			case MSG_TIMEOUT:
//				new SearchThread(handle).start();
				adapt.setdata(Utils.getClientList());
				mImageView_refresh.clearAnimation();
				break;
			case SearchThread.SEARCH_TEST:
//				flagrefreshdate=!flagrefreshdate;
//				List<ClientScanResultSO> temp=(List<ClientScanResultSO>) msg.obj;
//				if (temp.size()==0) {
//					adapt.setdata(Utils.getClientList());
//					mImageView_refresh.clearAnimation();
					
//					break;
//				}
//				adapt.setdata(Utils.checkClient((temp)));
//				mImageView_refresh.clearAnimation();
				break;
			default:
				break;
			}


		};

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.wifilistfragment, null);
		context = getActivity();
		lv = (ListView) view.findViewById(R.id.wifi_listView);
		tv_ownip = (TextView) view.findViewById(R.id.tv_own_wife);
		tv1 = (TextView) view.findViewById(R.id.textView1);
		mImageView_refresh = (ImageView) view.findViewById(R.id.imageView_refresh);
		wifiUtil = new WifiUtil(context);

		tv1.setText("局域网设备");
		adapt = new MyAdapt<ClientScanResultSO>(context, R.layout.netlistview) {

			@Override
			protected void initlistcell(int position, View listcellview, ViewGroup parent) {
				// TODO Auto-generated method stub
				ClientScanResultSO client=getItem(position);
				TextView tv_ip = (TextView) listcellview.findViewById(R.id.tv_ip);
				TextView tv_mac = (TextView) listcellview.findViewById(R.id.tv_mac);
				TextView tv_author = (TextView) listcellview.findViewById(R.id.tv_clientname);
				CircleImageView cim_client=(CircleImageView) listcellview.findViewById(R.id.cim_clinthead);
				
				
				tv_ip.setText(client.getIp());
				tv_mac.setText(client.getHwAddress());
				tv_author.setText(client.getHostname());
				cim_client.setImageBitmap(Utils.getRes(context, client.getHeadName()));
			}
		};
		refreshanimation();
		refreshdata();
		lv.setAdapter(adapt);

		tv_ownip.setText("本机ip：" + wifiUtil.getIP() + "\nmac地址:" + wifiUtil.getMacAddress());
		mImageView_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (flagrefreshdate) {
					handle.sendEmptyMessage(MSG_STOP);
				}else {
					refreshanimation();
					refreshdata();
				}
				flagrefreshdate=!flagrefreshdate;
				

			}
		});

		return view;
	}

	public void refreshanimation() {
		animation = AnimationUtils.loadAnimation(context, R.anim.netdevicerefresh);
		animation.setRepeatCount(-1);
//		LinearInterpolator lin = new LinearInterpolator();
//		animation.setInterpolator(lin);
		mImageView_refresh.startAnimation(animation);

	}

	public void refreshdata() {
		new Thread() {
			public void run() {

				Ping ping = new Ping();
				ping.pingAll(wifiUtil.getIP());

				
			};
		}.start();
		new Thread() {
			public void run() {

				try {
					
					Thread.sleep(3000);
					
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}

				handle.sendEmptyMessage(MSG_TIMEOUT);
				
			};
		}.start();
		

	}

}
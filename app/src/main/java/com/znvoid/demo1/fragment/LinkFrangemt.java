package com.znvoid.demo1.fragment;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.znvoid.demo1.R;
import com.znvoid.demo1.adapt.MyAdapt;
import com.znvoid.demo1.daim.Contact;
import com.znvoid.demo1.net.Ping;
import com.znvoid.demo1.net.SearchThread;
import com.znvoid.demo1.util.TCPData;
import com.znvoid.demo1.util.Utils;
import com.znvoid.demo1.util.WifiUtil;

import java.util.ArrayList;
import java.util.List;

public class LinkFrangemt extends Fragment implements OnRefreshListener, OnItemClickListener {
	private final int MSG_OVER = 0x10001;
	private final int MSG_STOP = 0x10002;
	private final int MSG_TIMEOUT = 0x10003;

	
	private ListView lv;
	private Context context;
	private List<Contact>list=new ArrayList<Contact>();
	private WifiUtil wifiUtil;
	private MyAdapt<Contact> adapt;
	private SwipeRefreshLayout mSwipRefresh;


	
	private Handler handle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			
			case MSG_STOP:
				Toast.makeText(getContext(),"网络异常",Toast.LENGTH_LONG).show();
				mSwipRefresh.setRefreshing(false);
				break;
			case MSG_TIMEOUT:
				new SearchThread( TCPData.creatTestMessage(context),handle).start();
				
				break;
			case SearchThread.SEARCH_FINSH:
				isfresh=false;
				List<Contact>list=(List<Contact>) msg.obj;
				mSwipRefresh.setRefreshing(false);
				
				adapt.setdata(list);
				
				
				break;
			default:
				break;
			}


		};

	};

	private boolean isfresh=false;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.link, container,false);
		context = getActivity();
		lv = (ListView) view.findViewById(R.id.link_ListView);
		
		wifiUtil = new WifiUtil(context);
		mSwipRefresh=(SwipeRefreshLayout) view.findViewById(R.id.link_refresh);
		mSwipRefresh.setOnRefreshListener(this);  
		adapt = new MyAdapt<Contact>(context, R.layout.contacts_item) {

			@Override
			protected void initlistcell(int position, View listcellview, ViewGroup parent) {
				
				final Contact client=getItem(position);
				TextView tv_id = (TextView) listcellview.findViewById(R.id.contactsItem_tv_lastMsg);
				TextView tv_name = (TextView) listcellview.findViewById(R.id.contactsItem_tv_name);
				TextView tv_ip = (TextView) listcellview.findViewById(R.id.contactsItem_tv_time);
				ImageView im_head = (ImageView) listcellview.findViewById(R.id.contactsItem_iv_head);
				
				listcellview.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						doCick(client);
					}
				});
				
				tv_ip.setText(client.getIp());
				tv_name.setText(client.getName());
				tv_id.setText(client.getId());
				im_head.setImageBitmap(Utils.getRes(context, client.getHead()));
			}
		};
	
		mSwipRefresh.post(new Runnable() {
			
			@Override
			public void run() {
				onRefresh();
				mSwipRefresh.setRefreshing(true);
				
			}
		});
		lv.setAdapter(adapt);
		lv.setOnItemClickListener(this);

		return view;
	}



	@Override
	public void onRefresh() {
		
		if (!isfresh&&wifiUtil.checkState()== WifiManager.WIFI_STATE_ENABLED) {
			isfresh=!isfresh;
			new Thread(){public void run() {
				Ping ping = new Ping();
				ping.pingAll(wifiUtil.getIP());
				
			};
			};
			
			handle.sendEmptyMessageDelayed(MSG_TIMEOUT, 2000);
		}
		if (wifiUtil.checkState()!= WifiManager.WIFI_STATE_ENABLED) {
			System.out.println("mSwipRefresh.setRefreshing(false)");
			handle.sendEmptyMessageDelayed(MSG_STOP, 1000);
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		doCick(list.get(position));
		
	}
	public void  doCick(Contact contact) {

		FragmentManager fragmentManager=getActivity().getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		ChatFragment chatFragment=(ChatFragment) fragmentManager.findFragmentByTag(ChatFragment.class.getName());
		ViewPagerFragment viewPagerFragment=(ViewPagerFragment) fragmentManager.findFragmentByTag(ViewPagerFragment.class.getName());
		if (chatFragment==null) {
			chatFragment=new ChatFragment();

		}
		chatFragment.beginToShow(contact, true);
		if (!chatFragment.isAdded()) {

			transaction.add(R.id.mian_frame, chatFragment,chatFragment.getClass().getName());
			transaction.hide(viewPagerFragment);
			transaction.commit();
		} else {

			transaction.show(chatFragment);
			transaction.hide(viewPagerFragment);
			transaction.commit();
		}
	}


}
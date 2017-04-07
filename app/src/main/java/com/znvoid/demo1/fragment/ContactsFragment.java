package com.znvoid.demo1.fragment;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.znvoid.demo1.R;
import com.znvoid.demo1.adapt.ContactsAdapter;
import com.znvoid.demo1.daim.Contact;
import com.znvoid.demo1.imf.ContactsItemTouchHelperCallback;
import com.znvoid.demo1.imf.ItemClickListener;
import com.znvoid.demo1.sql.MsgSQL;
import com.znvoid.demo1.util.Utils;
import com.znvoid.demo1.util.WifiUtil;
import com.znvoid.demo1.view.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment implements ItemClickListener {

	private ChatFragment chatFragment;
	private Context context;
	private ItemTouchHelper itemTouchHelper;
	private TextView tv1;
	private TextView tv3;
	private RecyclerView recyclerView ;
	private FloatingActionButton fab;
	private ContactsAdapter adapter;
	private List<Contact> list=new ArrayList<Contact>();

	private MsgSQL mDB;
	private static final String MESSAGE_NOTIFICATION="com.zn.demo.CHATMESSAGE";
	 private BroadcastReceiver messageReceiver = new BroadcastReceiver()
	    {
	        @Override
	        public void onReceive(Context context, Intent intent)
	        {
	            if (intent.getAction() == MESSAGE_NOTIFICATION)
	            {
	            	Bundle bundle=intent.getExtras();
	                Contact contact = (Contact) bundle.getSerializable("message");
	                if (contact!=null) {
	                	  handleResult(contact);
					}
	              

	            }

	        }

			
	    };

	@Override
	public void onCreate(Bundle savedInstanceState) {

		IntentFilter filter = new IntentFilter();
        filter.addAction(MESSAGE_NOTIFICATION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, filter);
        super.onCreate(savedInstanceState);

	}

	protected void handleResult(Contact contact) {
		boolean flag=false;
				
		for (int i = 0; i < list.size(); i++) {
			Contact iContact = list.get(i);
			
			if (iContact.getId().equals(contact.getId())) {
				list.set(i, contact);
				adapter.notifyItemChanged(i);
				flag=true;
				break;
			}
		}
		if (!flag) {
			list.add(0, contact);;
			adapter.notifyItemInserted(0);
		}
		if (chatFragment!=null&&!chatFragment.isHidden()) {
			chatFragment.handleResult(contact);
		}else {

		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		context=getActivity();

		mDB=new MsgSQL(context);
		View view = inflater.inflate(R.layout.contacts, container,false);
		
		 recyclerView = (RecyclerView)view.findViewById(R.id.contacts_recyclerview); 
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		
		list=make();
		adapter= new ContactsAdapter(list,context,this);
		recyclerView.setAdapter(adapter);
		recyclerView.addItemDecoration(new RecycleViewDivider(getContext(),LinearLayoutManager.VERTICAL,R.drawable.divider));
		//条目触摸帮助类
		ItemTouchHelper.Callback callback = new ContactsItemTouchHelperCallback(adapter);
		itemTouchHelper = new ItemTouchHelper(callback);
		itemTouchHelper.attachToRecyclerView(recyclerView);


		//chatFragment=(ChatFragment) fm.findFragmentByTag(ChatFragment.class.getName());
		
		return view;
		
		
	}
	
	private List<Contact> make() {
		List<Contact> list=mDB.loadContacts();
		if (list.size()==0) {
			list.add(new Contact(Utils.getId(context), Utils.getOtherName(context), Utils.getOtherHead(context), new WifiUtil(context).getIP()));
		}
		return list;
	}
	
	@Override
	public void itemOnClick(View view, int position) {
		//准备数据
		FragmentManager fm=getActivity().getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		chatFragment=(ChatFragment) fm.findFragmentByTag(ChatFragment.class.getName());
		ViewPagerFragment viewPagerFragment=(ViewPagerFragment) fm.findFragmentByTag(ViewPagerFragment.class.getName());
		if (chatFragment==null) {
			chatFragment=new ChatFragment();
			
		}
		chatFragment.beginToShow(list.get(position), false);
		if (!chatFragment.isAdded()) {
			System.out.println("1111111");
			transaction.add(R.id.mian_frame, chatFragment,chatFragment.getClass().getName());
			transaction.hide(viewPagerFragment);
			transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			
			transaction.commit();
		} else {
			System.out.println("2222222");
			transaction.show(chatFragment);
			transaction.hide(viewPagerFragment);
			transaction.setCustomAnimations(R.animator.enter_anim, R.animator.out_anim);
			transaction.commit();
		}
		
	}
	@Override
	public void itemOnLongClick(View view, int position) {
		// TODO Auto-generated method stub
		
	}
//	@Override
//	public void onClick(View v) {
//
//		switch (v.getId()) {
//
//			case R.id.contacts_fab:
//				ObjectAnimator animation=ObjectAnimator.ofFloat(fab, "rotation", 0,180,360);
//				animation.setDuration(500);
//				animation.start();
//
//				list.clear();
//				list.addAll(make());
//				adapter.notifyDataSetChanged();
//				break;
//		}
//
//
//	}
	
	@Override
	public void onDestroy() {
		LocalBroadcastManager.getInstance(context).unregisterReceiver(messageReceiver);
		super.onDestroy();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			list.clear();
			list.addAll(make());
			adapter.notifyDataSetChanged();
		}
		
		
	}
	public void refleshDate(){
		list.clear();
		list.addAll(make());
		adapter.notifyDataSetChanged();

	}


}

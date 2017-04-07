package com.znvoid.demo1.adapt;

import java.util.Collections;
import java.util.List;

import com.znvoid.demo1.R;
import com.znvoid.demo1.daim.Contact;
import com.znvoid.demo1.imf.ItemClickListener;
import com.znvoid.demo1.imf.ItemTouchMoveListener;
import com.znvoid.demo1.sql.MsgSQL;
import com.znvoid.demo1.util.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactsAdapter extends  Adapter<ContactsAdapter.MyViewHolder> implements ItemTouchMoveListener {
	private List<Contact> list;
	private Context context;
	private ItemClickListener listener;
	private MsgSQL sql;
	public ContactsAdapter(List<Contact> list,Context context,ItemClickListener listener) {
		this.context=context;
		this.list=list;
		this.listener=listener;
		sql=new MsgSQL(context);
	}
	
	
	class MyViewHolder extends ViewHolder implements OnClickListener ,OnLongClickListener{

		private ImageView iv_head;
		private TextView tv_name;
		private TextView tv_Msg;
		private TextView tv_time;
		
		public MyViewHolder(View itemView) {
			super(itemView);
		
			iv_head = (ImageView)itemView.findViewById(R.id.contactsItem_iv_head);
			tv_name = (TextView)itemView.findViewById(R.id.contactsItem_tv_name);
			tv_Msg = (TextView)itemView.findViewById(R.id.contactsItem_tv_lastMsg);
			tv_time = (TextView)itemView.findViewById(R.id.contactsItem_tv_time);
			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
			
		}
		@Override
		public void onClick(View v) {
			
			listener.itemOnClick(v, getAdapterPosition());
			
		}
		@Override
		public boolean onLongClick(View v) {
			listener.itemOnLongClick(v, getAdapterPosition());
			return true;
		}
		

		
		
	}

	@Override
	public boolean onItemMove(int fromPosition, int toPosition) {
		Collections.swap(list, fromPosition, toPosition);
		notifyItemMoved(fromPosition, toPosition);
		return true;
	}

	@Override
	public boolean onItemRemove(int position) {
		sql.delete(list.get(position));
		list.remove(position);
		notifyItemRemoved(position);
		
		return true;
	}

	@Override
	public int getItemCount() {
		
		return list.size();
	}

	@Override
	public void onBindViewHolder(MyViewHolder viewHolder, int position) {
		Contact contact = list.get(position);
		viewHolder.iv_head.setImageBitmap(Utils.getRes(context, contact.getHead()) );
		viewHolder.tv_name.setText(contact.getName());
		if (contact.getLastMsg().equals("NULL")) {
			viewHolder.tv_Msg.setText("");
			viewHolder.tv_time.setText("");
			
		}else {
			viewHolder.tv_Msg.setText(contact.getLastMsg());
			String[] times=contact.getTime().split("    ");
			if (times.length>1) {
				viewHolder.tv_time.setText(times[1]);
			}
			
		}
		
		
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_item, parent, false);
		
		
		return new MyViewHolder(view);
	}

	




}

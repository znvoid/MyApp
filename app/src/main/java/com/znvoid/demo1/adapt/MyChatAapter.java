package com.znvoid.demo1.adapt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.znvoid.demo1.R;
import com.znvoid.demo1.daim.Chat;
import com.znvoid.demo1.popup.ShowImagePopup;
import com.znvoid.demo1.sql.MsgSQL;
import com.znvoid.demo1.util.Utils;
import com.znvoid.demo1.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class MyChatAapter extends BaseAdapter implements OnClickListener,OnLongClickListener{
	private int delposition = -1;
	private Context context;
	private String mId;
	private List<Chat> datalist = new ArrayList<Chat>();
	private MsgSQL msgSQL;
	private ShowImagePopup showImagePopup;
	private DisplayImageOptions options;
	public MyChatAapter(Activity context,String id) {
		super();
		this.context = context;
		msgSQL=new MsgSQL(context);
		mId=id;
		showImagePopup=new ShowImagePopup(context);
		options= new DisplayImageOptions.Builder()   
                .cacheInMemory(true)  
                .cacheOnDisk(true)  
                .bitmapConfig(Bitmap.Config.RGB_565)  
                .build();
	}

	// 增加条目
	public void add(Chat item) {
		datalist.add(item);
		notifyDataSetChanged();
	}

	// 删除条目
	public void remove(int position) {

		datalist.remove(position);
		notifyDataSetChanged();
	}

	// 设置数据
	public void setdata(List<Chat> list) {
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
	public Chat getItem(int position) {
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
		Chat chat = getItem(position);
		/*
		 * if (convertView==null) { if
		 * (context.getString(R.string.author).equals(chat.getAuthor())) {
		 * convertView=LayoutInflater.from(context).inflate(R.layout.
		 * chat_message_own, null); }else {
		 * convertView=LayoutInflater.from(context).inflate(R.layout.
		 * chat_message_other, null); }
		 * 
		 * } initlistcell( position, convertView, parent);
		 * 
		 * 
		 * return convertView;
		 */

		ViewHolder holder = null;

		if (convertView == null || (holder = (ViewHolder) convertView.getTag()).flag != chat.getDirection()) {
			holder = new ViewHolder();
			if (chat.getDirection() == Chat.MESSAGE_SEND) {
				holder.flag = Chat.MESSAGE_SEND;
				convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_own, null);
			} else {
				holder.flag = Chat.MESSAGE_RECEIVE;
				convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_other, null);
			}
			holder.text = (TextView) convertView.findViewById(R.id.message);
			holder.tvauthor = (TextView) convertView.findViewById(R.id.author);
			holder.tvtime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			holder.circleImageView = (CircleImageView) convertView.findViewById(R.id.imageView_author);
			holder.imageView=(ImageView) convertView.findViewById(R.id.ishow);
			convertView.setTag(holder);
		}
		if ("flie/picture".equals(chat.getMsgType())) {
			holder.imageView.setVisibility(View.VISIBLE);
			
			holder.imageView.setTag(position);
			
		//	holder.imageView.setImageBitmap(BitmapFactory.decodeFile(chat.getMessage()));;
			ImageLoader.getInstance().displayImage("file://"+chat.getMessage(), holder.imageView,options);
			holder.text.setVisibility(View.GONE);
			holder.imageView.setOnClickListener(this);
			holder.imageView.setOnLongClickListener(this);
		}else {
			holder.imageView.setVisibility(View.GONE);
			holder.text.setVisibility(View.VISIBLE);
			holder.text.setText(chat.getMessage());
			holder.text.setTag(position);
			holder.text.setOnClickListener(this);
			holder.text.setOnLongClickListener(this);
		}
		
		if (holder.flag==0) {
			holder.circleImageView.setImageBitmap(getRes(Utils.getHead(context)));
			holder.tvauthor.setText(Utils.getName(context));
		}else {
			holder.tvauthor.setText(chat.getAuthor() );
			holder.circleImageView.setImageBitmap(getRes(chat.getHead()));
		}
		
		holder.tvtime.setText(chat.getTime());
		

		return convertView;
	}

	// 优化listview的Adapter
	static class ViewHolder {
		
		TextView text;
		TextView tvauthor;
		TextView tvtime;
		CircleImageView circleImageView;
		ImageView imageView;
		int flag;
	}

	public Bitmap getRes(String name) {
		ApplicationInfo appInfo = context.getApplicationInfo();
		int resID = context.getResources().getIdentifier(name, "drawable", appInfo.packageName);
		return BitmapFactory.decodeResource(context.getResources(), resID);
	}

	protected void dialog() {

		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("确认删除吗？");

		builder.setTitle("提示");

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				msgSQL.delete(mId, getItem(delposition));
				remove(delposition);

				dialog.dismiss();

			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				delposition = -1;
				dialog.dismiss();
			}
		});

		builder.create().show();
	}
	public void setMId(String mId) {
		this.mId=mId;
		
	}

	@Override
	public boolean onLongClick(View v) {
		delposition = (Integer) v.getTag();

		dialog();
		return true;
	}

	@Override
	public void onClick(View v) {
		int i=(Integer) v.getTag();
		String msgType=datalist.get(i).getMsgType();
		
		if ("flie/picture".equals(msgType)) {
			showImagePopup.Show(datalist.get(i).getMessage());
		}
		
		
	}
}

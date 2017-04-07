package com.znvoid.studylibrary.slidingcard;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.znvoid.studylibrary.R;

public class SampleAdapter extends  Adapter<SampleAdapter.MyViewHolder>  {

	private final String[] ITEM={
			"刘德华" ,"梁朝伟" ,"郑秀文"  ,"梁咏琪" ,"李纹" , "莫文蔚", "林嘉欣", "周丽淇" ,"周杰伦", "蔡依林", "潘伟柏" , "房祖名"};

	private LayoutInflater mInflater;
	public SampleAdapter(RecyclerView recyclerView) {
		mInflater=LayoutInflater.from(recyclerView.getContext());
		recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
				final int position=parent.getChildViewHolder(view).getAdapterPosition();
				final int offset=parent.getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);
				outRect.set(offset,position==0?offset:0,offset,offset);


			}
		});

	}


	class MyViewHolder extends ViewHolder {

		private TextView tv;

		public MyViewHolder(View itemView) {
			super(itemView);

			tv = (TextView)itemView.findViewById(R.id.list_item_textview);
			tv.setClickable(true);
		}

	}



	@Override
	public int getItemCount() {
		
		return ITEM.length;
	}

	@Override
	public void onBindViewHolder(MyViewHolder viewHolder, int position) {
		String  s = ITEM[position];
		viewHolder.tv.setText(s);
		
		
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		View view = mInflater.inflate(R.layout.list_item, parent, false);

		return new MyViewHolder(view);
	}

	




}

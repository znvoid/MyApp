package com.znvoid.demo1.imf;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;

public class ContactsItemTouchHelperCallback extends Callback {
	private ItemTouchMoveListener moveListener;

	public ContactsItemTouchHelperCallback(ItemTouchMoveListener moveListener) {
		this.moveListener = moveListener;
	}

	//Callback回调监听时先调用的，用来判断当前是什么动作，比如判断方向（意思就是我要监听哪个方向的拖动）
	@Override
	public int getMovementFlags(RecyclerView recyclerView, ViewHolder holder) {

		//我要监听的拖拽方向是哪两个方向。
		int dragFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
		//我要监听的swipe侧滑方向是哪个方向
//		int swipeFlags = 0;
		int swipeFlags = ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
		
		
		int flags = makeMovementFlags(dragFlags, swipeFlags);
		return flags;
	}
	
	@Override
	public boolean isLongPressDragEnabled() {
		// 是否允许长按拖拽效果
		return true;
	}

	//当移动的时候回调的方法--拖拽
	@Override
	public boolean onMove(RecyclerView recyclerView, ViewHolder srcHolder, ViewHolder targetHolder) {
		if(srcHolder.getItemViewType()!=targetHolder.getItemViewType()){
			return false;
		}
		// 在拖拽的过程当中不断地调用adapter.notifyItemMoved(from,to);
		boolean result = moveListener.onItemMove(srcHolder.getAdapterPosition(), targetHolder.getAdapterPosition());
		return result;
	}

	//侧滑的时候回调的
	@Override
	public void onSwiped(ViewHolder holder, int arg1) {
		// 监听侧滑，1.删除数据；2.调用adapter.notifyItemRemove(position)
		moveListener.onItemRemove(holder.getAdapterPosition());
	}
	
	
	@Override
	public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
		//判断选中状态
		if(actionState!=ItemTouchHelper.ACTION_STATE_IDLE){
			viewHolder.itemView.setBackgroundColor(Color.parseColor("#F06292"));
		}
		super.onSelectedChanged(viewHolder, actionState);
	}
	
	@Override
	public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
		viewHolder.itemView.setBackgroundColor(Color.WHITE);
		super.clearView(recyclerView, viewHolder);
	}
	
	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView,
			ViewHolder viewHolder, float dX, float dY, int actionState,
			boolean isCurrentlyActive) {

		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
				isCurrentlyActive);
	}
	

}

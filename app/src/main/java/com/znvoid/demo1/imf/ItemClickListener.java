package com.znvoid.demo1.imf;

import android.view.View;

public interface ItemClickListener {
	/**
	 * 条目点击事件
	 * @param view
	 * @param position
	 */
	  void itemOnClick(View view , int position);
	  
	  /**
		 * 条目长点击事件
		 * @param view
		 * @param position
		 */
		  void itemOnLongClick(View view , int position);
	  
}

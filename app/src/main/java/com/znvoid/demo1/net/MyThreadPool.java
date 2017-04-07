package com.znvoid.demo1.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyThreadPool {

	private static MyThreadPool myThreadPool=new MyThreadPool();
	private ExecutorService mThreadPoolExecutor;
	protected MyThreadPool(){
		mThreadPoolExecutor= Executors.newCachedThreadPool();
		
	}
	
	public static MyThreadPool getInstance() {
		return myThreadPool;
	}
	
	public void submit(Runnable runnable) {
		mThreadPoolExecutor.submit(runnable);
	}
}

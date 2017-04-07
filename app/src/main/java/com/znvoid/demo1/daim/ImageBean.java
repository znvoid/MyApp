package com.znvoid.demo1.daim;

public class ImageBean {
	private String path;
	private boolean isChoosed;
	public String getPath() {
		return path;
	}
	
	public ImageBean(String path) {
		this(path, false);
		
	}

	public ImageBean(String path, boolean isChoosed) {
		super();
		this.path = path;
		this.isChoosed = isChoosed;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isChoosed() {
		return isChoosed;
	}
	public void setChoosed(boolean isChoosed) {
		this.isChoosed = isChoosed;
	}
	
	
}

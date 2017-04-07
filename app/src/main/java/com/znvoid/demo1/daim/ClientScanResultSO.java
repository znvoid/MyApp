package com.znvoid.demo1.daim;

public class ClientScanResultSO {
	
	private String ip;
	
	private String hwAddress;
	private String headName;
	
	private boolean isReachable;
	private String hostname;
	public ClientScanResultSO(String ip, String hwAddress, String headName, boolean isReachable, String hostname) {
		super();
		this.ip = ip;
		this.hwAddress = hwAddress;
		this.headName = headName;
		this.isReachable = isReachable;
		this.hostname = hostname;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getHwAddress() {
		return hwAddress;
	}
	public void setHwAddress(String hwAddress) {
		this.hwAddress = hwAddress;
	}
	
	public boolean isReachable() {
		return isReachable;
	}
	public void setReachable(boolean isReachable) {
		this.isReachable = isReachable;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getHeadName() {
		return headName;
	}
	public void setHeadName(String headName) {
		this.headName = headName;
	}
	
	
	
	

	
	
}

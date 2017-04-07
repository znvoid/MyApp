package com.znvoid.demo1.daim;

import java.util.HashMap;
import java.util.Map;

public class RequestHead {

	private String verson="CHATMESSAGE/V1.0";
	
	private Map<String,String> headParams=new HashMap<String, String>();

	public String getVerson() {
		return verson;
	}

	public void setVerson(String verson) {
		this.verson = verson;
	}

	public void setheadParam(String key,String value) {
		headParams.put(key, value);
	}
	
	public String getheadParam(String key) {

			return	headParams.get(key);
	}

	public Map<String, String> getHeadParams() {
		return headParams;
	}
	
}

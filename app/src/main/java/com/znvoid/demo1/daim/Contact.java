package com.znvoid.demo1.daim;

import java.io.Serializable;

public class Contact implements Serializable {

	private String id;

	private String name;

	private String head;

	private String lastMsg;

	private String time;

	private String ip;

	private String msgType;
/**
 * 1 为左边 接收到的，0 为右边 发送的
 */
	private int direction;

	public Contact(String id, String name, String head, String ip) {
		
		this(id, name, head, "NULL", "NULL", ip);
	}

	public Contact(String id, String name, String head, String lastMsg, String time, String ip, String msgType,
			int direction) {
		super();
		this.id = id;
		this.name = name;
		this.head = head;
		this.lastMsg = lastMsg;
		this.time = time;
		this.ip = ip;
		this.msgType = msgType;
		this.direction = direction;
	}

	public Contact(String id, String name, String head, String lastMsg, String time, String ip) {
		
		this(id, name, head, lastMsg, time, ip, "message/string", 0);
		
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(String lastMsg) {
		this.lastMsg = lastMsg;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

}

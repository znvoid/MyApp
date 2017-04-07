package com.znvoid.demo1.daim;

//聊天内容
public class Chat {
	public static final int MESSAGE_RECEIVE = 1;
	 public static final int MESSAGE_SEND = 0;
    
    private String author;
    private String message;
    private int direction;
   
    private String head;
    private String ip;
    private String time;
   private String msgType="message/string";

	public String getMsgType() {
	return msgType;
}

public void setMsgType(String msgType) {
	this.msgType = msgType;
}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Chat(String author, String message, int direction, String head, String ip,String time) {
		super();
		this.author = author;
		this.message = message;
		this.direction = direction;
		this.head = head;
		this.ip = ip;
		this.time = time;
	}
	public Chat(String author, String message, int direction, String head, String ip,String time,String msgType) {
		super();
		this.author = author;
		this.message = message;
		this.direction = direction;
		this.head = head;
		this.ip = ip;
		this.time = time;
		this.msgType = msgType;
	}
	public Chat(String author, String message, int direction) {
		super();
		this.author = author;
		this.message = message;
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public Chat(String author, String message) {
        this.message = message;
        this.author = author;
    }

    public void setAuthor(String author) {
		this.author = author;
	}

	public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }
}

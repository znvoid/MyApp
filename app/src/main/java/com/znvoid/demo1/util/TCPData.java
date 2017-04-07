package com.znvoid.demo1.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.znvoid.demo1.daim.Chat;
import com.znvoid.demo1.daim.Contact;
import com.znvoid.demo1.daim.RequestHead;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/*
 * 根据TCP得到的msg进行分析
 */
public class TCPData {
	public final static String DIV = "<== DIV ==>";
	public final static String CHATMESSAGE = "CHATMESSAGE V1.0";
	private static ByteBuffer byteBuffer=ByteBuffer.allocate(4096);

	public static String getMsg(Chat chat) {
		String result = CHATMESSAGE;

		result = result + DIV + chat.getAuthor() + DIV + chat.getMessage() + DIV + chat.getDirection() + DIV
				+ chat.getHead() + DIV + chat.getIp() + DIV + chat.getTime();

		return result;
	}

	/**
	 * Sting转Chat,String 为接收到的信息
	 * 废弃
	 * @param msg
	 * @return
	 */
	public static Chat getChat(String msg) {
		String[] strings = msg.split(DIV);
		Chat chat = null;

		if (strings.length == 7 && strings[0].equals(CHATMESSAGE)) {

			chat = new Chat(strings[1], strings[2], 1, strings[4], strings[5], strings[6]);

		}

		return chat;
	}

	/**
	 * 为测试连接生成数据
	 * 
	 * @param context
	 * @return
	 */
	public static String creatTestMessage(Context context) {

		SharedPreferences sp = context.getSharedPreferences("configs", context.MODE_PRIVATE);
		WifiUtil wifiUtil = new WifiUtil(context);
		String id = sp.getString("ID", Utils.getId(context));
		String name = sp.getString("author", Utils.getId(context));
		String head = sp.getString("head", "head_1");
		String ip = wifiUtil.getIP();

		Contact contact = new Contact(id, name, head, ip);
		RequestHead requestHead = new RequestHead();
		requestHead.setheadParam("Content-Type", "message/test");
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("head", requestHead2Json(requestHead));
			jsonObject.put("info", contact2Json(contact));

		} catch (JSONException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();

		}

		return jsonObject.toString();
	}

	/**
	 * jsonString 转成 Contact 对象
	 * @param jsonString
	 * @return
	 */
	public static Contact parseJsonConact(String jsonString) {
		Contact contact = null;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);

			// info= (JSONObject) jsonObject.get("info");
			Object info = jsonObject.get("info");
			if (info instanceof Contact) {

				return (Contact) info;
			}
			// info=(JSONObject) info;
			String id = ((JSONObject) info).optString("id");
			String name = ((JSONObject) info).optString("name");
			String head = ((JSONObject) info).optString("head");
			String ip = ((JSONObject) info).optString("ip");
			String massage = ((JSONObject) info).optString("massage");
			String tiem = ((JSONObject) info).optString("time");
			String type = ((JSONObject) info).optString("type");
			int direction = ((JSONObject) info).optInt("direction");

			contact = new Contact(id, name, head, massage, tiem, ip, type, direction);

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return contact;
	}
	//jsonString 转成 RequestHead 对象
	public static RequestHead parseJsonHead(String jsonString) {
		RequestHead requestHead = null;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);

			JSONObject head = (JSONObject) jsonObject.get("head");
			requestHead = new RequestHead();

			requestHead.setVerson(head.optString("ConnVerson"));
			requestHead.setheadParam("Content-Type", head.optString("Content-Type"));

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return requestHead;
	}
//RequestHead 到JSONObject
	public static JSONObject requestHead2Json(RequestHead requestHead) throws JSONException {

		JSONObject head = new JSONObject();
		head.put("ConnVerson", requestHead.getVerson());
		head.put("Content-Type", requestHead.getheadParam("Content-Type"));

		return head;
	}
//Contact 到JSONObject
	public static JSONObject contact2Json(Contact contact) throws JSONException {

		JSONObject jsonContact = new JSONObject();
		jsonContact.put("id", contact.getId());
		jsonContact.put("name", contact.getName());
		jsonContact.put("head", contact.getHead());
		jsonContact.put("ip", contact.getIp());
		jsonContact.put("massage", contact.getLastMsg());
		jsonContact.put("time", contact.getTime());
		jsonContact.put("type", contact.getMsgType());
		jsonContact.put("direction", contact.getDirection());

		return jsonContact;
	}

	public static Chat contact2Chat(Contact contact) {
		Chat chat= new Chat(contact.getName(), contact.getLastMsg(), contact.getDirection(), contact.getHead(),
				contact.getIp(), contact.getTime());
		chat.setMsgType(contact.getMsgType());
		return chat;
	}
	
	public static String makeSendDate(RequestHead requestHead ,Contact contact) throws JSONException {
		
		JSONObject jsonObject=new JSONObject();
		
		jsonObject.put("head", requestHead2Json(requestHead));
		jsonObject.put("info", contact2Json(contact));
		
		return jsonObject.toString();
	}
	public static synchronized byte[] preforData(String msg,int Mode)  {
		 byteBuffer.clear(); 
		 byte msgBytes[] = null;
		 try {
			msgBytes = msg.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		 switch (Mode) {
		case 1:
			byteBuffer.put((byte) 1);
			
			break;
		case 2:
			byteBuffer.put((byte) 2);
			break;
		case 3:
			byteBuffer.put((byte) 3);
			break;
		case 4:
			byteBuffer.put((byte) 4);
			break;
		}
		byteBuffer.putInt(msgBytes.length);
		byteBuffer.put(msgBytes);
		
		return byteBuffer.array();
		
	}
}

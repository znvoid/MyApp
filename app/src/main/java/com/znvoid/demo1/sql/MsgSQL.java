package com.znvoid.demo1.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.znvoid.demo1.daim.Chat;
import com.znvoid.demo1.daim.Contact;

import java.util.ArrayList;
import java.util.List;

public class MsgSQL extends MySQLiteOpenHelper {

	public MsgSQL(Context context) {
		super(context);

	}

	// 增加
	public void add(Contact contact) {
		SQLiteDatabase db = getWritableDatabase();
		if (db.isOpen()) {

			ContentValues cont = new ContentValues();
			cont.put("contact", contact.getId());
			cont.put("message", contact.getLastMsg());
			cont.put("time", contact.getTime());
			cont.put("direction", contact.getDirection());
			cont.put("type", contact.getMsgType());

			db.insert("messagedata", null, cont);

			// chatcontacts 表操作

			Cursor cursor = db.rawQuery("select * from chatContacts where contact = ? ",
					new String[] { contact.getId() });
			if (cursor.moveToFirst()) {
				ContentValues cont1 = new ContentValues();
				// int Contactsid = cursor.getInt(0); // 获取第一列的值,第一列的索引从0开始
				// String id = cursor.getString(1);// 获取第二列的值
				// String name = cursor.getString(2);// 获取第3列的值
				// String head = cursor.getString(3);// 获取第4列的值
				// String lastMsg = cursor.getString(4);// 获取第5列的值
				// String time = cursor.getString(5);// 获取第5列的值
				// String ip = cursor.getString(6);// 获取第5列的值
				// String type = cursor.getString(7);// 获取第5列的值
				// int direction = cursor.getInt(8);// 获取第四列的值

				cont1.put("contact", contact.getId());
				cont1.put("name", contact.getName());
				cont1.put("head", contact.getHead());
				cont1.put("ip", contact.getIp());

				db.update("chatContacts", cont1, "contact=?", new String[] { contact.getId() });

			} else {

				ContentValues cont1 = new ContentValues();
				cont1.put("contact", contact.getId());
				cont1.put("name", contact.getName());
				cont1.put("head", contact.getHead());
				cont1.put("ip", contact.getIp());

				db.insert("chatContacts", null, cont1);

				//

			}

			cursor.close();

			db.close();
		}
	}

	// 删除
	public void delete(String id, Chat chat) {
		SQLiteDatabase db = getWritableDatabase();
		if (db.isOpen()) {
			// db.execSQL("delete from person where name=?", new
			// Object[]{name});

			db.delete("messageData", "contact=? and message=? and direction=? and time=?",
					new String[] { id, chat.getMessage(), String.valueOf(chat.getDirection()), chat.getTime() });

			db.close();
		}
	}
	public void delete(Contact contact) {
		SQLiteDatabase db = getWritableDatabase();
		if (db.isOpen()) {
			// db.execSQL("delete from person where name=?", new
			// Object[]{name});

			db.delete("chatContacts", "contact=?",
					new String[] {contact.getId() });

			db.close();
		}
	}

	// 获取所有数据
	public List<Contact> loadContacts() {
		List<Contact> listContact = new ArrayList<Contact>();
		Cursor cursor = findAllByCursor();
		while (cursor.moveToNext()) {
			// Contact TEXT,name varchar(20),head varchar(20),lastMsg TEXT,ip
			// TEXT,type varchar(20)"
			int Contactsid = cursor.getInt(0); // 获取第一列的值,第一列的索引从0开始
			String id = cursor.getString(1);// 获取第二列的值
			String name = cursor.getString(2);// 获取第3列的值
			String head = cursor.getString(3);// 获取第4列的值
			String ip = cursor.getString(4);// 获取第5列的值
			String lastMsg = "NULL";
			String time = "NULL";
			String type = "NULL";
			int direction = -1;
			Cursor cursor1 = findAllByCursor(id);
			if (cursor1 != null) {
				if (cursor1.moveToLast()) {

					lastMsg = cursor1.getString(2);
					time = cursor1.getString(3);
					type = cursor1.getString(4);
					direction = cursor1.getInt(5);

					cursor1.close();
				}
			}

			listContact.add(new Contact(id, name, head, lastMsg, time, ip, type, direction));

		}
		cursor.close();

		return listContact;
	}

	/**
	 * 
	 * @param contact
	 * @return
	 */
	public List<Chat> loadMsg(Contact contact) {
		List<Chat> list = new ArrayList<Chat>();
		Cursor cursor = findAllByCursor(contact.getId());
		while (cursor.moveToNext()) {
			//
			int msgid = cursor.getInt(0); // 获取第一列的值,第一列的索引从0开始
			// String name = cursor.getString(1);// 获取第二列的值
			String message = cursor.getString(2);// 获取第3列的值
			String time = cursor.getString(3);// 获取第4列的值
			int direction = cursor.getInt(4);// 获取第四列的值
			String msgType = cursor.getString(5);
			list.add(new Chat(contact.getName(), message, direction, contact.getHead(), contact.getIp(), time,msgType));

		}
		cursor.close();

		return list;
	}

	// 查找cursor
	/**
	 * 
	 * @return 返回所有联系人cursor
	 */
	public Cursor findAllByCursor() {
		SQLiteDatabase db = getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.query("chatContacts", null, null, null, null, null, null);
			return cursor;
		}
		return null;

	}

	// 查找cursor
	/**
	 * 
	 * @param id
	 * @return
	 */
	public Cursor findByCursor(String id) {
		SQLiteDatabase db = getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.query("chatContacts", null, "contact=?", new String[] { id }, null, null, null);
			return cursor;
		}
		return null;

	}

	/**
	 * 根据联系人id 找所有的聊天记录
	 * 
	 * @param id
	 * @return
	 */
	public Cursor findAllByCursor(String id) {
		SQLiteDatabase db = getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.query("messageData", null, "contact=?", new String[] { id }, null, null, null);
			return cursor;
		}
		return null;

	}

	public Cursor findAllMsgDateByCursor() {
		SQLiteDatabase db = getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.query("messageData", null, null, null, null, null, null);
			return cursor;
		}
		return null;
	}
/**
 * 为检测设备存在时更新contact 的ip等信息
 * @param contact
 */
	public void updataContact(Contact contact) {
		
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor cursor =db.rawQuery("select * from chatContacts where contact = ? ",
				new String[] { contact.getId() });
		if (cursor.moveToFirst()) {
			ContentValues cont = new ContentValues();
			cont.put("contact", contact.getId());
			cont.put("name", contact.getName());
			cont.put("head", contact.getHead());
			cont.put("ip", contact.getDirection());

			db.update("chatContacts", cont, "contact=?", new String[] { contact.getId() });
			cursor.close();
		}
		
		
		db.close();
	}
	
	public boolean findMsg(Contact contact) {
		boolean result=false;
		SQLiteDatabase db = getReadableDatabase();
		if (db.isOpen()) {
			
			Cursor cursor = db.query("messageData", null, "contact=? and message=? and time=?", new String[] { contact.getId(),contact.getLastMsg(),contact.getTime() }, null, null, null);
			
			if (cursor.moveToFirst()) {
				result=true;
				cursor.close();
			}
			db.close();
		}
		
		
		return result;
	}
}

package com.znvoid.demo1.sql;

import java.util.ArrayList;
import java.util.List;

import com.znvoid.demo1.daim.Chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

public class ChatSqlOpenHelp extends SQLiteOpenHelper {

	public ChatSqlOpenHelp(Context context) {
		super(context, "chatdata.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(
				"CREATE TABLE IF NOT EXISTS chatdata (chatid integer primary key autoincrement, name varchar(20), message TEXT,direction INT,head varchar(20),ip TEXT,time TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	// 增加
	public void add(Chat chat) {
		SQLiteDatabase db = getWritableDatabase();
		if (db.isOpen()) {

			ContentValues cont = new ContentValues();
			cont.put("name", chat.getAuthor());
			cont.put("message", chat.getMessage());
			cont.put("direction", chat.getDirection());
			cont.put("head", chat.getHead());
			cont.put("ip", chat.getIp());
			cont.put("time", chat.getTime());

			db.insert("chatdata", null, cont);
			db.close();
		}
	}

	// 删除
	public void delete(Chat chat) {
		SQLiteDatabase db = getWritableDatabase();
		if (db.isOpen()) {
			// db.execSQL("delete from person where name=?", new
			// Object[]{name});

			db.delete("chatdata", "name=? and message=? and direction=?and head=? and ip=? and time=?",
					new String[] { chat.getAuthor(), chat.getMessage(), String.valueOf(chat.getDirection()),
							chat.getHead(), chat.getIp() ,chat.getTime()});

			db.close();
		}
	}

	// 获取所有数据
	public List<Chat> loadall() {
		List<Chat> listchat = new ArrayList<Chat>();
		Cursor cursor = findAllByCursor();
		while (cursor.moveToNext()) {
			int chatid = cursor.getInt(0); // 获取第一列的值,第一列的索引从0开始
			String name = cursor.getString(1);// 获取第二列的值
			String message = cursor.getString(2);// 获取第三列的值
			int direction = cursor.getInt(3);// 获取第四列的值
			String head = cursor.getString(4);// 获取第五列的值
			String ip = cursor.getString(5);// 获取第六列的值
			String time = cursor.getString(6);// 获取第七列的值
			listchat.add(new Chat(name, message, direction, head, ip,time));

		}
		cursor.close();

		return listchat;
	}

	// 查找cursor
	public Cursor findAllByCursor() {
		SQLiteDatabase db = getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.query("chatdata", null, null, null, null, null, null);
			return cursor;
		}
		return null;

	}
}

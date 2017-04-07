package com.znvoid.demo1.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	public MySQLiteOpenHelper(Context context) {
		super(context, "msgdata.db", null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"CREATE TABLE IF NOT EXISTS messageData (msgid integer primary key autoincrement, contact varchar(40), message TEXT,time TEXT,direction INT,type varchar(20))");
		db.execSQL(
				"CREATE TABLE IF NOT EXISTS chatContacts (Contactsid integer primary key autoincrement, contact varchar(40),name varchar(20),head varchar(20),ip TEXT)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}

package com.whatswall.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBImgHelper extends SQLiteOpenHelper{

	private static final String DATABASE_NAME = "a.db"; 
	private static final int DATABASE_VERSION = 1; 
	
	public DBImgHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table img(objectId varchar primary key, isWhole integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}

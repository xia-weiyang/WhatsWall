package com.whatswall.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBImgManager {

	private DBImgHelper helper;
	private SQLiteDatabase db;
	
	public DBImgManager(Context mContext) {
		helper = new DBImgHelper(mContext);
		db = helper.getWritableDatabase();
	}
	
	public void add(String objectId, boolean isWhole){
		int a = 0;
		if(isWhole) 
			a = 1;
		db.execSQL("INSERT INTO img VALUES ( ?, ?)", new Object[]{objectId, a});
		
	}
	
	public void update(String objectId, boolean isWhole){
		int a = 0;
		if(isWhole) 
			a = 1;
		ContentValues cv = new ContentValues();
		cv.put("isWhole", a);
		db.update("img", cv, "objectId = ?", new String[]{objectId});
	}
	
    public boolean query(String objectId){
    	Cursor c = db.rawQuery("SELECT * FROM img WHERE objectId like ?", new String[]{objectId});
    	boolean isWhole = false;
    	int a = 0;
    	while (c.moveToNext()) {
    		a = c.getInt(c.getColumnIndex("isWhole"));
    	}
    	c.close();
    	if(a == 1)
    		isWhole = true;
    	return isWhole;
    }
}

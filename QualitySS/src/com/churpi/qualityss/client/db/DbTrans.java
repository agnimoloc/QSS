package com.churpi.qualityss.client.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbTrans {
	
	public interface Db{
		public Object onDo(Context context, SQLiteDatabase db);
	}
	public static Object read(Context context, Db reader){
		QualitySSDbHelper dbHelper = new QualitySSDbHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return reader.onDo(context, db);
	}
	
	public static Object write(Context context, Db writer){
		QualitySSDbHelper dbHelper = new QualitySSDbHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Object result = null;
		try{
			db.beginTransaction();
			result = writer.onDo(context, db);			
			db.setTransactionSuccessful();			
		}finally{
			db.endTransaction();
		}
		return result;
	}
	
	

}

package com.churpi.qualityss.client.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbTrans {
	
	public interface Db{
		public Object onDo(Context context, Object parameter, SQLiteDatabase db);
	}
	public static Object read(Context context, Db reader){
		return read(context, null, reader);
	}
	public static Object read(Context context, Object parameter, Db reader){
		QualitySSDbHelper dbHelper = new QualitySSDbHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return reader.onDo(context, parameter, db);
	}
	
	public static Object write(Context context, Db writer){
		return write(context, null, writer);
	}
	public static Object write(Context context, Object parameter, Db writer){
		QualitySSDbHelper dbHelper = new QualitySSDbHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Object result = null;
		try{
			db.beginTransaction();
			result = writer.onDo(context, parameter, db);
			db.setTransactionSuccessful();			
		}finally{
			db.endTransaction();
		}
		return result;
	}
	
	

}

package com.churpi.qualityss.client.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbTrans {
	
	public interface Db{
		public void onDo(Context context, SQLiteDatabase db);
	}
	public static void read(Context context, Db reader){
		QualitySSDbHelper dbHelper = new QualitySSDbHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		reader.onDo(context, db);
	}
	
	public static void write(Context context, Db writer){
		QualitySSDbHelper dbHelper = new QualitySSDbHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try{
			db.beginTransaction();
			writer.onDo(context, db);			
			db.setTransactionSuccessful();			
		}finally{
			db.endTransaction();
		}
		
	}
	
	

}

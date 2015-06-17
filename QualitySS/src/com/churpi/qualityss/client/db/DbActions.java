package com.churpi.qualityss.client.db;

import java.io.File;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbImageToSend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DbActions {
	public static void deleteImageAndFromQueue(Context context, File file){
		DbTrans.write(context, file, new DbTrans.Db() {

			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				File file = (File)parameter;
				db.delete(DbImageToSend.TABLE_NAME, DbImageToSend.CN_URL + " = ?",
						new String[]{ file.getAbsolutePath() });
				if(file.exists()){
					file.delete();
				}
				return null;
			}
		});
	}
}

package com.churpi.qualityss.client.db;

import android.provider.BaseColumns;

public final class QualitySSDbContract {
	public QualitySSDbContract(){}
	
	public static abstract class DbUser implements BaseColumns {
		public static final String TABLE_NAME = "user";
		public static final String CN_NAME = "name";
		public static final String CN_ACCOUNT = "account";
		public static final String CN_PASSWORD = "password";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_NAME + " TEXT, " +
				CN_ACCOUNT + " TEXT, " +
				CN_PASSWORD + " TEXT);";
	}
	
	public static abstract class DbConsigna implements BaseColumns {
		public static final String TABLE_NAME = "consigna";
		public static final String CN_NAME = "name";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				CN_NAME + " TEXT);";
	}
	
	public static abstract class DbConsignaDetalle implements BaseColumns {
		public static final String TABLE_NAME = "consigna_detalle";
		public static final String CN_NAME = "name";
		public static final String CN_CONSIGNA = "consignaID";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_NAME + " TEXT, " +
				CN_CONSIGNA + " INTEGER, " +
				"FOREIGN KEY(" + CN_CONSIGNA +
				") REFERENCES "+ DbConsigna.TABLE_NAME + 
				"(" + DbConsigna._ID + "));";
	}
	
	public static abstract class DbService implements BaseColumns {
		public static final String TABLE_NAME = "service";
		public static final String CN_TYPE = "type";
		public static final String CN_ADDRESS = "address";
		public static final String CN_CONTACT = "contact";
		public static final String CN_PHONE = "phone";
		public static final String CN_DESCRIPTION = "description";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_TYPE + " TEXT, " +
				CN_ADDRESS + " TEXT, " +
				CN_CONTACT + " TEXT, " +
				CN_PHONE + " TEXT, " +
				CN_DESCRIPTION + " TEXT);";
	}
	
	public static abstract class DbEmployee implements BaseColumns {
		public static final String TABLE_NAME = "employee";
		public static final String CN_NAME = "name";
		public static final String CN_SERVICE = "serviceID";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_NAME  + " TEXT, " +
				CN_SERVICE + " INTEGER, " +
				"FOREIGN KEY(" + CN_SERVICE +
				") REFERENCES "+ DbService.TABLE_NAME + 
				"(" + DbService._ID + "));";
	}
	
	public static abstract class DbGeneralCheckpoint implements BaseColumns {
		public static final String TABLE_NAME = "general_checkpoint";
		public static final String CN_NAME = "name";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_NAME + " TEXT);";
	}
	
	public static abstract class DbGeneralCheckpointResult implements BaseColumns {
		public static final String TABLE_NAME = "general_checkpoint_result";
		public static final String CN_SERVICE = "serviceID";
		public static final String CN_GENERAL_CHECKPOINT = "general_checkpointID";
		public static final String CN_COMMENT = "comment";

		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "(" + 
				//_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				CN_SERVICE + " INTEGER," +
				CN_GENERAL_CHECKPOINT +" INTEGER," +
				CN_COMMENT + " TEXT," +
				"FOREIGN KEY(" + CN_SERVICE + ") " + 
				"REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + ")," +
				"FOREIGN KEY(" + CN_GENERAL_CHECKPOINT + ") " + 
				"REFERENCES "+ DbGeneralCheckpoint.TABLE_NAME + "(" + DbGeneralCheckpoint._ID + "), "+ 
				"PRIMARY KEY (" + CN_SERVICE + "," + CN_GENERAL_CHECKPOINT + "));";
	}
	
}

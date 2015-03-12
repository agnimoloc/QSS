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
		
	public static abstract class DbService implements BaseColumns {
		public static final String TABLE_NAME = "service";
		public static final String CN_TYPE = "type";
		public static final String CN_CODE = "code";
		public static final String CN_DESCRIPTION = "description";
		public static final String CN_ADDRESS = "address";
		public static final String CN_CUSTOMER = "customer";
		
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_TYPE + " INTEGER, " +
				CN_CODE + " TEXT, " +
				CN_DESCRIPTION + " TEXT, " +
				CN_ADDRESS + " TEXT, " +
				CN_CUSTOMER + " TEXT);";
	}
	
	public static abstract class DbEmployee implements BaseColumns {
		public static final String TABLE_NAME = "employee";
		public static final String CN_CODE = "code";
		public static final String CN_NAME = "name";
		public static final String CN_PLATE = "plate";
		public static final String CN_SERVICE = "serviceID";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_CODE  + " TEXT, " +
				CN_NAME  + " TEXT, " +
				CN_PLATE  + " TEXT, " +
				CN_SERVICE + " INTEGER, " +
				"FOREIGN KEY(" + CN_SERVICE +
				") REFERENCES "+ DbService.TABLE_NAME + 
				"(" + DbService._ID + "));";
	}
	
	public static abstract class DbServiceEmployee implements BaseColumns {
		public static final String TABLE_NAME = "service_employee";
		public static final String CN_SERVICE = "serviceID";
		public static final String CN_EMPLOYEE = "employeeID";
		public static final String CN_TYPE = "type";

		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "(" + 
				CN_SERVICE + " INTEGER," +
				CN_EMPLOYEE +" INTEGER," +
				CN_TYPE + " TEXT," +
				"FOREIGN KEY(" + CN_SERVICE + ") " + 
				"REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + ")," +
				"FOREIGN KEY(" + CN_EMPLOYEE + ") " + 
				"REFERENCES "+ DbEmployee.TABLE_NAME + "(" + DbEmployee._ID + "), "+ 
				"PRIMARY KEY (" + CN_SERVICE + "," + CN_EMPLOYEE + "));";
	}
	
	
	
	
	public static abstract class DbEquipment implements BaseColumns {
		public static final String TABLE_NAME = "equipment";
		public static final String CN_DESCRIPTION = "description";
		public static final String CN_TYPE = "type";
		public static final String CN_EMPLOYEE = "employee";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_DESCRIPTION  + " TEXT, " +
				CN_TYPE  + " TEXT, " +
				CN_EMPLOYEE + " INTEGER, " +
				"FOREIGN KEY(" + CN_EMPLOYEE + ") "	+ 
				"REFERENCES " + DbEmployee.TABLE_NAME +	"(" + DbEmployee._ID + "));";
	}
	
	/*public static abstract class DbGeneralCheckpoint implements BaseColumns {
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
	}*/
	
}

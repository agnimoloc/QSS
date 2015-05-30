package com.churpi.qualityss.client.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class QualitySSDbContract {
	public QualitySSDbContract(){}
	
	public static abstract class DbState implements BaseColumns{
		public static final String TABLE_NAME = "state";
		public static final String CN_NAME = "name";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "(" 
						+ _ID + " INTEGER PRIMARY KEY," 
						+ CN_NAME  + " TEXT);";
	}
	
	public static abstract class DbTown implements BaseColumns{
		public static final String TABLE_NAME = "town";
		public static final String CN_NAME = "name";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "(" 
						+ _ID + " INTEGER PRIMARY KEY," 
						+ CN_NAME  + " TEXT);";
	}
	
	public static abstract class DbAddress implements BaseColumns{
		public static final String TABLE_NAME = "address";
		public static final String CN_STREET = "street";
		public static final String CN_NO_EXT = "noExt";
		public static final String CN_NO_INT = "noInt";
		public static final String CN_COLONY = "colony";
		public static final String CN_STATE = "stateId";
		public static final String CN_POSTAL = "postal";
		public static final String CN_TOWN = "townId";
		public static final String CN_REF_1 = "ref1";
		public static final String CN_REF_2 = "ref2";
		public static final String CN_PHONE_1 = "phone1";
		public static final String CN_PHONE_2 = "phone2";
		
		/** this constants is only for queries is not store in db **/
		public static final String CN_STATE_NAME = "stateName";
		public static final String CN_TOWN_NAME = "townName";
		
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "(" 
						+ _ID + " INTEGER PRIMARY KEY," 
						+ CN_STREET + " TEXT,"
						+ CN_NO_EXT + " TEXT,"
						+ CN_NO_INT + " TEXT,"
						+ CN_COLONY + " TEXT,"
						+ CN_POSTAL + " INTEGER,"
						+ CN_STATE + " INTEGER,"
						+ CN_TOWN + " INTEGER,"
						+ CN_REF_1 + " TEXT,"
						+ CN_REF_2 + " TEXT,"
						+ CN_PHONE_1 + " TEXT,"
						+ CN_PHONE_2 + " TEXT,"
						+ "FOREIGN KEY(" + CN_STATE + ") " 
						+ "REFERENCES "+ DbState.TABLE_NAME + "(" + DbState._ID + "),"
						+ "FOREIGN KEY(" + CN_TOWN + ") " 
						+ "REFERENCES "+ DbTown.TABLE_NAME + "(" + DbTown._ID + "));";
	}

	public static abstract class DbCustomer implements BaseColumns {
		public static final String TABLE_NAME = "customer";
		public static final String CN_CODE = "code";
		public static final String CN_DESCRIPTION = "description";
		public static final String CN_ADDRESS = "addressId";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_CODE + " TEXT, " +
				CN_DESCRIPTION + " TEXT, " +
				CN_ADDRESS + " INTEGER,"
				+ "FOREIGN KEY(" + CN_ADDRESS + ") " 
				+ "REFERENCES "+ DbAddress.TABLE_NAME + "(" + DbAddress._ID + "));";
	}
	
	public static abstract class DbSector implements BaseColumns {
		public static final String TABLE_NAME = "sector";
		public static final String CN_NAME = "name";		
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_NAME + " TEXT);";
	}
	
	public static abstract class DbEmployee implements BaseColumns {
		public static final String TABLE_NAME = "employee";
		public static final String CN_CODE = "code";
		public static final String CN_NAME = "name";
		public static final String CN_PLATE = "plate";
		public static final String CN_STATUS = "status";
		public static final String CN_INVENTORY_COMMENT = "inventory_comment";
		public static final String CN_REVIEW_COMMENT = "review_comment";
		public static final String CN_SURVEY_COMMENT = "survey_comment";
		public static final String CN_BARCODECHECK = "checkBarcode";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_CODE  + " TEXT, " +
				CN_NAME  + " TEXT, " +
				CN_PLATE  + " TEXT, " +
				CN_STATUS + " TEXT, " +
				CN_INVENTORY_COMMENT + " TEXT, " +
				CN_REVIEW_COMMENT + " TEXT, " +
				CN_SURVEY_COMMENT + " TEXT, " +
				CN_BARCODECHECK + " INTEGER);";
		
		public class EmployeeStatus{
			public final static String CURRENT = "C";
			public final static String FINALIZED = "F";
			public final static String SENT = "S";
		}
		
		public static void setStatus(SQLiteDatabase db, int employeeId, String status){
			String whereClause = DbEmployee._ID + "=?";
			String[] whereArgs = new String[]{String.valueOf(employeeId)}; 
			Cursor cur = db.query(DbEmployee.TABLE_NAME, 
					new String[]{DbEmployee.CN_STATUS},
					whereClause, whereArgs,
					null,null,null);
			try{
				if(cur.moveToFirst() ){
					int columnIndex = cur.getColumnIndex(DbEmployee.CN_STATUS);
					String currentStatus = cur.getString(columnIndex);
					ContentValues values = new ContentValues();
					values.put(DbEmployee.CN_STATUS, status);

					if(cur.isNull(columnIndex) || currentStatus.compareTo(status) != 0){
						db.update(DbEmployee.TABLE_NAME, values, whereClause, whereArgs);
					}
				}
			}finally{
				cur.close();
			}
		}
		
		public static void clean(SQLiteDatabase db, int employeeId){
			String[] whereArgs = new String[]{String.valueOf(employeeId)}; 
			
			db.delete(DbEmployeeEquipmentInventory.TABLE_NAME, DbEmployeeEquipmentInventory.CN_EMPLOYEE + "=?", whereArgs);
			
			ContentValues values = new ContentValues();
			values.putNull(DbEmployee.CN_STATUS);
			values.putNull(DbEmployee.CN_BARCODECHECK);
			values.putNull(DbEmployee.CN_INVENTORY_COMMENT);
			values.putNull(DbEmployee.CN_REVIEW_COMMENT);
			values.putNull(DbEmployee.CN_SURVEY_COMMENT);
			
			db.update(DbEmployee.TABLE_NAME, values, DbEmployee._ID + "=?", whereArgs);			
			
		}
	}
		
	public static abstract class DbServiceType implements BaseColumns {
		public static final String TABLE_NAME = "service_type";
		public static final String CN_TITLE = "description";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("
				+ _ID + " INTEGER PRIMARY KEY," 
				+ CN_TITLE + " TEXT);";
	}

	public static abstract class DbService implements BaseColumns {
		public static final String TABLE_NAME = "service";
		public static final String CN_CODE = "code";
		public static final String CN_DESCRIPTION = "description";
		public static final String CN_ADDRESS = "addressId";
		public static final String CN_CUSTOMER = "customer";
		public static final String CN_SECTOR = "sectorId";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("
				+ _ID + " INTEGER PRIMARY KEY," 
				+ CN_CODE + " TEXT, " 
				+ CN_DESCRIPTION + " TEXT, "
				+ CN_ADDRESS + " INTEGER,"
				+ CN_CUSTOMER + " INTEGER," 
				+ CN_SECTOR + " INTEGER,"
				+ "FOREIGN KEY(" + CN_ADDRESS + ") " 
				+ "REFERENCES "+ DbAddress.TABLE_NAME + "(" + DbAddress._ID + ")," 
				+ "FOREIGN KEY(" + CN_CUSTOMER + ") " 
				+ "REFERENCES "+ DbCustomer.TABLE_NAME + "(" + DbCustomer._ID + ")," 
				+ "FOREIGN KEY(" + CN_SECTOR + ") " 
				+ "REFERENCES "+ DbSector.TABLE_NAME + "(" + DbSector._ID + "));";
		
	}

	public static abstract class DbServiceConfiguration implements BaseColumns {
		public static final String TABLE_NAME = "service_configuration";
		public static final String CN_SERVICE = "serviceId";
		public static final String CN_TITLE = "title";
		public static final String CN_ACTIVITY_TYPE = "activityType";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("
				+ _ID + " INTEGER PRIMARY KEY," 
				+ CN_SERVICE + " INTEGER, " 
				+ CN_TITLE + " TEXT, "
				+ CN_ACTIVITY_TYPE + " INTEGER,"
				+ "FOREIGN KEY(" + CN_ACTIVITY_TYPE + ") " 
				+ "REFERENCES "+ DbServiceType.TABLE_NAME + "(" + DbServiceType._ID + "),"
				+ "FOREIGN KEY(" + CN_SERVICE + ") " 
				+ "REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + "));";
		
	}

	public static abstract class DbServiceInstance implements BaseColumns {
		public static final String TABLE_NAME = "service_instance";
		public static final String CN_ACTIVITY_TYPE = "activityType";
		public static final String CN_SERVICE = "serviceId";
		public static final String CN_KEY = "KEY"; //composite key with format (datestart(YYYYMMDD)+ '|' + employeeReview + '|' + activityType + '|' + serviceId 
		public static final String CN_EMPLOYEEREVIEW = "employeeReviewId";
		public static final String CN_START_DATETIME = "startDate";
		public static final String CN_FINISH_DATETIME = "finishDate";
		public static final String CN_STATUS = "status";
		public static final String CN_INVENTORY_COMMENT = "inventoryComment";
		public static final String CN_REVIEW_COMMENT = "reviewComment";
		public static final String CN_EMPLOYEE_COMMENT = "employee_comment";
		public static final String CN_SERVICE_CONFIGURATION = "serviceConfigurationId";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ CN_ACTIVITY_TYPE + " INTEGER,"
				+ CN_SERVICE + " INTEGER," 
				+ CN_KEY + " TEXT,"
				+ CN_EMPLOYEEREVIEW + " INTEGER,"
				+ CN_START_DATETIME + " TEXT," 
				+ CN_FINISH_DATETIME + " TEXT," 
				+ CN_STATUS + " TEXT,"
				+ CN_INVENTORY_COMMENT + " TEXT," 
				+ CN_REVIEW_COMMENT + " TEXT," 
				+ CN_EMPLOYEE_COMMENT + " TEXT," 
				+ CN_SERVICE_CONFIGURATION + " INTEGER,"
				+ "FOREIGN KEY(" + CN_ACTIVITY_TYPE + ") " 
				+ "REFERENCES "+ DbServiceType.TABLE_NAME + "(" + DbServiceType._ID + "),"
				+ "FOREIGN KEY(" + CN_SERVICE + ") " 
				+ "REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + ")," 
				+ "FOREIGN KEY(" + CN_SERVICE_CONFIGURATION + ") " 
				+ "REFERENCES "+ DbServiceConfiguration.TABLE_NAME + "(" + DbServiceConfiguration._ID + ")," 
				+ "FOREIGN KEY(" + CN_EMPLOYEEREVIEW+ ") " 
				+ "REFERENCES "+ DbEmployee.TABLE_NAME + "(" + DbEmployee._ID + "));";
		
		public class ServiceStatus{
			public final static String CURRENT = "C";
			public final static String FINALIZED = "F";
			public final static String SENT = "S";
		}
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
				CN_TYPE + " INTEGER," +
				"FOREIGN KEY(" + CN_SERVICE + ") " + 
				"REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + ")," +
				"FOREIGN KEY(" + CN_EMPLOYEE + ") " + 
				"REFERENCES "+ DbEmployee.TABLE_NAME + "(" + DbEmployee._ID + "), "+ 
				"PRIMARY KEY (" + CN_SERVICE + "," + CN_EMPLOYEE + "));";
	}
	
	public static abstract class DbServiceEquipment implements BaseColumns {
		public static final String TABLE_NAME = "service_equipment";
		public static final String CN_ACTIVITY_TYPE = "activityType";
		public static final String CN_SERVICE = "serviceId";
		public static final String CN_EQUIPMENT = "equipmentId";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "(" +
				CN_ACTIVITY_TYPE + " INTEGER, " +
				CN_SERVICE + " INTEGER, " +
				CN_EQUIPMENT + " INTEGER, " +
				"FOREIGN KEY(" + CN_EQUIPMENT + ") " + 
				"REFERENCES "+ DbEquipment.TABLE_NAME + "(" + DbEquipment._ID + ")," +
				"FOREIGN KEY(" + CN_ACTIVITY_TYPE + ") " +
				"REFERENCES "+ DbServiceType.TABLE_NAME + "(" + DbServiceType._ID + ")," +
				"FOREIGN KEY(" + CN_SERVICE + ") " + 
				"REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + "), "+ 
				"PRIMARY KEY (" + CN_EQUIPMENT + "," + CN_SERVICE + "," + CN_ACTIVITY_TYPE + "));";
	}	
	public static abstract class DbServiceEquipmentInventory implements BaseColumns {
		public static final String TABLE_NAME = "service_equipment_inventory";
		public static final String CN_SERVICE_INSTANCE = "serviceInstanceId";
		public static final String CN_EQUIPMENT = "equipmentId";
		public static final String CN_CHECKED = "checked";
		public static final String CN_COMMENT = "comment";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_SERVICE_INSTANCE + " INTEGER, " +
				CN_EQUIPMENT + " INTEGER, " +
				CN_CHECKED + " INTEGER, " +
				CN_COMMENT + " TEXT, " +
				"FOREIGN KEY(" + CN_EQUIPMENT + ") " + 
				"REFERENCES "+ DbServiceInstance.TABLE_NAME + "(" + DbEquipment._ID + ")," +
				"FOREIGN KEY(" + CN_SERVICE_INSTANCE + ") " + 
				"REFERENCES "+ DbServiceInstance.TABLE_NAME + "(" + DbServiceInstance._ID + "), "+ 
				"PRIMARY KEY (" + CN_EQUIPMENT + "," + CN_SERVICE_INSTANCE + "));";
	}	
		
	
	public static abstract class DbEquipment implements BaseColumns {
		public static final String TABLE_NAME = "equipment";
		public static final String CN_DESCRIPTION = "description";
		public static final String CN_TYPE = "type";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_DESCRIPTION  + " TEXT, " +
				CN_TYPE  + " INTEGER);";
	}
	
	public static abstract class DbEmployeeEquipment implements BaseColumns {
		public static final String TABLE_NAME = "employee_equipment";
		public static final String CN_EMPLOYEE = "employeeId";
		public static final String CN_EQUIPMENT = "equipmentId";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_EMPLOYEE + " INTEGER, " +
				CN_EQUIPMENT + " INTEGER, " +
				"FOREIGN KEY(" + CN_EQUIPMENT + ") " + 
				"REFERENCES "+ DbEquipment.TABLE_NAME + "(" + DbEquipment._ID + ")," +
				"FOREIGN KEY(" + CN_EMPLOYEE + ") " + 
				"REFERENCES "+ DbEmployee.TABLE_NAME + "(" + DbEmployee._ID + "), "+ 
				"PRIMARY KEY (" + CN_EQUIPMENT + "," + CN_EMPLOYEE + "));";
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
	
	
	public static abstract class DbEmployeeEquipmentInventory implements BaseColumns {
		public static final String TABLE_NAME = "employee_equipment_inventory";
		public static final String CN_EMPLOYEE = "employeeId";
		public static final String CN_EQUIPMENT = "equipmentId";
		public static final String CN_CHECKED = "checked";
		public static final String CN_COMMENT = "comment";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_EMPLOYEE + " INTEGER, " +
				CN_EQUIPMENT + " INTEGER, " +
				CN_CHECKED + " INTEGER, " +
				CN_COMMENT + " TEXT, " +
				"FOREIGN KEY(" + CN_EQUIPMENT + ") " + 
				"REFERENCES "+ DbEquipment.TABLE_NAME + "(" + DbEquipment._ID + ")," +
				"FOREIGN KEY(" + CN_EMPLOYEE + ") " + 
				"REFERENCES "+ DbEmployee.TABLE_NAME + "(" + DbEmployee._ID + "), "+ 
				"PRIMARY KEY (" + CN_EQUIPMENT + "," + CN_EMPLOYEE + "));";
	}	
	
	public static abstract class DbSection implements BaseColumns{
		public static final String TABLE_NAME = "section";
		public static final String CN_DESCRIPTION = "description";
		public static final String CN_VALUE = "value";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "(" 
						+ _ID + " INTEGER PRIMARY KEY," 
						+ CN_DESCRIPTION  + " TEXT,"
						+ CN_VALUE  + " TEXT);";
	}

	public static abstract class DbQuestion implements BaseColumns{
		public static final String TABLE_NAME = "question";
		public static final String CN_DESCRIPTION = "description";
		public static final String CN_VALUE = "value";
		
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "(" 
						+ _ID + " INTEGER PRIMARY KEY," 
						+ CN_DESCRIPTION  + " TEXT,"
						+ CN_VALUE + " NUMERIC);";
	}
	
	public static abstract class DbReviewQuestion implements BaseColumns {
		public static final String TABLE_NAME = "review_question";
		public static final String CN_ACTIVITY_TYPE = "activityType";
		public static final String CN_SERVICE = "serviceId";
		public static final String CN_QUESTION = "questionId";
		public static final String CN_SECTION = "sectionId";
		public static final String CN_TYPE = "type";
		
		/** this constant is only for queries, this not store in db **/
		public static final String CN_SECTION_NAME = "sectionName";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+
				CN_ACTIVITY_TYPE + " INTEGER, " +
				CN_SERVICE + " INTEGER, " +
				CN_QUESTION + " INTEGER, " +
				CN_SECTION + " INTEGER, " +
				CN_TYPE + " INTEGER, " +
				"FOREIGN KEY(" + CN_ACTIVITY_TYPE + ") " + 
				"REFERENCES "+ DbServiceType.TABLE_NAME + "(" + DbServiceType._ID + ")," +
				"FOREIGN KEY(" + CN_SERVICE + ") " + 
				"REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + ")," +
				"FOREIGN KEY(" + CN_QUESTION + ") " + 
				"REFERENCES "+ DbQuestion.TABLE_NAME + "(" + DbQuestion._ID + "), "+
				"FOREIGN KEY(" + CN_SECTION + ") " + 
				"REFERENCES "+ DbSection.TABLE_NAME + "(" + DbSection._ID + ")," +
				"PRIMARY KEY (" + CN_ACTIVITY_TYPE + "," + CN_SERVICE + "," + CN_QUESTION + "," + CN_TYPE + "));";
		
		public class Types{
			public static final int SERVICE = 0;
			public static final int EMPLOYEE = 1; 
		}
	}	
	public static abstract class DbReviewQuestionAnswerEmployee implements BaseColumns {
		public static final String TABLE_NAME = "review_question_answer_employee";
		public static final String CN_SERVICE_INSTANCE = "serviceInstanceId";
		public static final String CN_QUESTION = "questionId";
		public static final String CN_EMPLOYEE = "employeeId";
		public static final String CN_RESULT = "result";
		public static final String CN_COMMENT = "comment";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_SERVICE_INSTANCE + " INTEGER, " +
				CN_QUESTION + " INTEGER, " +
				CN_EMPLOYEE + " INTEGER, " +
				CN_RESULT + " TEXT, " +
				CN_COMMENT + " TEXT, " +
				"FOREIGN KEY(" + CN_SERVICE_INSTANCE + ") " + 
				"REFERENCES "+ DbServiceInstance.TABLE_NAME + "(" + DbServiceInstance._ID + ")," +
				"FOREIGN KEY(" + CN_QUESTION + ") " +
				"REFERENCES "+ DbQuestion.TABLE_NAME + "(" + DbQuestion._ID + "), "+
				"FOREIGN KEY(" + CN_EMPLOYEE + ") " + 
				"REFERENCES "+ DbEmployee.TABLE_NAME + "(" + DbEmployee._ID + ")," +
				"PRIMARY KEY (" + CN_SERVICE_INSTANCE + "," + CN_QUESTION + "," + CN_EMPLOYEE + "));";
	}	
	
	public static abstract class DbReviewQuestionAnswerService implements BaseColumns {
		public static final String TABLE_NAME = "review_question_answer_service";
		public static final String CN_SERVICE_INSTANCE = "serviceInstanceId";
		public static final String CN_QUESTION = "questionId";
		public static final String CN_RESULT = "result";
		public static final String CN_COMMENT = "comment";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_SERVICE_INSTANCE + " INTEGER, " +
				CN_QUESTION + " INTEGER, " +
				CN_RESULT + " TEXT, " +
				CN_COMMENT + " TEXT, " +
				"FOREIGN KEY(" + CN_SERVICE_INSTANCE + ") " + 
				"REFERENCES "+ DbServiceInstance.TABLE_NAME + "(" + DbServiceInstance._ID + ")," +
				"FOREIGN KEY(" + CN_QUESTION + ") " +
				"REFERENCES "+ DbQuestion.TABLE_NAME + "(" + DbQuestion._ID + "), "+
				"PRIMARY KEY (" + CN_SERVICE_INSTANCE + "," + CN_QUESTION + "));";
	}	
	
	public static abstract class DbSurveyQuestion implements BaseColumns {
		public static final String TABLE_NAME = "survey_question";
		public static final String CN_ACTIVITY_TYPE = "activityType";
		public static final String CN_SERVICE = "serviceId";
		public static final String CN_QUESTION = "questionId";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+
				CN_ACTIVITY_TYPE + " INTEGER, " +
				CN_SERVICE + " INTEGER, " +
				CN_QUESTION + " INTEGER, " +
				"FOREIGN KEY(" + CN_ACTIVITY_TYPE + ") " + 
				"REFERENCES "+ DbServiceType.TABLE_NAME + "(" + DbServiceType._ID + ")," +
				"FOREIGN KEY(" + CN_SERVICE + ") " + 
				"REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + ")," +
				"FOREIGN KEY(" + CN_QUESTION + ") " + 
				"REFERENCES "+ DbQuestion.TABLE_NAME + "(" + DbQuestion._ID + "), "+ 
				"PRIMARY KEY (" + CN_ACTIVITY_TYPE + "," + CN_SERVICE + "," + CN_QUESTION + "));";
	}
	
	public static abstract class DbSurveyQuestionAnswer implements BaseColumns {
		public static final String TABLE_NAME = "survey_question_answer";
		public static final String CN_SERVICE_INSTANCE = "serviceInstanceId";
		public static final String CN_QUESTION = "questionId";
		public static final String CN_EMPLOYEE = "employeeId";
		public static final String CN_RESULT = "result";
		public static final String CN_COMMENT = "comment";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_SERVICE_INSTANCE + " INTEGER, " +
				CN_QUESTION + " INTEGER, " +
				CN_EMPLOYEE + " INTEGER, " +
				CN_RESULT + " TEXT, " +
				CN_COMMENT + " TEXT, " +
				"FOREIGN KEY(" + CN_SERVICE_INSTANCE + ") " + 
				"REFERENCES "+ DbServiceInstance.TABLE_NAME + "(" + DbServiceInstance._ID + ")," +
				"FOREIGN KEY(" + CN_QUESTION + ") " + 
				"REFERENCES "+ DbQuestion.TABLE_NAME + "(" + DbQuestion._ID + "), "+ 
				"FOREIGN KEY(" + CN_EMPLOYEE + ") " + 
				"REFERENCES "+ DbEmployee.TABLE_NAME + "(" + DbEmployee._ID + ")," +
				"PRIMARY KEY (" + CN_SERVICE_INSTANCE + "," + CN_QUESTION + "," + CN_EMPLOYEE + "));";
	}	
	public static abstract class DbImageToSend implements BaseColumns {
		public static final String TABLE_NAME = "image_to_send";
		public static final String CN_URL = "url";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_URL + " TEXT," +
				"PRIMARY KEY (" + CN_URL + "));";
	}	
	public static abstract class DbNotification implements BaseColumns {
		public static final String TABLE_NAME = "notifications";
		public static final String CN_TITLE = "title";
		public static final String CN_SERVICE = "serviceId";
		public static final String CN_EMPLOYEE = "employee";
		public static final String CN_MESSAGE = "message";
		public static final String CN_STATUS = "status";
		public static final String CN_PRIORITY = "priority";
		public static final String CN_TYPE = "type";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("
						+ _ID + " INTEGER PRIMARY KEY," 
						+ CN_TITLE  + " TEXT,"
						+ CN_SERVICE + " INTEGER,"
						+ CN_EMPLOYEE  + " INTEGER,"
						+ CN_MESSAGE  + " TEXT,"
						+ CN_STATUS  + " INTEGER,"
						+ CN_PRIORITY  + " INTEGER,"
						+ CN_TYPE  + " INTEGER,"
						+ "FOREIGN KEY(" + CN_SERVICE + ") "  
						+ "REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + ")," 
						+ "FOREIGN KEY(" + CN_EMPLOYEE + ") "  
						+ "REFERENCES "+ DbEmployee.TABLE_NAME + "(" + DbEmployee._ID + "));";
	}	
}

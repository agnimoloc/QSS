package com.churpi.qualityss.client.db;

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
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("+ _ID + " INTEGER PRIMARY KEY," +
				CN_CODE  + " TEXT, " +
				CN_NAME  + " TEXT, " +
				CN_PLATE  + " TEXT);";
	}
		
	public static abstract class DbService implements BaseColumns {
		public static final String TABLE_NAME = "service";
		public static final String CN_CODE = "code";
		public static final String CN_DESCRIPTION = "description";
		public static final String CN_ADDRESS = "addressId";
		public static final String CN_CUSTOMER = "customer";
		public static final String CN_SECTOR = "sectorId";
		public static final String CN_EMPLOYEEREVIEW = "employeeReviewId";
		public static final String CN_DATETIME = "dateReview";
		public static final String CN_LASTREVIEW = "dateLast";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + "("
				+ _ID + " INTEGER PRIMARY KEY," 
				+ CN_CODE + " TEXT, " 
				+ CN_DESCRIPTION + " TEXT, "
				+ CN_ADDRESS + " INTEGER,"
				+ CN_CUSTOMER + " INTEGER," 
				+ CN_SECTOR + " INTEGER,"
				+ CN_EMPLOYEEREVIEW + " INTEGER," 
				+ CN_DATETIME + " TEXT," 
				+ CN_LASTREVIEW + " TEXT," 
				+ "FOREIGN KEY(" + CN_ADDRESS + ") " 
				+ "REFERENCES "+ DbAddress.TABLE_NAME + "(" + DbAddress._ID + ")," 
				+ "FOREIGN KEY(" + CN_CUSTOMER + ") " 
				+ "REFERENCES "+ DbCustomer.TABLE_NAME + "(" + DbCustomer._ID + ")," 
				+ "FOREIGN KEY(" + CN_SECTOR + ") " 
				+ "REFERENCES "+ DbSector.TABLE_NAME + "(" + DbSector._ID + ")," 
				+ "FOREIGN KEY(" + CN_EMPLOYEEREVIEW+ ") " 
				+ "REFERENCES "+ DbEmployee.TABLE_NAME + "(" + DbEmployee._ID + "));";
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
		public static final String CN_SERVICE = "serviceId";
		public static final String CN_EQUIPMENT = "equipmentId";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
						CN_SERVICE + " INTEGER, " +
				CN_EQUIPMENT + " INTEGER, " +
				"FOREIGN KEY(" + CN_EQUIPMENT + ") " + 
				"REFERENCES "+ DbEquipment.TABLE_NAME + "(" + DbEquipment._ID + ")," +
				"FOREIGN KEY(" + CN_SERVICE + ") " + 
				"REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + "), "+ 
				"PRIMARY KEY (" + CN_EQUIPMENT + "," + CN_SERVICE + "));";
	}	
	public static abstract class DbServiceEquipmentInventory implements BaseColumns {
		public static final String TABLE_NAME = "service_equipment_inventory";
		public static final String CN_SERVICE = "serviceId";
		public static final String CN_EQUIPMENT = "equipmentId";
		public static final String CN_CHECKED = "checked";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_SERVICE + " INTEGER, " +
				CN_EQUIPMENT + " INTEGER, " +
				CN_CHECKED + " INTEGER, " +
				"FOREIGN KEY(" + CN_EQUIPMENT + ") " + 
				"REFERENCES "+ DbEquipment.TABLE_NAME + "(" + DbEquipment._ID + ")," +
				"FOREIGN KEY(" + CN_SERVICE + ") " + 
				"REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + "), "+ 
				"PRIMARY KEY (" + CN_EQUIPMENT + "," + CN_SERVICE + "));";
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
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_EMPLOYEE + " INTEGER, " +
				CN_EQUIPMENT + " INTEGER, " +
				CN_CHECKED + " INTEGER, " +
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
		public static final String CN_SERVICE = "serviceId";
		public static final String CN_QUESTION = "questionId";
		public static final String CN_SECTION = "sectionId";
		
		/** this constant is only for queries, this not store in db **/
		public static final String CN_SECTION_NAME = "sectionName";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_SERVICE + " INTEGER, " +
				CN_QUESTION + " INTEGER, " +
				CN_SECTION + " INTEGER, " +
				"FOREIGN KEY(" + CN_SERVICE + ") " + 
				"REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + ")," +
				"FOREIGN KEY(" + CN_QUESTION + ") " + 
				"REFERENCES "+ DbQuestion.TABLE_NAME + "(" + DbQuestion._ID + "), "+
				"FOREIGN KEY(" + CN_SECTION + ") " + 
				"REFERENCES "+ DbSection.TABLE_NAME + "(" + DbSection._ID + ")," +
				"PRIMARY KEY (" + CN_SERVICE + "," + CN_QUESTION + "));";
	}	
	public static abstract class DbReviewQuestionAnswer implements BaseColumns {
		public static final String TABLE_NAME = "review_question_answer";
		public static final String CN_SERVICE = "serviceId";
		public static final String CN_QUESTION = "questionId";
		public static final String CN_EMPLOYEE = "employeeId";
		public static final String CN_RESULT = "result";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_SERVICE + " INTEGER, " +
				CN_QUESTION + " INTEGER, " +
				CN_EMPLOYEE + " INTEGER, " +
				CN_RESULT + " TEXT, " +
				"FOREIGN KEY(" + CN_SERVICE + ") " + 
				"REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + ")," +
				"FOREIGN KEY(" + CN_QUESTION + ") " +
				"REFERENCES "+ DbQuestion.TABLE_NAME + "(" + DbQuestion._ID + "), "+
				"FOREIGN KEY(" + CN_EMPLOYEE + ") " + 
				"REFERENCES "+ DbEmployee.TABLE_NAME + "(" + DbEmployee._ID + ")," +
				"PRIMARY KEY (" + CN_SERVICE + "," + CN_QUESTION + "," + CN_EMPLOYEE + "));";
	}	
	
	public static abstract class DbSurveyQuestion implements BaseColumns {
		public static final String TABLE_NAME = "survey_question";
		public static final String CN_SERVICE = "serviceId";
		public static final String CN_QUESTION = "questionId";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_SERVICE + " INTEGER, " +
				CN_QUESTION + " INTEGER, " +
				"FOREIGN KEY(" + CN_SERVICE + ") " + 
				"REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + ")," +
				"FOREIGN KEY(" + CN_QUESTION + ") " + 
				"REFERENCES "+ DbQuestion.TABLE_NAME + "(" + DbQuestion._ID + "), "+ 
				"PRIMARY KEY (" + CN_SERVICE + "," + CN_QUESTION + "));";
	}
	
	public static abstract class DbSurveyQuestionAnswer implements BaseColumns {
		public static final String TABLE_NAME = "survey_question_answer";
		public static final String CN_SERVICE = "serviceId";
		public static final String CN_QUESTION = "questionId";
		public static final String CN_EMPLOYEE = "employeeId";
		public static final String CN_RESULT = "result";
		
		public static final String CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + "("+ 
				CN_SERVICE + " INTEGER, " +
				CN_QUESTION + " INTEGER, " +
				CN_EMPLOYEE + " INTEGER, " +
				CN_RESULT + " TEXT, " +
				"FOREIGN KEY(" + CN_SERVICE + ") " + 
				"REFERENCES "+ DbService.TABLE_NAME + "(" + DbService._ID + ")," +
				"FOREIGN KEY(" + CN_QUESTION + ") " + 
				"REFERENCES "+ DbQuestion.TABLE_NAME + "(" + DbQuestion._ID + "), "+ 
				"FOREIGN KEY(" + CN_EMPLOYEE + ") " + 
				"REFERENCES "+ DbEmployee.TABLE_NAME + "(" + DbEmployee._ID + ")," +
				"PRIMARY KEY (" + CN_SERVICE + "," + CN_QUESTION + "," + CN_EMPLOYEE + "));";
	}	

}

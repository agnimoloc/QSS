package com.churpi.qualityss;

import android.content.Context;
import android.content.SharedPreferences;

public final class Constants {

	public static final String PULL_PUSH_DATA_ACTION = 
			"com.churpi.qualityss.service.PUSHPULLDATA";

	public static final String UPDATE_DATA_ACTION = 
			"com.churpi.qualityss.service.UPDATEDATA";

	public static final String PULL_PUSH_DATA_REFRESH = 
			"com.churpi.qualityss.service.PUSHPULLDATA_FINISH";

	public static final String PULL_PUSH_DATA_FINISH = 
			"com.churpi.qualityss.service.PUSHPULLDATA_FINISH";
	
	public static final String PULL_PUSH_DATA_FAIL = 
			"com.churpi.qualityss.service.PUSHPULLDATA_FAIL";
		
	
	public static final String PULL_PUSH_DATA_STATUS = 
			"com.churpi.qualityss.service.PUSHPULLSTATUS";
	
	public static final String PULL_PUSH_DATA_PROGRESS = 
			"com.churpi.qualityss.service.PUSHPULLPROGRESS";
	
	public static final String PULL_PUSH_DATA_DESCRIPTION = 
			"com.churpi.qualityss.service.PUSHPULLDESCRIPTION";
	
	public static final String PULL_PUSH_DATA_DATA = 
			"com.churpi.qualityss.service.PUSHPULLDATA";
	
	private static final String PREFERENCES = "QSSPREF";
	public static final String PREF_FILLED_DB = "FILLEDDB";
	public static final String PREF_ACCOUNT = "CURRENT_ACCOUNT";
	public static final String PREF_PASSHASH = "CURRENT_PASSHASH";
	public static final String PREF_EMPLOYEE = "CURRENT_EMPLOYEE";
	public static final String PREF_CHANGESET = "CURRENT_CHANGESET";
	public static final String PREF_ACTIVITY = "CURRENT_ACTIVITY";
	public static final String PREF_SECTOR_ID = "SECTOR_ID";
	public static final String PREF_SERVICE_ID = "SERVICE_ID";
	public static final String PREF_EMPLOYEE_ID = "EMPLOYEE_ID";
	public static final String PREF_EMPLOYEE_NAME = "EMPLOYEE_NAME";
	
	
	public static final String JSON_DIR = "DB";
	public static final String JSON_NAME = "db.json";
	
	public static final String PHOTO_DIR = "picture";
	public static final String PHOTO_GENERAL_CHECKPOINT = "GCHK_%03d_%03d.jpg";
	
	public static final String IMG_SERVICE = "imgServ";
	public static final String IMG_EMPLOYEE = "imgEmp";

	
	
	public static SharedPreferences getPref(Context context){
		return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
	}
	
}

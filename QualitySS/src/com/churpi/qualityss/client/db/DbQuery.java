package com.churpi.qualityss.client.db;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEmployee;

public class DbQuery {
	
	public static final String EMPLOYEES_BY_SERVICE = 
			"SELECT e.* FROM " + DbEmployee.TABLE_NAME + " e " + 
			"INNER JOIN " + DbServiceEmployee.TABLE_NAME + " se ON " + 
				"se." + DbServiceEmployee.CN_EMPLOYEE + " = e." + DbEmployee._ID + 
				" AND se." + DbServiceEmployee.CN_SERVICE + " = ?"; 
	
}

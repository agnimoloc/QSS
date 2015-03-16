package com.churpi.qualityss.client.db;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEmployee;

public class DbQuery {
	
	public static final String EMPLOYEES_BY_SERVICE = 
			"SELECT e.* FROM " + DbEmployee.TABLE_NAME + " e " + 
			"INNER JOIN " + DbServiceEmployee.TABLE_NAME + " se ON " + 
				"se." + DbServiceEmployee.CN_EMPLOYEE + " = e." + DbEmployee._ID + 
				" AND se." + DbServiceEmployee.CN_SERVICE + " = ?";
	public static final String STAFF_INVENTORY = 
			"SELECT e." + DbEquipment._ID + ", "
					+ "CASE WHEN ei." + DbEmployeeEquipmentInventory.CN_CHECKED + " IS NULL "
					+ "THEN 0 ELSE ei." + DbEmployeeEquipmentInventory.CN_CHECKED + " "
					+ "END AS " + DbEmployeeEquipmentInventory.CN_CHECKED
					+ ", e." + DbEquipment.CN_DESCRIPTION + " "
			+ "FROM " + DbEmployeeEquipment.TABLE_NAME + " ee "
			+ "INNER JOIN " + DbEquipment.TABLE_NAME + " e ON "
			+ "e." + DbEquipment._ID + " = ee." + DbEmployeeEquipment.CN_EQUIPMENT + " "
			+ "LEFT JOIN " + DbEmployeeEquipmentInventory.TABLE_NAME + " ei ON "
			+ "e." + DbEquipment._ID + " = ei." + DbEmployeeEquipmentInventory.CN_EQUIPMENT + " AND "
			+ "ee." + DbEmployeeEquipment.CN_EMPLOYEE + " = ei." + DbEmployeeEquipmentInventory.CN_EMPLOYEE
			+ " WHERE ee." + DbEmployeeEquipment.CN_EMPLOYEE + " = ?"; 
	
}

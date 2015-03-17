package com.churpi.qualityss.client.db;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbAddress;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswer;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSection;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbState;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestionAnswer;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbTown;

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
	public static final String SERVICE_INVENTORY = 
			"SELECT e." + DbEquipment._ID + ", "
					+ "CASE WHEN ei." + DbServiceEquipmentInventory.CN_CHECKED + " IS NULL "
					+ "THEN 0 ELSE ei." + DbServiceEquipmentInventory.CN_CHECKED + " "
					+ "END AS " + DbServiceEquipmentInventory.CN_CHECKED
					+ ", e." + DbEquipment.CN_DESCRIPTION + " "
			+ "FROM " + DbServiceEquipment.TABLE_NAME + " ee "
			+ "INNER JOIN " + DbEquipment.TABLE_NAME + " e ON "
			+ "e." + DbEquipment._ID + " = ee." + DbServiceEquipment.CN_EQUIPMENT + " "
			+ "LEFT JOIN " + DbServiceEquipmentInventory.TABLE_NAME + " ei ON "
			+ "e." + DbEquipment._ID + " = ei." + DbServiceEquipmentInventory.CN_EQUIPMENT + " AND "
			+ "ee." + DbServiceEquipment.CN_SERVICE + " = ei." + DbServiceEquipmentInventory.CN_SERVICE
			+ " WHERE ee." + DbServiceEquipment.CN_SERVICE + " = ?";
	
	public static final String STAFF_REVIEW = 
			"SELECT q." + DbQuestion._ID 
					+ ", q." + DbQuestion.CN_DESCRIPTION +", "
					+ "s." + DbSection.CN_DESCRIPTION + " AS " + DbReviewQuestion.CN_SECTION_NAME +", "
					+ "CASE WHEN ra." + DbReviewQuestionAnswer.CN_RESULT + " IS NULL "
					+ "THEN 'B' ELSE ra." + DbReviewQuestionAnswer.CN_RESULT + " "
					+ "END AS " + DbReviewQuestionAnswer.CN_RESULT + " "					
			+ "FROM " + DbReviewQuestion.TABLE_NAME + " rq "
			+ "INNER JOIN " + DbQuestion.TABLE_NAME + " q ON "
			+ "q." + DbQuestion._ID + " = rq." + DbReviewQuestion.CN_QUESTION + " "
			+ "INNER JOIN " + DbSection.TABLE_NAME + " s ON "
			+ "s." + DbSection._ID + " = rq." + DbReviewQuestion.CN_SECTION + " "
			+ "LEFT JOIN " + DbReviewQuestionAnswer.TABLE_NAME + " ra ON "
			+ "q." + DbQuestion._ID + " = ra." + DbReviewQuestionAnswer.CN_QUESTION + " AND "
			+ "rq." + DbReviewQuestion.CN_SERVICE + " = ra." + DbReviewQuestionAnswer.CN_SERVICE + " AND "
			+ "ra." +  DbReviewQuestionAnswer.CN_EMPLOYEE + " = ? "
			+ " WHERE rq." + DbReviewQuestion.CN_SERVICE + " = ?"; 
	public static final String STAFF_SURVEY = 
			"SELECT q." + DbQuestion._ID 
					+ ", q." + DbQuestion.CN_DESCRIPTION +", "
					+ "CASE WHEN sa." + DbSurveyQuestionAnswer.CN_RESULT + " IS NULL "
					+ "THEN '' ELSE sa." + DbSurveyQuestionAnswer.CN_RESULT + " "
					+ "END AS " + DbSurveyQuestionAnswer.CN_RESULT + " "					
			+ "FROM " + DbSurveyQuestion.TABLE_NAME + " sq "
			+ "INNER JOIN " + DbQuestion.TABLE_NAME + " q ON "
			+ "q." + DbQuestion._ID + " = sq." + DbSurveyQuestion.CN_QUESTION + " "
			+ "LEFT JOIN " + DbSurveyQuestionAnswer.TABLE_NAME + " sa ON "
			+ "q." + DbQuestion._ID + " = sa." + DbSurveyQuestionAnswer.CN_QUESTION + " AND "
			+ "sq." + DbSurveyQuestion.CN_SERVICE + " = sa." + DbSurveyQuestionAnswer.CN_SERVICE + " AND "
			+ "sa." +  DbSurveyQuestionAnswer.CN_EMPLOYEE + " = ? "
			+ " WHERE sq." + DbSurveyQuestion.CN_SERVICE + " = ?"; 
	
	public static final String GET_ADDRESS = 
			"SELECT a.*, s." + DbState.CN_NAME + " AS " + DbAddress.CN_STATE_NAME + ", "
					+ "t." + DbTown.CN_NAME + " AS " + DbAddress.CN_TOWN_NAME + " "
			+ "FROM " + DbAddress.TABLE_NAME + " a "
			+ "INNER JOIN " + DbState.TABLE_NAME + " s ON "
					+ "s." + DbState._ID + " = a." + DbAddress.CN_STATE + " "
			+ "INNER JOIN " + DbTown.TABLE_NAME + " t ON "
					+ "t." + DbTown._ID + " = a." + DbAddress.CN_TOWN + " "
			+ "WHERE a." + DbAddress._ID + " = ?";
}

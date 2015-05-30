package com.churpi.qualityss.client.db;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbAddress;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSection;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceConfiguration;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbState;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestionAnswer;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbTown;

public class DbQuery {
	
	public static final String SERVICES_BY_SECTOR = 
			"SELECT "
				+ "s." + DbService._ID + ", "
				+ "i." + DbServiceInstance.CN_STATUS + ", "
				+ "s." + DbService.CN_CODE + ", "
				+ "i." + DbServiceInstance.CN_FINISH_DATETIME + " "
			+ "FROM " + DbService.TABLE_NAME + " s "
			+ "INNER JOIN " + DbServiceConfiguration.TABLE_NAME + " sc ON "
				+ "sc." + DbServiceConfiguration.CN_SERVICE + " = s." + DbService._ID + " AND "
				+ "sc." + DbServiceConfiguration.CN_ACTIVITY_TYPE +" = ? "
			+ "LEFT JOIN " + DbServiceInstance.TABLE_NAME + " i ON "
				+ "i." + DbServiceInstance.CN_SERVICE + " = sc." + DbServiceConfiguration.CN_SERVICE + " AND "
				+ "i." + DbServiceInstance.CN_ACTIVITY_TYPE + " = sc." + DbServiceConfiguration.CN_ACTIVITY_TYPE + " "
			+ "WHERE s." + DbService.CN_SECTOR + " = ?";
	
	public static final String EMPLOYEES_BY_SERVICE = 
			"SELECT e.* "
			+ "FROM " + DbServiceInstance.TABLE_NAME + " si " 
			+ "INNER JOIN " + DbServiceEmployee.TABLE_NAME + " se ON " 
				+ "se." + DbServiceEmployee.CN_SERVICE + " = si." + DbServiceInstance.CN_SERVICE + " "
			+ "INNER JOIN " + DbEmployee.TABLE_NAME + " e ON "
				+ "e." + DbEmployee._ID + " = se." + DbServiceEmployee.CN_EMPLOYEE + " "
			+ "WHERE si." + DbServiceInstance._ID + " = ? ";
	
	public static final String EMPLOYEES_SERVICE_NOT_END = 
			EMPLOYEES_BY_SERVICE
			+ "AND e." + DbEmployee.CN_STATUS + " IS NOT NULL "
			+ "AND e." + DbEmployee.CN_STATUS + " <> '" + DbEmployee.EmployeeStatus.FINALIZED + "'";
	
	public static final String STAFF_INVENTORY = 
			"SELECT e." + DbEquipment._ID 
					+ ", ei." + DbEmployeeEquipmentInventory.CN_CHECKED + " "
					+ ", e." + DbEquipment.CN_DESCRIPTION + " "
					+ ", ei." + DbEmployeeEquipmentInventory.CN_COMMENT + " "
			+ "FROM " + DbEmployeeEquipment.TABLE_NAME + " ee "
			+ "INNER JOIN " + DbEquipment.TABLE_NAME + " e ON "
				+ "e." + DbEquipment._ID + " = ee." + DbEmployeeEquipment.CN_EQUIPMENT + " "
			+ "LEFT JOIN " + DbEmployeeEquipmentInventory.TABLE_NAME + " ei ON "
				+ "e." + DbEquipment._ID + " = ei." + DbEmployeeEquipmentInventory.CN_EQUIPMENT + " AND "
				+ "ee." + DbEmployeeEquipment.CN_EMPLOYEE + " = ei." + DbEmployeeEquipmentInventory.CN_EMPLOYEE
			+ " WHERE ee." + DbEmployeeEquipment.CN_EMPLOYEE + " = ?";
	
	public static final String STAFF_INVENTORY_NULL_RESULT = 
			STAFF_INVENTORY 
			+ " AND ei." + DbEmployeeEquipmentInventory.CN_CHECKED + " IS NULL "; 
	
	/*public static final String SERVICE_INVENTORY =
			"SELECT e." + DbEquipment._ID 
					+ ", ei." + DbServiceEquipmentInventory.CN_CHECKED + " "
					+ ", e." + DbEquipment.CN_DESCRIPTION + " "
					+ ", ei." + DbServiceEquipmentInventory.CN_COMMENT + " "
			+ "FROM " + DbServiceEquipment.TABLE_NAME + " ee "
			+ "INNER JOIN " + DbEquipment.TABLE_NAME + " e ON "
				+ "e." + DbEquipment._ID + " = ee." + DbServiceEquipment.CN_EQUIPMENT + " "
			+ "LEFT JOIN " + DbServiceEquipmentInventory.TABLE_NAME + " ei ON "
				+ "e." + DbEquipment._ID + " = ei." + DbServiceEquipmentInventory.CN_EQUIPMENT + " AND "
				+ "ei." + DbServiceEquipmentInventory.CN_SERVICE_INSTANCE + " = ? " 
			+ "WHERE ee." + DbServiceEquipment.CN_SERVICE + " = ? AND ee." + DbServiceEquipment.CN_ACTIVITY_TYPE + " = ?";*/
	
	public static final String SERVICE_INVENTORY =
			"SELECT e." + DbEquipment._ID 
					+ ", ei." + DbServiceEquipmentInventory.CN_CHECKED + " "
					+ ", e." + DbEquipment.CN_DESCRIPTION + " "
					+ ", ei." + DbServiceEquipmentInventory.CN_COMMENT + " "
			+ "FROM " + DbServiceInstance.TABLE_NAME + " si "
			+ "INNER JOIN " + DbServiceEquipment.TABLE_NAME + " ee ON "
					+ "ee." + DbServiceEquipment.CN_SERVICE + " = si." + DbServiceInstance.CN_SERVICE + " AND "
					+ "ee." + DbServiceEquipment.CN_ACTIVITY_TYPE + " = si." + DbServiceInstance.CN_ACTIVITY_TYPE + " "
			+ "INNER JOIN " + DbEquipment.TABLE_NAME + " e ON "
				+ "e." + DbEquipment._ID + " = ee." + DbServiceEquipment.CN_EQUIPMENT + " "
			+ "LEFT JOIN " + DbServiceEquipmentInventory.TABLE_NAME + " ei ON "
				+ "ei." + DbServiceEquipmentInventory.CN_SERVICE_INSTANCE + " = si." + DbServiceInstance._ID + " AND "
				+ "ei." + DbServiceEquipmentInventory.CN_EQUIPMENT + " = e." + DbEquipment._ID + " "
			+ "WHERE si." + DbServiceInstance._ID + " = ?";
	
	
	
	public static final String SERVICE_INVENTORY_NULL_RESULT = 
			SERVICE_INVENTORY 
			+ " AND ei." + DbServiceEquipmentInventory.CN_CHECKED + " IS NULL "; 
	
	public static final String STAFF_REVIEW =
			"SELECT q." + DbQuestion._ID 
					+ ", q." + DbQuestion.CN_DESCRIPTION +", "
					+ "s." + DbSection.CN_DESCRIPTION + " AS " + DbReviewQuestion.CN_SECTION_NAME 
					+ ", ra." + DbReviewQuestionAnswerEmployee.CN_RESULT + " " 
					+ ", ra." + DbReviewQuestionAnswerEmployee.CN_COMMENT + " "
			+ "FROM " + DbServiceInstance.TABLE_NAME + " si "
			+ "INNER JOIN " + DbReviewQuestion.TABLE_NAME + " rq ON "
				+ "si." + DbServiceInstance.CN_SERVICE + " = rq." + DbReviewQuestion.CN_SERVICE + " AND "
				+ "si." + DbServiceInstance.CN_ACTIVITY_TYPE + " = rq." + DbReviewQuestion.CN_ACTIVITY_TYPE + " AND "
				+ "rq." + DbReviewQuestion.CN_TYPE + " = '" + DbReviewQuestion.Types.EMPLOYEE + "' "
			+ "INNER JOIN " + DbQuestion.TABLE_NAME + " q ON "
				+ "q." + DbQuestion._ID + " = rq." + DbReviewQuestion.CN_QUESTION + " "
			+ "INNER JOIN " + DbSection.TABLE_NAME + " s ON "
				+ "s." + DbSection._ID + " = rq." + DbReviewQuestion.CN_SECTION + " "
			+ "LEFT JOIN " + DbReviewQuestionAnswerEmployee.TABLE_NAME + " ra ON "
				+ "ra." + DbReviewQuestionAnswerEmployee.CN_SERVICE_INSTANCE + " = si." + DbServiceInstance._ID + " AND "
				+ "q." + DbQuestion._ID + " = ra." + DbReviewQuestionAnswerEmployee.CN_QUESTION + " AND "
				+ "ra." +  DbReviewQuestionAnswerEmployee.CN_EMPLOYEE + " = ? "
			+ " WHERE si." + DbServiceInstance._ID + " = ? ";
	
	public static final String STAFF_REVIEW_NULL_RESULT =
			STAFF_REVIEW + 
			" AND ra." + DbReviewQuestionAnswerEmployee.CN_RESULT + " IS NULL";
	
	public static final String SERVICE_REVIEW =
			"SELECT q." + DbQuestion._ID 
				+ ", q." + DbQuestion.CN_DESCRIPTION +", "
				+ "s." + DbSection.CN_DESCRIPTION + " AS " + DbReviewQuestion.CN_SECTION_NAME 
				+ ", ra." + DbReviewQuestionAnswerService.CN_RESULT + " " 
				+ ", ra." + DbReviewQuestionAnswerService.CN_COMMENT + " "
			+ "FROM " + DbServiceInstance.TABLE_NAME + " si "
			+ "INNER JOIN " + DbReviewQuestion.TABLE_NAME + " rq ON "
				+ "si." + DbServiceInstance.CN_SERVICE + " = rq." + DbReviewQuestion.CN_SERVICE + " AND "
				+ "si." + DbServiceInstance.CN_ACTIVITY_TYPE + " = rq." + DbReviewQuestion.CN_ACTIVITY_TYPE + " AND "
				+ "rq." + DbReviewQuestion.CN_TYPE + " = '" + DbReviewQuestion.Types.SERVICE + "' "
			+ "INNER JOIN " + DbQuestion.TABLE_NAME + " q ON "
				+ "q." + DbQuestion._ID + " = rq." + DbReviewQuestion.CN_QUESTION + " "
			+ "INNER JOIN " + DbSection.TABLE_NAME + " s ON "
				+ "s." + DbSection._ID + " = rq." + DbReviewQuestion.CN_SECTION + " "
			+ "LEFT JOIN " + DbReviewQuestionAnswerService.TABLE_NAME + " ra ON "
				+ "ra." + DbReviewQuestionAnswerService.CN_SERVICE_INSTANCE + " = si." + DbServiceInstance._ID + " AND "
				+ "q." + DbQuestion._ID + " = ra." + DbReviewQuestionAnswerService.CN_QUESTION + " "
			+ " WHERE si." + DbServiceInstance._ID + " = ? ";
	
	public static final String SERVICE_REVIEW_NULL_RESULT =
			SERVICE_REVIEW + 
			" AND ra." + DbReviewQuestionAnswerService.CN_RESULT + " IS NULL";
	
	public static final String STAFF_SURVEY = 
			"SELECT q." + DbQuestion._ID 
				+ ", q." + DbQuestion.CN_DESCRIPTION +", "
				+ "CASE WHEN sa." + DbSurveyQuestionAnswer.CN_RESULT + " IS NULL "
				+ "THEN '' ELSE sa." + DbSurveyQuestionAnswer.CN_RESULT + " "
				+ "END AS " + DbSurveyQuestionAnswer.CN_RESULT + ", "
				+ "CASE WHEN sa." + DbSurveyQuestionAnswer.CN_COMMENT + " IS NULL "
				+ "THEN '' ELSE sa." + DbSurveyQuestionAnswer.CN_COMMENT + " "
				+ "END AS " + DbSurveyQuestionAnswer.CN_COMMENT + " "
			+ "FROM " + DbServiceInstance.TABLE_NAME + " si "
			+ "INNER JOIN " + DbSurveyQuestion.TABLE_NAME + " sq ON "			
				+ "sq." + DbSurveyQuestion.CN_SERVICE + " = si." + DbServiceInstance.CN_SERVICE + " AND "
				+ "sq." + DbSurveyQuestion.CN_ACTIVITY_TYPE + " = si." + DbServiceInstance.CN_ACTIVITY_TYPE + " "
			+ "INNER JOIN " + DbQuestion.TABLE_NAME + " q ON "
				+ "q." + DbQuestion._ID + " = sq." + DbSurveyQuestion.CN_QUESTION + " "
			+ "LEFT JOIN " + DbSurveyQuestionAnswer.TABLE_NAME + " sa ON "
				+ "q." + DbQuestion._ID + " = sa." + DbSurveyQuestionAnswer.CN_QUESTION + " AND "
				+ "si." + DbServiceInstance._ID + " = sa." + DbSurveyQuestionAnswer.CN_SERVICE_INSTANCE + " AND "
				+ "sa." +  DbSurveyQuestionAnswer.CN_EMPLOYEE + " = ? "
			+ " WHERE si." + DbServiceInstance._ID + " = ?";
	
	public static final String STAFF_SURVEY_NULL_RESULT =
			STAFF_SURVEY + 
			" AND sa." + DbSurveyQuestionAnswer.CN_RESULT + " IS NULL";
	
	public static final String GET_ADDRESS = 
			"SELECT a.*, s." + DbState.CN_NAME + " AS " + DbAddress.CN_STATE_NAME + ", "
					+ "t." + DbTown.CN_NAME + " AS " + DbAddress.CN_TOWN_NAME + " "
			+ "FROM " + DbAddress.TABLE_NAME + " a "
			+ "INNER JOIN " + DbState.TABLE_NAME + " s ON "
					+ "s." + DbState._ID + " = a." + DbAddress.CN_STATE + " "
			+ "INNER JOIN " + DbTown.TABLE_NAME + " t ON "
					+ "t." + DbTown._ID + " = a." + DbAddress.CN_TOWN + " "
			+ "WHERE a." + DbAddress._ID + " = ?";

	public static final String EMPLOYEE_INVENTORY_FAULT =
			"SELECT e." + DbEquipment.CN_DESCRIPTION + ", "
				+ " ei."+ DbEmployeeEquipmentInventory.CN_COMMENT + ", "
				+ " ei."+ DbEmployeeEquipmentInventory.CN_CHECKED
			+ " FROM " + DbEmployeeEquipmentInventory.TABLE_NAME + " ei "
			+ "INNER JOIN " + DbEquipment.TABLE_NAME + " e ON "
				+ "e." + DbEquipment._ID + " = ei." + DbEmployeeEquipmentInventory.CN_EQUIPMENT + " "
			+ "WHERE ei." + DbEmployeeEquipmentInventory.CN_EMPLOYEE + " = ? AND "
					+ "(ei." + DbEmployeeEquipmentInventory.CN_CHECKED + " = '0' OR "
							+ "ei." + DbEmployeeEquipmentInventory.CN_COMMENT + " IS NOT NULL) ";

	public static final String EMPLOYEE_REVIEW_SUMMARY =
			STAFF_REVIEW 
			+ " AND (ra." + DbReviewQuestionAnswerEmployee.CN_RESULT + " = 'B'"
			+ " OR ra." + DbReviewQuestionAnswerEmployee.CN_COMMENT + " IS NOT NULL)"
			+ " ORDER BY s." + DbSection.CN_DESCRIPTION ;

	public static final String EMPLOYEE_SURVEY_SUMMARY =
			STAFF_SURVEY
			+ " AND (sa." + DbSurveyQuestionAnswer.CN_RESULT + " IS NOT NULL"
			+ " OR sa." + DbSurveyQuestionAnswer.CN_COMMENT + " IS NOT NULL)";
	
	public static final String SERVICE_INVENTORY_FAULT = 
			SERVICE_INVENTORY 
			+ " AND (ei." + DbServiceEquipmentInventory.CN_CHECKED + " = '0' OR "
			+ "ei." + DbServiceEquipmentInventory.CN_COMMENT + " IS NOT NULL) ";
	
	public static final String SERVICE_REVIEW_SUMMARY = 
			SERVICE_REVIEW
			+ " AND (ra." + DbReviewQuestionAnswerService.CN_RESULT + " = 'B'"
			+ " OR ra." + DbReviewQuestionAnswerService.CN_COMMENT + " IS NOT NULL)"
			+ " ORDER BY s." + DbSection.CN_DESCRIPTION ;
	
	public static final String EMPLOYEES_SERVICE_FINISHED = 
			EMPLOYEES_BY_SERVICE
			+ " AND e." + DbEmployee.CN_STATUS + " = '" + DbEmployee.EmployeeStatus.FINALIZED + "'";

	/*public static final String GET_ACTIVITIES = 
			"SELECT DISTINCT "
					+ DbServiceConfiguration.CN_ACTIVITY_TYPE +", "
					+ DbServiceConfiguration.CN_TITLE +
			" FROM "+ DbServiceConfiguration.TABLE_NAME;*/

}

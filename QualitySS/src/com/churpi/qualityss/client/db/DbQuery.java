package com.churpi.qualityss.client.db;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbAddress;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbHREmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbRequisition;
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
import com.churpi.qualityss.client.db.QualitySSDbContract.DbWarning;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbWarningDetail;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbWarningReason;

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
			"SELECT "
				+ "ei." + DbEmployeeInstance._ID + ", "
				+ "ei." + DbEmployeeInstance.CN_BARCODECHECK + ", "
				+ "e." + DbEmployee._ID + " AS " + DbEmployeeInstance.CN_EMPLOYEE +", "
				+ "ei." + DbEmployeeInstance.CN_INVENTORY_COMMENT + ", "
				+ "ei." + DbEmployeeInstance.CN_REVIEW_COMMENT + ", "
				+ "si." + DbServiceInstance._ID + " AS " + DbEmployeeInstance.CN_SERVICE_INSTANCE + ", "
				+ "ei." + DbEmployeeInstance.CN_STATUS + ", "
				+ "ei." + DbEmployeeInstance.CN_SURVEY_COMMENT + ", "
				+ "e." + DbEmployee.CN_CODE + ", "
				+ "e." + DbEmployee.CN_NAME + ", "
				+ "e." + DbEmployee.CN_PLATE + " "
			+ "FROM " + DbServiceInstance.TABLE_NAME + " si " 
			+ "INNER JOIN " + DbServiceEmployee.TABLE_NAME + " se ON " 
				+ "se." + DbServiceEmployee.CN_SERVICE + " = si." + DbServiceInstance.CN_SERVICE + " "
			+ "INNER JOIN " + DbEmployee.TABLE_NAME + " e ON "
				+ "e." + DbEmployee._ID + " = se." + DbServiceEmployee.CN_EMPLOYEE + " "
			+ "LEFT JOIN " + DbEmployeeInstance.TABLE_NAME + " ei ON "
				+ "ei." + DbEmployeeInstance.CN_SERVICE_INSTANCE + " = si." + DbServiceInstance._ID + " AND "
				+ "ei." + DbEmployeeInstance.CN_EMPLOYEE + " = e." + DbEmployee._ID + " "
			+ "WHERE si." + DbServiceInstance._ID + " = ? ";
	
	public static final String EMPLOYEES_SERVICE_NOT_END = 
			EMPLOYEES_BY_SERVICE
			+ "AND ei." + DbEmployeeInstance.CN_STATUS + " IS NOT NULL "
			+ "AND ei." + DbEmployeeInstance.CN_STATUS + " <> '" + DbEmployeeInstance.EmployeeStatus.FINALIZED + "'";
	
	public static final String STAFF_INVENTORY = 
			"SELECT e." + DbEquipment._ID 
					+ ", eii." + DbEmployeeEquipmentInventory.CN_CHECKED + " "
					+ ", e." + DbEquipment.CN_DESCRIPTION + " "
					+ ", eii." + DbEmployeeEquipmentInventory.CN_COMMENT + " "
			+ "FROM " + DbEmployeeInstance.TABLE_NAME + " ei "
			+ "INNER JOIN " + DbEmployeeEquipment.TABLE_NAME + " ee ON "
				+ "ee." + DbEmployeeEquipment.CN_EMPLOYEE + " = ei." + DbEmployeeInstance.CN_EMPLOYEE + " "
			+ "INNER JOIN " + DbEquipment.TABLE_NAME + " e ON "
				+ "e." + DbEquipment._ID + " = ee." + DbEmployeeEquipment.CN_EQUIPMENT + " "
			+ "LEFT JOIN " + DbEmployeeEquipmentInventory.TABLE_NAME + " eii ON "
				+ "e." + DbEquipment._ID + " = eii." + DbEmployeeEquipmentInventory.CN_EQUIPMENT + " AND "
				+ "eii." + DbEmployeeEquipmentInventory.CN_EMPLOYEE_INSTANCE + " = ei." + DbEmployeeInstance._ID
			+ " WHERE ei." + DbEmployeeInstance._ID + " = ?";
	
	public static final String STAFF_INVENTORY_NULL_RESULT = 
			STAFF_INVENTORY 
			+ " AND eii." + DbEmployeeEquipmentInventory.CN_CHECKED + " IS NULL "; 
		
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
			+ "FROM " + DbEmployeeInstance.TABLE_NAME + " ei " 
			+ "INNER JOIN " + DbServiceInstance.TABLE_NAME + " si ON "
				+ "si." + DbServiceInstance._ID + " = ei." + DbEmployeeInstance.CN_SERVICE_INSTANCE + " "
			+ "INNER JOIN " + DbReviewQuestion.TABLE_NAME + " rq ON "
				+ "si." + DbServiceInstance.CN_SERVICE + " = rq." + DbReviewQuestion.CN_SERVICE + " AND "
				+ "si." + DbServiceInstance.CN_ACTIVITY_TYPE + " = rq." + DbReviewQuestion.CN_ACTIVITY_TYPE + " AND "
				+ "rq." + DbReviewQuestion.CN_TYPE + " = '" + DbReviewQuestion.Types.EMPLOYEE + "' "
			+ "INNER JOIN " + DbQuestion.TABLE_NAME + " q ON "
				+ "q." + DbQuestion._ID + " = rq." + DbReviewQuestion.CN_QUESTION + " "
			+ "INNER JOIN " + DbSection.TABLE_NAME + " s ON "
				+ "s." + DbSection._ID + " = rq." + DbReviewQuestion.CN_SECTION + " "
			+ "LEFT JOIN " + DbReviewQuestionAnswerEmployee.TABLE_NAME + " ra ON "
				+ "ra." + DbReviewQuestionAnswerEmployee.CN_EMPLOYEE_INSTANCE + " = ei." + DbEmployeeInstance._ID + " AND "
				+ "q." + DbQuestion._ID + " = ra." + DbReviewQuestionAnswerEmployee.CN_QUESTION + " "
			+ " WHERE si." + DbEmployeeInstance._ID + " = ? ";
	
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
			+ "FROM " + DbEmployeeInstance.TABLE_NAME + " ei "
			+ "INNER JOIN " + DbServiceInstance.TABLE_NAME + " si ON "
				+ "si." + DbServiceInstance._ID + " = ei." + DbEmployeeInstance.CN_SERVICE_INSTANCE + " "
			+ "INNER JOIN " + DbSurveyQuestion.TABLE_NAME + " sq ON "			
				+ "sq." + DbSurveyQuestion.CN_SERVICE + " = si." + DbServiceInstance.CN_SERVICE + " AND "
				+ "sq." + DbSurveyQuestion.CN_ACTIVITY_TYPE + " = si." + DbServiceInstance.CN_ACTIVITY_TYPE + " "
			+ "INNER JOIN " + DbQuestion.TABLE_NAME + " q ON "
				+ "q." + DbQuestion._ID + " = sq." + DbSurveyQuestion.CN_QUESTION + " "
			+ "LEFT JOIN " + DbSurveyQuestionAnswer.TABLE_NAME + " sa ON "
				+ "q." + DbQuestion._ID + " = sa." + DbSurveyQuestionAnswer.CN_QUESTION + " AND "
				+ "ei." + DbEmployeeInstance._ID + " = sa." + DbSurveyQuestionAnswer.CN_EMPLOYEE_INSTANCE + " "
			+ " WHERE ei." + DbEmployeeInstance._ID + " = ?";
	
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
			STAFF_INVENTORY
					+ " AND (eii." + DbEmployeeEquipmentInventory.CN_CHECKED + " = '0' OR "
						+ "eii." + DbEmployeeEquipmentInventory.CN_CHECKED + " IS NULL OR "
						+ "eii." + DbEmployeeEquipmentInventory.CN_COMMENT + " IS NOT NULL) ";

	public static final String GET_EMPLOYEE_DATA = 
			"SELECT ei.*, "
			+ "e." + DbEmployee.CN_CODE + ", "
			+ "e." + DbEmployee.CN_NAME + ", "
			+ "e." + DbEmployee.CN_PLATE + " "
			+ "FROM " + DbEmployeeInstance.TABLE_NAME + " ei "
			+ "INNER JOIN " + DbEmployee.TABLE_NAME + " e ON "
				+ "e." + DbEmployee._ID + " = ei." + DbEmployeeInstance.CN_EMPLOYEE + " "
			+ "WHERE ei." + DbEmployeeInstance._ID + " = ?";

	
	public static final String EMPLOYEE_REVIEW_SUMMARY =
			STAFF_REVIEW 
			+ " AND (ra." + DbReviewQuestionAnswerEmployee.CN_RESULT + " = 'B'"
			+ " OR ra." + DbReviewQuestionAnswerEmployee.CN_RESULT + " IS NULL "
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
			+ " AND ei." + DbEmployeeInstance.CN_STATUS + " = '" + DbEmployeeInstance.EmployeeStatus.FINALIZED + "'";

	public static final String GET_HR_EMPLOYEES = 
			"SELECT e.* "
			+ "FROM " + DbHREmployee.TABLE_NAME + " h "
			+ "INNER JOIN " + DbEmployee.TABLE_NAME + " e ON e." + DbEmployee._ID + " = h." + DbHREmployee._ID;

	public static final String GET_REQUISITION_LIST = 
			"SELECT "
				+ "r." + DbRequisition._ID + ","
				+ "substr(r." + DbRequisition.CN_START_DATE + ",7,2)||'/'||"
					+ "substr(r." + DbRequisition.CN_START_DATE + ",5,2)||'/'||"
					+ "substr(r." + DbRequisition.CN_START_DATE + ",1,4) "
				+ "AS " + DbRequisition.CN_START_DATE + ","
				+ "CASE r." + DbRequisition.CN_STATUS + " "
					+ "WHEN 0 THEN 'Abierto' "
					+ "WHEN 1 THEN 'En proceso' "
					+ "WHEN 2 THEN 'Cerrado' "
				+ "END AS " + DbRequisition.CN_STATUS + ","
				+ "r." + DbRequisition.CN_AGREEMENT + ","
				+ "e." + DbEmployee.CN_NAME + " "
			+ "FROM " + DbRequisition.TABLE_NAME + " r "
			+ "INNER JOIN " + DbEmployee.TABLE_NAME + " e ON e." + DbEmployee._ID + " = r." + DbRequisition.CN_ASSIGN_EMPLOYEE + " "
			+ "WHERE r." + DbRequisition.CN_SERVICE + " = ?";
	
	public static final String GET_REQUISITION_TO_SEND = 
			"SELECT "
				+ "r.*"
			+ "FROM " + DbRequisition.TABLE_NAME + " r "
			+ "WHERE r." + DbRequisition.CN_SENT + " IS NULL OR r." + DbRequisition.CN_SENT + " = 0";

	public static final String GET_WARNING_LIST = 
			"SELECT "
				+ "w." + DbWarning._ID + ","
				+ "w." + DbWarning.CN_CREATION_DATE + ","
				+ "wr." + DbWarningReason.CN_DESCRIPTION + " AS " + DbWarningDetail.CN_WARNING_REASON + ", "
				+ "w." + DbWarning.CN_NOTE + " "
			+ "FROM " + DbWarning.TABLE_NAME + " w "
			+ "INNER JOIN " + DbWarningDetail.TABLE_NAME + " wd ON wd." + DbWarningDetail.CN_WARNING + " = w." + DbWarning._ID + " "
			+ "INNER JOIN " + DbWarningReason.TABLE_NAME + " wr ON wr." + DbWarningReason._ID + " = wd." + DbWarningDetail.CN_WARNING_REASON + " "
			+ "WHERE w." + DbWarning.CN_EMPLOYEE_INSTANCE + " = ?";

	public static final String GET_WARNING_REASON_LIST = 
			"SELECT "
				+ "wr." + DbWarningReason._ID + ", "
				+ "wr." + DbWarningReason.CN_DESCRIPTION + ", "
				+ "wd." + DbWarningDetail.CN_NOTE + ", "
				+ "wd." + DbWarningDetail._ID + " AS " + DbWarningDetail.CN_WARNING_REASON + " "
			+ "FROM " + DbWarningReason.TABLE_NAME + " wr "
			+ "LEFT JOIN " + DbWarningDetail.TABLE_NAME + " wd ON wd." + DbWarningDetail.CN_WARNING_REASON + " = wr." + DbWarningReason._ID + " AND wd." + DbWarningDetail.CN_WARNING + " = ?";
	
	public static final String ALL_EMPLOYEES = 
			"SELECT "
				+ "0 AS " + DbEmployeeInstance._ID + ", "
				+ "0 AS " + DbEmployeeInstance.CN_BARCODECHECK + ", "
				+ "e." + DbEmployee._ID + " AS " + DbEmployeeInstance.CN_EMPLOYEE +", "
				+ "'' AS " + DbEmployeeInstance.CN_INVENTORY_COMMENT + ", "
				+ "'' AS " + DbEmployeeInstance.CN_REVIEW_COMMENT + ", "
				+ "'' AS  " + DbEmployeeInstance.CN_SERVICE_INSTANCE + ", "
				+ "NULL AS " + DbEmployeeInstance.CN_STATUS + ", "
				+ "'' AS  " + DbEmployeeInstance.CN_SURVEY_COMMENT + ", "
				+ "e." + DbEmployee.CN_CODE + ", "
				+ "e." + DbEmployee.CN_NAME + ", "
				+ "e." + DbEmployee.CN_PLATE + " "
			+ "FROM " + DbEmployee.TABLE_NAME + " e "
			+ "ORDER BY e." + DbEmployee.CN_NAME;
}


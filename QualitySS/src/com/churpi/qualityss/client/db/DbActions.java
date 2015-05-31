package com.churpi.qualityss.client.db;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.churpi.qualityss.Config;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbImageToSend;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance.ServiceStatus;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestionAnswer;
import com.churpi.qualityss.client.dto.ServiceInstanceDTO;
import com.churpi.qualityss.client.helper.DateHelper;

public class DbActions {
	public static boolean checkClearService(Context context, ServiceInstanceDTO serviceInstance) {
		if (serviceInstance.getStatus() == null || serviceInstance.getStatus().compareTo(ServiceStatus.CURRENT) == 0) {
			return true;
		}
		long diff = 0;
		Date sentDate = DateHelper.getDateFromDb(serviceInstance.getFechaFin());
		Calendar cal = Calendar.getInstance();
		cal.setTime(sentDate);
		cal.add(Calendar.HOUR_OF_DAY, Config.HOURS_TO_RESET_SENT_SERVICE);
		diff = cal.getTime().getTime()
				- Calendar.getInstance().getTime().getTime();
		if (diff <= 0) {
			serviceInstance.setStatus(null);

			return (Boolean)DbTrans.write(context, serviceInstance, new DbTrans.Db() {
				@Override
				public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
					ServiceInstanceDTO si = (ServiceInstanceDTO)parameter;

					String[] whereArgs = new String[]{ String.valueOf(si.getServicioInstanciaId())};

					db.delete(DbServiceEquipmentInventory.TABLE_NAME, 
							DbServiceEquipmentInventory.CN_SERVICE_INSTANCE + " = ?", 
							whereArgs);

					db.delete(DbReviewQuestionAnswerService.TABLE_NAME, 
							DbReviewQuestionAnswerService.CN_SERVICE_INSTANCE + " = ?", 
							whereArgs);

					Cursor cEmployees = db.query(DbEmployeeInstance.TABLE_NAME, 
							new String[]{ DbEmployeeInstance._ID }, 
							DbEmployeeInstance.CN_SERVICE_INSTANCE + " = ? ", 
							whereArgs, null, null, null); 

					if(cEmployees.moveToFirst()){
						do{
							String[] whereArgsEmp = new String[]{ 
									String.valueOf(cEmployees.getInt(cEmployees.getColumnIndex(DbEmployeeInstance._ID)))};
							db.delete(DbReviewQuestionAnswerEmployee.TABLE_NAME, 
									DbReviewQuestionAnswerEmployee.CN_EMPLOYEE_INSTANCE + " = ?", 
									whereArgsEmp);

							db.delete(DbEmployeeEquipmentInventory.TABLE_NAME, 
									DbEmployeeEquipmentInventory.CN_EMPLOYEE_INSTANCE + "=?",
									whereArgsEmp);

							db.delete(DbSurveyQuestionAnswer.TABLE_NAME, 
									DbSurveyQuestionAnswer.CN_EMPLOYEE_INSTANCE + " = ?", 
									whereArgsEmp);
						}while(cEmployees.moveToNext());
					}

					db.delete(DbEmployeeInstance.TABLE_NAME, 
							DbEmployeeInstance.CN_SERVICE_INSTANCE + " = ? ",
							whereArgs);
					
					db.delete(DbServiceInstance.TABLE_NAME, 
							DbServiceInstance._ID + " = ? ",
							whereArgs);

					return true;
				}
			});
		}
		return false;

	}
	public static void deleteImageAndFromQueue(Context context, File file){
		DbTrans.write(context, file, new DbTrans.Db() {

			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				File file = (File)parameter;
				db.delete(DbImageToSend.TABLE_NAME, DbImageToSend.CN_URL + " = ?",
						new String[]{ file.getAbsolutePath() });
				if(file.exists()){
					file.delete();
				}
				return null;
			}
		});
	}
}

package com.churpi.qualityss.client.db;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.churpi.qualityss.Config;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbImageToSend;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEmployee;
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
					/*String serviceEmployees = 
							"SELECT " + DbServiceEmployee.CN_EMPLOYEE + 
							" FROM " + DbServiceEmployee.TABLE_NAME + 
							" WHERE " + DbServiceEmployee.CN_SERVICE + " = ?";*/
					
					String[] whereArgs = new String[]{ String.valueOf(si.getServicioInstanciaId())};
					
					db.delete(DbServiceEquipmentInventory.TABLE_NAME, 
							DbServiceEquipmentInventory.CN_SERVICE_INSTANCE + " = ?", 
							whereArgs);

					db.delete(DbReviewQuestionAnswerEmployee.TABLE_NAME, 
							DbReviewQuestionAnswerEmployee.CN_SERVICE_INSTANCE + " = ?", 
							whereArgs);

					db.delete(DbReviewQuestionAnswerService.TABLE_NAME, 
							DbReviewQuestionAnswerService.CN_SERVICE_INSTANCE + " = ?", 
							whereArgs);

					db.delete(DbSurveyQuestionAnswer.TABLE_NAME, 
							DbSurveyQuestionAnswer.CN_SERVICE_INSTANCE + " = ?", 
							whereArgs);

					db.delete(DbServiceInstance.TABLE_NAME, 
							DbServiceInstance._ID + " = ? ",
							whereArgs);
					/*db.execSQL(
							"DELETE FROM " + DbServiceEquipmentInventory.TABLE_NAME +
							" WHERE " + DbServiceEquipmentInventory.CN_SERVICE_INSTANCE + " = ?", new Object[]{ si.getServicioInstanciaId() });
					db.execSQL(
							"DELETE FROM " + DbReviewQuestionAnswerEmployee.TABLE_NAME + 
							" WHERE " + DbReviewQuestionAnswerEmployee.CN_SERVICE_INSTANCE + " = ?", new Object[]{ si.getServicioInstanciaId() });
					db.execSQL(
							"DELETE FROM " + DbReviewQuestionAnswerService.TABLE_NAME + 
							" WHERE " + DbReviewQuestionAnswerService.CN_SERVICE_INSTANCE + " = ?", new Object[]{ si.getServicioInstanciaId() });
					db.execSQL(
							"DELETE FROM " + DbSurveyQuestionAnswer.TABLE_NAME + 
							" WHERE " + DbSurveyQuestionAnswer.CN_SERVICE_INSTANCE + "= ?", new Object[]{ si.getServicioInstanciaId() });
					db.execSQL(
							"DELETE FROM " + DbEmployeeEquipmentInventory.TABLE_NAME + 
							" WHERE " + DbEmployeeEquipmentInventory.CN_EMPLOYEE + " in ("+ serviceEmployees +")", new Object[]{ si.getServicioId() });
					
					ContentValues eVal = new ContentValues();
					eVal.putNull(DbEmployee.CN_STATUS);
					eVal.putNull(DbEmployee.CN_BARCODECHECK);
					eVal.putNull(DbEmployee.CN_INVENTORY_COMMENT);
					eVal.putNull(DbEmployee.CN_REVIEW_COMMENT);
					eVal.putNull(DbEmployee.CN_SURVEY_COMMENT);
					db.update(DbEmployee.TABLE_NAME, eVal, 
							DbEmployee._ID + " in ("+ serviceEmployees +")", 
							new String[]{ String.valueOf(si.getServicioId())});
										
					db.execSQL(
							"DELETE FROM " + DbServiceInstance.TABLE_NAME + 
							" WHERE " + DbServiceInstance._ID + " = ? ", new Object[]{ si.getServicioInstanciaId() });
					
					File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
					for(File file :dir.listFiles()){
						String[] parts = file.getName().split("_");
						if(parts[0]== si.getKey()){
							file.delete();
						}
					}*/
					
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

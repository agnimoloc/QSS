package com.churpi.qualityss.client.db;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.churpi.qualityss.Config;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService.ServiceStatus;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswer;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestionAnswer;
import com.churpi.qualityss.client.dto.ServiceDTO;
import com.churpi.qualityss.client.helper.DateHelper;

public class DbActions {
	public static boolean checkClearService(Context context, ServiceDTO service) {
		if (service.getStatus() == null || service.getStatus().compareTo(ServiceStatus.CURRENT) == 0) {
			return true;
		}
		long diff = 0;
		Date sentDate = DateHelper.getDateFromDb(service.getFechaRevision());
		Calendar cal = Calendar.getInstance();
		cal.setTime(sentDate);
		cal.add(Calendar.HOUR_OF_DAY, Config.HOURS_TO_RESET_SENT_SERVICE);
		diff = cal.getTime().getTime()
				- Calendar.getInstance().getTime().getTime();
		if (diff <= 0) {
			service.setStatus(null);
			final int serviceID = service.getServicioId();
			return (Boolean)DbTrans.write(context, new DbTrans.Db() {
				@Override
				public Object onDo(Context context, SQLiteDatabase db) {
					String serviceEmployees = 
							"SELECT " + DbServiceEmployee.CN_EMPLOYEE + 
							" FROM " + DbServiceEmployee.TABLE_NAME + 
							" WHERE " + DbServiceEmployee.CN_SERVICE + " = ?";
					db.execSQL(
							"DELETE FROM " + DbServiceEquipmentInventory.TABLE_NAME +
							" WHERE " + DbServiceEquipmentInventory.CN_SERVICE + " = ?", new Object[]{ serviceID });
					db.execSQL(
							"DELETE FROM " + DbReviewQuestionAnswer.TABLE_NAME + 
							" WHERE " + DbReviewQuestionAnswer.CN_SERVICE + " = ?", new Object[]{ serviceID });
					db.execSQL(
							"DELETE FROM " + DbSurveyQuestionAnswer.TABLE_NAME + 
							" WHERE " + DbSurveyQuestionAnswer.CN_SERVICE + "= ?", new Object[]{ serviceID });
					db.execSQL(
							"DELETE FROM " + DbEmployeeEquipmentInventory.TABLE_NAME + 
							" WHERE " + DbEmployeeEquipmentInventory.CN_EMPLOYEE + " in ("+ serviceEmployees +")", new Object[]{ serviceID });
					
					db.execSQL(
							"UPDATE " + DbEmployee.TABLE_NAME + 
							" SET " + DbEmployee.CN_STATUS + " = NULL " +
							" WHERE " + DbEmployee._ID + " in ("+ serviceEmployees +")", new Object[]{ serviceID });					
					db.execSQL(
							"UPDATE " + DbService.TABLE_NAME + 
							" SET " + DbService.CN_STATUS + " = NULL " +
							" WHERE " + DbService._ID + " = ? ", new Object[]{ serviceID });
					return true;
				}
			});
		}
		return false;
	}
}

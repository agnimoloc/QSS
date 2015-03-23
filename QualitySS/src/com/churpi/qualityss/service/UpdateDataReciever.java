package com.churpi.qualityss.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.churpi.qualityss.Config;
import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswer;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestionAnswer;
import com.churpi.qualityss.client.helper.DateHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.widget.Toast;

public class UpdateDataReciever extends BroadcastReceiver {


	AlarmManager alarmManager;
	PendingIntent pi;
	Context mContext;
	
	private final String JSON_LIST_SERVICES = "Servicios";
	private final String JSON_SERVICE_ID = "ServicioId";
	private final String JSON_LIST_EMPLOYEES = "EvaluacionElemento";
	private final String JSON_EMPLOYEE_ID = "ElementoId";
	private final String JSON_SERVICE_DATE = "FechaEvaluacion";
	
	public UpdateDataReciever(Context context){
		alarmManager = (AlarmManager)(context.getSystemService( Context.ALARM_SERVICE ));
		pi = PendingIntent.getBroadcast( context, 
				0,new Intent(Constants.UPDATE_DATA_ACTION),0 );
	}

	public void start(){
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
				SystemClock.elapsedRealtime() + Config.REFRESH_TIME, pi);
	}

	public void dispose(){
		alarmManager.cancel(pi);
	}


	public void onReceive(final Context context, Intent intent) {		 

		final JSONObject json = (JSONObject)DbTrans.read(context, new DbTrans.Db() {

			@Override
			public Object onDo(Context context, SQLiteDatabase db) {
				JSONObject json = new JSONObject();
				try {
					JSONArray jServices = new JSONArray();
					Cursor sCursor = db.query(
							DbService.TABLE_NAME, 
							new String[]{ 
									DbService._ID, 
									DbService.CN_DATETIME, 
									DbService.CN_EMPLOYEEREVIEW }, 
									DbService.CN_STATUS + "=?", 
									new String[]{ DbService.ServiceStatus.FINALIZED}, 
									null, null, null);
					if(sCursor.moveToFirst()){
						json.put(JSON_LIST_SERVICES, jServices);
						do{
							JSONObject service = createJSONService(sCursor, db);
							jServices.put(service);
							
						}while(sCursor.moveToNext());
					}
					sCursor.close();
				} catch (JSONException e) {
					Toast.makeText(context, context.getString(R.string.msg_error_json_to_send), Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				return json;
			}
		});

		if(!json.isNull(JSON_LIST_SERVICES)){						
			JsonObjectRequestResponseString request = new JsonObjectRequestResponseString(
					Request.Method.POST,
					Config.getUrl(Config.ServerAction.SEND_DATA), 
					json, 
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject arg0) {
							DbTrans.write(context, new DbTrans.Db() {
								@Override
								public Object onDo(Context context, SQLiteDatabase db) {
									try {
										JSONArray services = json.getJSONArray(JSON_LIST_SERVICES);								
										for(int i = 0; i < services.length(); i++){
											JSONObject service = (JSONObject) services.get(i);
											ContentValues values = new ContentValues();
											values.put(DbService.CN_STATUS, DbService.ServiceStatus.SENT);
											values.putNull(DbService.CN_DATETIME);
											values.put(DbService.CN_LASTREVIEW, DateHelper.fromJSONDate(service.getString(JSON_SERVICE_DATE)));
											
											db.update(DbService.TABLE_NAME, values, 
													DbService._ID + "=?", 
													new String[]{ String.valueOf(service.getInt(JSON_SERVICE_ID))});
											
											JSONArray employees = service.getJSONArray(JSON_LIST_EMPLOYEES);
											for(int j = 0; j < employees.length(); j++){
												JSONObject employee = (JSONObject) employees.get(j);
												DbEmployee.setStatus(db, 
														employee.getInt(JSON_EMPLOYEE_ID), 
														DbEmployee.EmployeeStatus.SENT);
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
									return null;
								}
							});

							PullPushDataService.updateData(context, 
									Constants.getPref(context).getString(Constants.PREF_CHANGESET, null),
									callback);						
						}
					}, 
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							String errorMsg = error.getLocalizedMessage();
							if(errorMsg == null)
								errorMsg = error.getMessage();
							if(errorMsg == null && error instanceof com.android.volley.TimeoutError)
								errorMsg = com.android.volley.TimeoutError.class.getName();
							if(errorMsg == null)
								errorMsg = context.getString(R.string.ttl_error);

							Toast.makeText(context, context.getString(R.string.msg_error_send) + errorMsg, Toast.LENGTH_SHORT).show();
							start();
						}
					});
			VolleySingleton.getInstance(context).addToRequestQueue(request);
		}else{
			PullPushDataService.updateData(context, 
					Constants.getPref(context).getString(Constants.PREF_CHANGESET, null),
					callback);
		}
	}

	PullPushDataService.Callback callback = new PullPushDataService.Callback() {

		@Override
		public void success(Context context) {
			start();
		}

		@Override
		public void error(VolleyError error, Context context) {
			String errorMsg = error.getLocalizedMessage();
			if(errorMsg == null)
				errorMsg = error.getMessage();
			if(errorMsg == null && error instanceof com.android.volley.TimeoutError)
				errorMsg = com.android.volley.TimeoutError.class.getName();
			if(errorMsg == null)
				errorMsg = context.getString(R.string.ttl_error);

			Toast.makeText(context, context.getString(R.string.msg_error_recieved) + errorMsg, Toast.LENGTH_SHORT).show();
			start();
		}
	}; 
	
	private JSONObject createJSONService(Cursor c, SQLiteDatabase db) throws JSONException{
		JSONObject item = new JSONObject();
		int serviceId = c.getInt(c.getColumnIndex(DbService._ID));
		item.put("SupervisorId", c.getInt(c.getColumnIndex(DbService.CN_EMPLOYEEREVIEW)));
		
		item.put(JSON_SERVICE_DATE, DateHelper.getJSONDate(c.getString(c.getColumnIndex(DbService.CN_DATETIME))));
		item.put(JSON_SERVICE_ID, serviceId);
		
		Cursor cInvent = db.rawQuery(DbQuery.SERVICE_INVENTORY, 
				new String[]{ String.valueOf(serviceId) });
		JSONArray inventory = new JSONArray();
		item.put("ResultadosInventario", inventory);
		if(cInvent.moveToFirst()){
			do{
				JSONObject json = new JSONObject();
				json.put("EquipoId", cInvent.getInt(cInvent.getColumnIndex(DbEquipment._ID)));
				json.put("Valido", cInvent.getInt(cInvent.getColumnIndex(DbServiceEquipmentInventory.CN_CHECKED)) == 1);
				inventory.put(json);
			}while(cInvent.moveToNext());
		}
		cInvent.close();

		Cursor cEmployee = db.rawQuery(DbQuery.EMPLOYEES_BY_SERVICE, 
				new String[]{ String.valueOf(serviceId) });
		JSONArray employees = new JSONArray();
		item.put(JSON_LIST_EMPLOYEES, employees);
		if(cEmployee.moveToFirst()){
			do{
				JSONObject json = new JSONObject();
				createJSONEmployee(cEmployee, db, json, serviceId);
				employees.put(json);
			}while(cEmployee.moveToNext());
		}
		cEmployee.close();
		return item;
	}
	
	private void createJSONEmployee(Cursor c, SQLiteDatabase db, JSONObject item, int serviceId) throws JSONException{
		String status = c.getString(c.getColumnIndex(DbEmployee.CN_STATUS));
		
		int employeeId = c.getInt(c.getColumnIndex(DbEmployee._ID));
		item.put(JSON_EMPLOYEE_ID, employeeId);
		if(status == null){
			item.put("Omitido", true);
		}else{
			item.put("Omitido", DbEmployee.EmployeeStatus.FINALIZED.compareTo(status) == 0);
			item.put("IdentificoGafete", c.getInt(c.getColumnIndex(DbEmployee.CN_BARCODECHECK))==1);

			JSONArray questions = new JSONArray();
			item.put("ResultadoPreguntas", questions);

			Cursor cQuestion = db.rawQuery(DbQuery.STAFF_REVIEW, 
					new String[]{ String.valueOf(employeeId),  String.valueOf(serviceId) });
			createQuestions(cQuestion, questions, true);
			cQuestion = db.rawQuery(DbQuery.STAFF_SURVEY, 
					new String[]{ String.valueOf(employeeId),  String.valueOf(serviceId) });
			createQuestions(cQuestion, questions, false);

			Cursor cInvent = db.rawQuery(DbQuery.STAFF_INVENTORY, 
					new String[]{ String.valueOf(employeeId) });
			JSONArray inventory = new JSONArray();
			item.put("ResultadoInventarios", inventory);
			if(cInvent.moveToFirst()){
				do{
					JSONObject json = new JSONObject();
					json.put("EquipoId", cInvent.getInt(cInvent.getColumnIndex(DbEquipment._ID)));
					json.put("Valido", cInvent.getInt(cInvent.getColumnIndex(DbEmployeeEquipmentInventory.CN_CHECKED)) == 1 );
					inventory.put(json);
				}while(cInvent.moveToNext());
			}
			cInvent.close();
		}
	}
	
	private void createQuestions(Cursor c, JSONArray list, boolean resultado) throws JSONException{
		if(c.moveToFirst()){
			do{
				JSONObject json = new JSONObject();
				json.put("PreguntaId", c.getInt(c.getColumnIndex(DbQuestion._ID)));
				if(resultado){
					String result = c.getString(c.getColumnIndex(DbReviewQuestionAnswer.CN_RESULT));
					int intResult = -1;
					if("B".compareTo(result)==0){
						intResult = 1;
					}else if("G".compareTo(result)==0){
						intResult = 2;
					}else if("E".compareTo(result)==0){
						intResult = 3;
					}
					json.put("Resultado", intResult);
					list.put(json);
				}else{
					String response = c.getString(c.getColumnIndex(DbSurveyQuestionAnswer.CN_RESULT));
					if(response != null && response.length() > 0){
						json.put("Respuesta", c.getString(c.getColumnIndex(DbSurveyQuestionAnswer.CN_RESULT)));
						list.put(json);
					}
				}				
			}while(c.moveToNext());
		}
		c.close();
	}
}

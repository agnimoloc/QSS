package com.churpi.qualityss.service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.churpi.qualityss.Config;
import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbImageToSend;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestionAnswer;
import com.churpi.qualityss.client.dto.EmployeeDTO;
import com.churpi.qualityss.client.dto.ImageSendDTO;
import com.churpi.qualityss.client.dto.ServiceInstanceDTO;
import com.churpi.qualityss.client.helper.DateHelper;
import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.SystemClock;
import android.widget.Toast;

public class UpdateDataReciever extends BroadcastReceiver {

	
	private static UpdateDataReciever INSTANCE = null;

	AlarmManager alarmManager;
	PendingIntent pi;
	Context mContext;
	
	private final String JSON_LIST_SERVICES = "Servicios";
	private final String JSON_LIST_EMPLOYEES = "EvaluacionElemento";
	private final String JSON_EMPLOYEE_ID = "ElementoId";
	private final String JSON_SERVICE_INSTANCE_KEY = "Key";
	
	public static synchronized void createInstance(Context context){
		if(INSTANCE == null){
			INSTANCE = new UpdateDataReciever(context);
		}
	}
	
	public static UpdateDataReciever getInstance(){
		return INSTANCE;
	}
	
	public UpdateDataReciever(Context context){
		alarmManager = (AlarmManager)(context.getSystemService( Context.ALARM_SERVICE ));
		pi = PendingIntent.getBroadcast( context, 
				0,new Intent(Constants.UPDATE_DATA_ACTION),0 );
		mContext = context;
	}

	public void start(){
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
				SystemClock.elapsedRealtime() + Config.REFRESH_TIME, pi);
	}
	
	public void force(){
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
				SystemClock.elapsedRealtime() - 1, pi);
	}

	public void dispose(){
		alarmManager.cancel(pi);
	}


	public void onReceive(final Context context, Intent intent) {		 

		
		/*DbTrans.write(context, dir, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				File dir = (File)parameter;
				
					Cursor sCursor = db.query(
							DbServiceInstance.TABLE_NAME, 
							new String[]{DbServiceInstance.CN_KEY},
							DbServiceInstance.CN_STATUS + "=?", 
							new String[]{ DbServiceInstance.ServiceStatus.FINALIZED}, 
							null, null, null);
					if(sCursor.moveToFirst()){						
						do{
							
							
						}while(sCursor.moveToNext());
					}
					sCursor.close();
					
					
				return null;
			}
		});*/

		
		/*for(File file :dir.listFiles()){
			String[] parts = file.getName().split("_");
			if(keys.contains(parts[0])){
				
				 file.getAbsolutePath()				
				MultipartRequest<ImageSendDTO> fileRequest = new MultipartRequest<ImageSendDTO>(
						Config.getUrl(Config.ServerAction.SEND_FILE), 
						file, 
						ImageSendDTO.class, 
						null, //headers 
						new Response.Listener<ImageSendDTO>() {
							@Override
							public void onResponse(ImageSendDTO arg0) {
								arg0.getFile().delete();
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
							}
						});
				fileRequest.setRetryPolicy(new DefaultRetryPolicy(
						Config.SERVER_GET_DATA_TIMEOUT, 
						DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
						DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
				VolleySingleton.getInstance(context).addToRequestQueue(fileRequest);
				
			}
		}*/
		
		
		final JSONObject json = (JSONObject)DbTrans.read(context, new DbTrans.Db() {

			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				JSONObject json = new JSONObject();
				try {
					JSONArray jServices = new JSONArray();
					Cursor sCursor = db.query(
							DbServiceInstance.TABLE_NAME, 
							null,
							DbServiceInstance.CN_STATUS + "=?", 
							new String[]{ DbServiceInstance.ServiceStatus.FINALIZED}, 
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
								public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
									try {
										JSONArray services = json.getJSONArray(JSON_LIST_SERVICES);
										File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

										for(int i = 0; i < services.length(); i++){
											JSONObject service = (JSONObject) services.get(i);
											ContentValues values = new ContentValues();
											values.put(DbServiceInstance.CN_STATUS, DbServiceInstance.ServiceStatus.SENT);
											String key = service.getString(JSON_SERVICE_INSTANCE_KEY); 
											db.update(DbServiceInstance.TABLE_NAME, values, 
													DbServiceInstance.CN_KEY + "=?", 
													new String[]{ key });
											
											sendImages(key, db, dir);
											
											JSONArray employees = service.getJSONArray(JSON_LIST_EMPLOYEES);
											for(int j = 0; j < employees.length(); j++){
												JSONObject employee = (JSONObject) employees.get(j);
												/*DbEmployee.setStatus(db, 
														employee.getInt(JSON_EMPLOYEE_ID), 
														DbEmployee.EmployeeStatus.SENT);*/
												DbEmployee.clean(db, 
														employee.getInt(JSON_EMPLOYEE_ID));
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
									return null;
								}
							});

							PullPushDataService.updateData(context, 
									Ses.getInstance(context).getChangeset(),
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
			request.setRetryPolicy(new DefaultRetryPolicy(
					Config.SERVER_GET_DATA_TIMEOUT, 
					DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			VolleySingleton.getInstance(context).addToRequestQueue(request);
		}else{
			PullPushDataService.updateData(context, 
					Ses.getInstance(context).getChangeset(),
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
	
	private void sendImages(final String serviceKey, SQLiteDatabase db, File dir){
		FileFilter filter = new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.isFile()){
					String[] parts = pathname.getName().split("_");
					if(parts[0].compareTo(serviceKey)==0){
						return true;
					}
				}
				return false;
			}
		};
		for(File file :dir.listFiles(filter)){
			ContentValues values = new ContentValues();
			values.put(DbImageToSend.CN_URL, file.getAbsolutePath());
			db.insert(DbImageToSend.TABLE_NAME, null, values);
		}
		//UploadImageBroadcast.getInstance().start();
		WorkflowHelper.uploadImages(mContext);
	}
	
	private JSONObject createJSONService(Cursor c, SQLiteDatabase db) throws JSONException{
		JSONObject item = new JSONObject();
		ServiceInstanceDTO si = new ServiceInstanceDTO();
		si.fillFromCursor(c);
		
		item.put("SupervisorId", si.getEmpleadoRevision());
		item.put("FechaEvaluacion", DateHelper.getJSONDate(si.getFechaInicio()));
		item.put("ServicioConfiguracionId", si.getServicioConfiguracionId());
		item.put(JSON_SERVICE_INSTANCE_KEY, si.getKey());
		if(si.getComentariosInventario()!= null)
			item.put("ComentariosInventario", si.getComentariosInventario());
		if(si.getComentariosCheckList()!= null)
			item.put("ComentariosCheckList", si.getComentariosCheckList());
		if(si.getComentariosElementos()!= null)
			item.put("ComentariosElementos", si.getComentariosElementos());
		
		/*Cursor cInvent = db.rawQuery(DbQuery.SERVICE_INVENTORY, 
				new String[]{ 
					String.valueOf(si.getServicioInstanciaId()),
					String.valueOf(si.getServicioId()),
					String.valueOf(si.getTipo())
				}
		);*/
		Cursor cInvent = db.rawQuery(DbQuery.SERVICE_INVENTORY, 
			new String[]{ 
				String.valueOf(si.getServicioInstanciaId())
			}
		);
		JSONArray inventory = new JSONArray();
		item.put("Inventario", inventory);
		if(cInvent.moveToFirst()){
			do{
				JSONObject json = new JSONObject();
				json.put("EquipoId", cInvent.getInt(cInvent.getColumnIndex(DbEquipment._ID)));
				json.put("Valido", cInvent.getInt(cInvent.getColumnIndex(DbServiceEquipmentInventory.CN_CHECKED)) == 1);
				String comment =cInvent.getString(cInvent.getColumnIndex(DbServiceEquipmentInventory.CN_COMMENT));
				if(comment != null)
					json.put("Comentario", comment);
				inventory.put(json);
			}while(cInvent.moveToNext());
		}
		cInvent.close();

		Cursor cEmployee = db.rawQuery(DbQuery.EMPLOYEES_BY_SERVICE, 
				new String[]{ String.valueOf(si.getServicioInstanciaId()) });
		JSONArray employees = new JSONArray();
		item.put(JSON_LIST_EMPLOYEES, employees);
		if(cEmployee.moveToFirst()){
			do{
				JSONObject json = new JSONObject();
				createJSONEmployee(cEmployee, db, json, si);
				employees.put(json);
			}while(cEmployee.moveToNext());
		}
		cEmployee.close();
		return item;
	}
	
	private void createJSONEmployee(Cursor c, SQLiteDatabase db, JSONObject item, ServiceInstanceDTO serviceInstance) throws JSONException{
		EmployeeDTO employee = new EmployeeDTO();
		employee.fillFromCursor(c);
		
		item.put(JSON_EMPLOYEE_ID, employee.getElementoId());
				
		if(employee.getStatus() == null){
			item.put("Omitido", true);
		}else{
			item.put("Omitido", DbEmployee.EmployeeStatus.FINALIZED.compareTo(employee.getStatus()) == 0);
			if(employee.getComentariosCheckList()!= null)
				item.put("ComentariosCheckList", employee.getComentariosCheckList());
			if(employee.getComentariosExamen()!= null)
				item.put("ComentariosExamen", employee.getComentariosExamen());
			if(employee.getComentariosInventario()!= null)
				item.put("ComentariosInventario", employee.getComentariosInventario());

			JSONArray checklist = new JSONArray();
			item.put("CheckList", checklist);

			Cursor cQuestion = db.rawQuery(DbQuery.STAFF_REVIEW, 
					new String[]{ 
						String.valueOf(employee.getElementoId()),
						String.valueOf(serviceInstance.getServicioInstanciaId()) 
					}
			);
			createQuestions(cQuestion, checklist, true, true);
			
			JSONArray survey = new JSONArray();
			item.put("Examen", checklist);
			cQuestion = db.rawQuery(DbQuery.STAFF_SURVEY, 
					new String[]{ 
					String.valueOf(employee.getElementoId()),
					String.valueOf(serviceInstance.getServicioInstanciaId()) 
				}
			);
			createQuestions(cQuestion, survey, false, true);

			Cursor cInvent = db.rawQuery(DbQuery.STAFF_INVENTORY, 
					new String[]{ String.valueOf(employee.getElementoId()) });
			
			JSONArray inventory = new JSONArray();
			item.put("Inventario", inventory);
			if(cInvent.moveToFirst()){
				do{
					JSONObject json = new JSONObject();
					json.put("EquipoId", cInvent.getInt(cInvent.getColumnIndex(DbEquipment._ID)));
					json.put("Valido", cInvent.getInt(cInvent.getColumnIndex(DbEmployeeEquipmentInventory.CN_CHECKED)) == 1 );
					String comment = cInvent.getString(cInvent.getColumnIndex(DbEmployeeEquipmentInventory.CN_COMMENT));
					if(comment != null)
						json.put("Comentario", comment);
					inventory.put(json);
				}while(cInvent.moveToNext());
			}
			cInvent.close();
		}
	}
	
	private void createQuestions(Cursor c, JSONArray list, boolean resultado, boolean employee) throws JSONException{
		if(c.moveToFirst()){
			do{
				JSONObject json = new JSONObject();
				json.put("PreguntaId", c.getInt(c.getColumnIndex(DbQuestion._ID)));
				String result = null;
				String comment = null;
				if(resultado){
					if(employee)
						result = c.getString(c.getColumnIndex(DbReviewQuestionAnswerEmployee.CN_RESULT));
					else
						result = c.getString(c.getColumnIndex(DbReviewQuestionAnswerService.CN_RESULT));
					int intResult = -1;
					if("B".compareTo(result)==0){
						intResult = 1;
					}else if("G".compareTo(result)==0){
						intResult = 2;
					}else if("E".compareTo(result)==0){
						intResult = 3;
					}
					json.put("Resultado", intResult);
					
					if(employee)
						comment = c.getString(c.getColumnIndex(DbReviewQuestionAnswerEmployee.CN_COMMENT));
					else
						comment = c.getString(c.getColumnIndex(DbReviewQuestionAnswerService.CN_COMMENT));
					
				}else{
					result = c.getString(c.getColumnIndex(DbSurveyQuestionAnswer.CN_RESULT));
					if(result != null && result.length() > 0){
						json.put("Respuesta", result);
					}
					comment = c.getString(c.getColumnIndex(DbSurveyQuestionAnswer.CN_COMMENT));
				}
				if(comment != null)
					json.put("Comentarios", comment);
				if(json.length() > 0)
					list.put(json);
			}while(c.moveToNext());
		}
		c.close();
	}
}

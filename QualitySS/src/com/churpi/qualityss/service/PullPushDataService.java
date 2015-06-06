package com.churpi.qualityss.service;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;













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
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbRequisition;
import com.churpi.qualityss.client.db.QualitySSDbHelper;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbImageToSend;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestionAnswer;
import com.churpi.qualityss.client.dto.DataDTO;
import com.churpi.qualityss.client.dto.RequisitionDTO;
import com.churpi.qualityss.client.dto.ServiceInstanceDTO;
import com.churpi.qualityss.client.helper.DateHelper;
import com.churpi.qualityss.client.helper.GsonRequest;
import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class PullPushDataService extends IntentService {

	private final String JSON_LIST_SERVICES = "Servicios";
	private final String JSON_LIST_REQUISITIONS = "Requisiciones";
	private final String JSON_REQUISITION_ID = "RequisicionId";
	private final String JSON_LIST_EMPLOYEES = "EvaluacionElemento";
	private final String JSON_EMPLOYEE_ID = "ElementoId";
	private final String JSON_SERVICE_INSTANCE_KEY = "Key";
	private static final String ERR_CONN = "CONNECTION";
	
	private Context mContext;
	
	public interface Callback {
		void success(Context context);
		void error(VolleyError error, Context context);
	}



	public PullPushDataService() {
		super("PullPushDataService");		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
	
		String changeSet = Ses.getInstance(getBaseContext()).getChangeset();
		mContext = getBaseContext();
		if(changeSet== null){
			updateData();
		}else{
			sendData();
		}
	}
	
	
	private void sendData(){
		final JSONObject json = (JSONObject)DbTrans.read(mContext, new DbTrans.Db() {

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
					sCursor = db.rawQuery(DbQuery.GET_REQUISITION_TO_SEND, null);
					if(sCursor.moveToFirst()){
						JSONArray jRequisitions = new JSONArray();
						json.put(JSON_LIST_REQUISITIONS, jRequisitions);
						do{
							RequisitionDTO requisition = new RequisitionDTO();
							requisition.fillFromCursor(sCursor);
							JSONObject jRequisition = new JSONObject();
							jRequisition.put(JSON_REQUISITION_ID, requisition.getId());
							jRequisition.put("ResponsableId", requisition.getResponsableId());
							jRequisition.put("Acuerdo_Compromiso", requisition.getAcuerdo_Compromiso());
							/*if(requisition.getLugar() != null)
								jRequisition.put("Lugar", requisition.getLugar());*/
							if(requisition.getAvance() != null)
								jRequisition.put("Avance", requisition.getAvance());							
							jRequisition.put("Status", requisition.getStatus());
							jRequisition.put("FechaInicio", DateHelper.getJSONDate(requisition.getFechaInicio()));
							if(requisition.getFechaTerminacion() != null)
								jRequisition.put("FechaTerminacion", DateHelper.getJSONDate(requisition.getFechaTerminacion()));
							if(requisition.getServicioId() != null && requisition.getServicioId() >0)
								jRequisition.put("ServicioId", requisition.getServicioId());							
							jRequisition.put("UniqueKey", requisition.getUniqueKey());
							
							jRequisitions.put(jRequisition);							
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

		if(!json.isNull(JSON_LIST_SERVICES) || !json.isNull(JSON_LIST_REQUISITIONS)){	
			
			
			
			JsonObjectRequestResponseString request = new JsonObjectRequestResponseString(
					Request.Method.POST,
					Config.getUrl(Config.ServerAction.SEND_DATA), 
					json, 
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject arg0) {
							DbTrans.write(mContext, new DbTrans.Db() {
								@Override
								public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
									try {
										if(!json.isNull(JSON_LIST_SERVICES)){
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
											}
										}
										if(!json.isNull(JSON_LIST_REQUISITIONS)){
											JSONArray requisitions = json.getJSONArray(JSON_LIST_REQUISITIONS);
											for(int i = 0; i < requisitions.length(); i++){
												JSONObject requisition = (JSONObject) requisitions.get(i);
												ContentValues values = new ContentValues();
												values.put(DbRequisition.CN_SENT, 1);
												int requisitionId = requisition.getInt(JSON_REQUISITION_ID); 
												db.update(DbRequisition.TABLE_NAME, values, 
														DbRequisition._ID + "=?", 
														new String[]{ String.valueOf(requisitionId) });											
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
									return null;
								}
							});

							updateData();
							//WorkflowHelper.pullPushData(mContext);
							/*PullPushDataService.updateData(context, 
									Ses.getInstance(context).getChangeset(),
									callback);*/						
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
								errorMsg = mContext.getString(R.string.ttl_error);

							Toast.makeText(mContext, mContext.getString(R.string.msg_error_send) + errorMsg, Toast.LENGTH_SHORT).show();
							UpdateDataReciever.getInstance().start();
						}
					});
			request.setRetryPolicy(new DefaultRetryPolicy(
					Config.SERVER_GET_DATA_TIMEOUT, 
					DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			VolleySingleton.getInstance(mContext).addToRequestQueue(request);
		}else{
			updateData();
			//WorkflowHelper.pullPushData(mContext);
			/*PullPushDataService.updateData(context, 
					Ses.getInstance(context).getChangeset(),
					callback);*/
		}
	}
	
	private void updateData(){
		String changeSet = Ses.getInstance(getBaseContext()).getChangeset();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("content-type", "text/json");
		GsonRequest<DataDTO> request = new GsonRequest<DataDTO>(
				Config.getUrl(Config.ServerAction.GET_DATA, changeSet).toString(), 
				DataDTO.class, 
				headers, 
				new Response.Listener<DataDTO>(){

					@Override
					public void onResponse(DataDTO data) {
						if(data != null){
							DownloadFileReciever.setDataDTO(data);
							sendBroadCastResult(mContext, Constants.PULL_PUSH_DATA_REFRESH, mContext.getString(R.string.msg_update_database), 50);
							setPreferencesByData(mContext, data);
							QualitySSDbHelper dbHelper = new QualitySSDbHelper(mContext);
							dbHelper.updateDBfromValue(data);
						}else{
							String errorMsg = mContext.getString(R.string.ttl_error);
							Toast.makeText(mContext, "no", Toast.LENGTH_LONG).show();
							sendBroadCastResult(mContext, Constants.PULL_PUSH_DATA_FAIL, errorMsg, 0);
						}
						UpdateDataReciever.getInstance().start();
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
							errorMsg = mContext.getString(R.string.ttl_error);

						Log.e(ERR_CONN, errorMsg);
						sendBroadCastResult(mContext, Constants.PULL_PUSH_DATA_FAIL, errorMsg, 0);
						UpdateDataReciever.getInstance().start();
					}
				}); 
		request.setRetryPolicy(new DefaultRetryPolicy(
				50000, 
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		VolleySingleton.getInstance(mContext).addToRequestQueue(request);
	}
	
	private static void setPreferencesByData(Context context, DataDTO data){
		Ses.getInstance(context).edit()
		.setChangeset(data.getChangeset())
		.setImgURL(data.getImageBaseUrl())
		.commit();
	}

	private static void sendBroadCastResult(Context context, String statusDescription, String description, int progress){
		Intent status = new Intent();
		status.setAction(Constants.PULL_PUSH_DATA_ACTION);
		status.putExtra(Constants.PULL_PUSH_DATA_STATUS, statusDescription);
		status.putExtra(Constants.PULL_PUSH_DATA_DESCRIPTION, description);
		status.putExtra(Constants.PULL_PUSH_DATA_PROGRESS, progress);
		//status.putExtra(Constants.PULL_PUSH_DATA_DATA, enqueue);
		context.sendBroadcast(status);

	}
	
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
		
		int employeeId = c.getInt(c.getColumnIndex(DbEmployeeInstance.CN_EMPLOYEE));
		int employeeInstanceId = c.getInt(c.getColumnIndex(DbEmployeeInstance._ID));
		String status = c.getString(c.getColumnIndex(DbEmployeeInstance.CN_STATUS));
		String reviewComments = c.getString(c.getColumnIndex(DbEmployeeInstance.CN_REVIEW_COMMENT));
		String inventoryComments = c.getString(c.getColumnIndex(DbEmployeeInstance.CN_INVENTORY_COMMENT));
		String surveyComments = c.getString(c.getColumnIndex(DbEmployeeInstance.CN_SURVEY_COMMENT));
		
		item.put(JSON_EMPLOYEE_ID, employeeId);
				
		if(status == null){
			item.put("Omitido", true);
		}else{
			item.put("Omitido", DbEmployeeInstance.EmployeeStatus.FINALIZED.compareTo(status) == 0);
			if(reviewComments != null)
				item.put("ComentariosCheckList", reviewComments);
			if(surveyComments != null)
				item.put("ComentariosExamen", surveyComments);
			if(inventoryComments != null)
				item.put("ComentariosInventario", inventoryComments);

			JSONArray checklist = new JSONArray();
			item.put("CheckList", checklist);

			Cursor cQuestion = db.rawQuery(DbQuery.STAFF_REVIEW, 
					new String[]{ String.valueOf(employeeInstanceId)}
			);
			createQuestions(cQuestion, checklist, true, true);
			
			JSONArray survey = new JSONArray();
			item.put("Examen", checklist);
			cQuestion = db.rawQuery(DbQuery.STAFF_SURVEY, 
					new String[]{ String.valueOf(employeeInstanceId)}
			);
			createQuestions(cQuestion, survey, false, true);

			Cursor cInvent = db.rawQuery(DbQuery.STAFF_INVENTORY, 
					new String[]{ String.valueOf(employeeInstanceId)}
			);
			
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

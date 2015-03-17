package com.churpi.qualityss.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.churpi.qualityss.Config;
import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.db.QualitySSDbHelper;
import com.churpi.qualityss.client.dto.DataDTO;
import com.churpi.qualityss.client.helper.GsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

public class PullPushDataService extends IntentService {

	private static final String RESP_TIMESTAMP = "timestamp";

	private static final String ERR_CONN = "CONNECTION";

	private static long enqueue;



	public PullPushDataService() {
		super("PullPushDataService");		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
	
		String changeSet = getChangeset(); 
		if(changeSet== null){
			updateData(getBaseContext(), changeSet);
		}
	}
		
	
	public static void updateData(final Context context, String changeSet){
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
							sendBroadCastResult(context, Constants.PULL_PUSH_DATA_REFRESH, context.getString(R.string.msg_update_database), 50);
							setChangeset(context, data.getChangeset());
							QualitySSDbHelper dbHelper = new QualitySSDbHelper(context);
							dbHelper.updateDBfromValue(data);
						}else{
							String errorMsg = context.getString(R.string.ttl_error);
							Toast.makeText(context, "no", Toast.LENGTH_LONG).show();
							sendBroadCastResult(context, Constants.PULL_PUSH_DATA_FAIL, errorMsg, 0);
						}

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

						Log.e(ERR_CONN, errorMsg);
						sendBroadCastResult(context, Constants.PULL_PUSH_DATA_FAIL, errorMsg, 0);
					}
				}); 
		request.setRetryPolicy(new DefaultRetryPolicy(
				50000, 
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		VolleySingleton.getInstance(context).addToRequestQueue(request);
	}
	
	private static void setChangeset(Context context, String changeset){
		SharedPreferences pref = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(Constants.PREF_CHANGESET, changeset);
		editor.commit();
	}
	private String getChangeset(){
		SharedPreferences pref = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
		if(pref.contains(Constants.PREF_CHANGESET)){
			return pref.getString(Constants.PREF_CHANGESET, null);
		}
		return null;
	}

	private static void sendBroadCastResult(Context context, String statusDescription, String description, int progress){
		Intent status = new Intent();
		status.setAction(Constants.PULL_PUSH_DATA_ACTION);
		status.putExtra(Constants.PULL_PUSH_DATA_STATUS, statusDescription);
		status.putExtra(Constants.PULL_PUSH_DATA_DESCRIPTION, description);
		status.putExtra(Constants.PULL_PUSH_DATA_PROGRESS, progress);
		status.putExtra(Constants.PULL_PUSH_DATA_DATA, enqueue);
		context.sendBroadcast(status);

	}
}

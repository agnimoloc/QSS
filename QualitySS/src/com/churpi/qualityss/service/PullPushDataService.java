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

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

public class PullPushDataService extends IntentService {

	private static final String RESP_TIMESTAMP = "timestamp";

	private static final String ERR_CONN = "CONNECTION";

	private long enqueue;


	public PullPushDataService() {
		super("PullPushDataService");		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("content-type", "text/json");
		GsonRequest<DataDTO> request = new GsonRequest<DataDTO>(
				Config.getUrl(Config.ServerAction.GET_DATA).toString(), 
				DataDTO.class, 
				headers, 
				new Response.Listener<DataDTO>(){

					@Override
					public void onResponse(DataDTO data) {
						if(data != null){
							sendBroadCastResult(Constants.PULL_PUSH_DATA_REFRESH, getString(R.string.msg_update_database), 50);
							setChangeset(data.getChangeset());
							QualitySSDbHelper dbHelper = new QualitySSDbHelper(getBaseContext());
							dbHelper.updateDBfromValue(data);

						}else{
							String errorMsg = getString(R.string.ttl_error);
							Toast.makeText(getApplicationContext(), "no", Toast.LENGTH_LONG).show();
							sendBroadCastResult(Constants.PULL_PUSH_DATA_FAIL, errorMsg, 0);
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
							errorMsg = getString(R.string.ttl_error);

						Log.e(ERR_CONN, errorMsg);
						sendBroadCastResult(Constants.PULL_PUSH_DATA_FAIL, errorMsg, 0);
					}
				}); 
		request.setRetryPolicy(new DefaultRetryPolicy(
				50000, 
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		VolleySingleton.getInstance(this).addToRequestQueue(request);

	}
	
	private void setChangeset(String changeset){
		SharedPreferences pref = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.commit();
	}

	private void sendBroadCastResult(String statusDescription, String description, int progress){
		Intent status = new Intent();
		status.setAction(Constants.PULL_PUSH_DATA_ACTION);
		status.putExtra(Constants.PULL_PUSH_DATA_STATUS, statusDescription);
		status.putExtra(Constants.PULL_PUSH_DATA_DESCRIPTION, description);
		status.putExtra(Constants.PULL_PUSH_DATA_PROGRESS, progress);
		status.putExtra(Constants.PULL_PUSH_DATA_DATA, enqueue);
		sendBroadcast(status);

	}
}

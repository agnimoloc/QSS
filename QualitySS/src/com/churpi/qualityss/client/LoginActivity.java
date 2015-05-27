package com.churpi.qualityss.client;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.churpi.qualityss.Config;
import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.WorkflowHelper;
import com.churpi.qualityss.service.DownloadFileReciever;
import com.churpi.qualityss.service.PullPushDataService;
import com.churpi.qualityss.service.UpdateDataReciever;
import com.churpi.qualityss.service.UploadImageService;
import com.churpi.qualityss.service.VolleySingleton;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends Activity {

	private final int REQUEST_MAIN_ACTION = 1;
	
	private final int VALID_USER = 1;
	private final int VALID_USER_PASSWORD =2;
	private final int VALID_NONE = 0;
	
	
	
	//UpdateDataReciever updateRecievier;
	DownloadFileReciever downloadRecievier;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		UpdateDataReciever.createInstance(this);
		downloadRecievier = new DownloadFileReciever(this);
		
        registerReceiver(UpdateDataReciever.getInstance(), new IntentFilter(Constants.UPDATE_DATA_ACTION) );
        registerReceiver(downloadRecievier, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE) );
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(UpdateDataReciever.getInstance());
		unregisterReceiver(downloadRecievier);
		UpdateDataReciever.getInstance().dispose();
		
		downloadRecievier.dispose();
		
		
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Toast.makeText(this, "Saliendo", Toast.LENGTH_LONG).show();
		quit();
	}
	
	private void quit(){
		this.finish();
        System.exit(0);
	}
	
	public void onClick_Login(View v){
		TextView accountField = (TextView)findViewById(R.id.txtUser);
		TextView passwordField = (TextView)findViewById(R.id.txtPassword);
		final String account = accountField.getText().toString().trim();
		final String password = passwordField.getText().toString();
		int valid = validateLocal(account, password);
		if(valid == VALID_NONE){
			final ProgressDialog progress = ProgressDialog.show(this, 
					getString(R.string.msg_authenitcate), 
					getString(R.string.msg_verify_user)); 
			StringRequest request = new StringRequest(
				Config.getUrl(Config.ServerAction.GET_AUTHENTICATE, account, password)
				, new Response.Listener<String>() {
					@Override
					public void onResponse(String employeeIdStr) {
						int employeeId = Integer.parseInt(employeeIdStr);
						if(employeeId != 0){
							
							Ses.getInstance(getBaseContext())
							.edit()
							.setAccount(account)
							.setPassHashcode(password.hashCode())
							.setEmployee(employeeId)
							.commit();
					
							startMainActivity();
						}else{
							Toast.makeText(getBaseContext(), getString(R.string.msg_invalid_login), Toast.LENGTH_LONG).show();
						}
						progress.dismiss();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						String errorMsg = error.getLocalizedMessage();
						if(errorMsg == null){
							errorMsg = error.getMessage();
						}if(errorMsg == null && error instanceof com.android.volley.TimeoutError){
							errorMsg = com.android.volley.TimeoutError.class.getName();
						}if(errorMsg == null){
							errorMsg = getString(R.string.ttl_error);
						}

						Log.e("Login connection error", errorMsg);
						Toast.makeText(getBaseContext(), getString(R.string.msg_error_connection), Toast.LENGTH_LONG).show();
						progress.dismiss();
					}
				});
			request.setRetryPolicy(new DefaultRetryPolicy(
				Config.SERVER_GET_DATA_TIMEOUT, 
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			VolleySingleton.getInstance(this).addToRequestQueue(request);    
		} else if(valid == VALID_USER ){
			Toast.makeText(getBaseContext(), getString(R.string.msg_invalid_login), Toast.LENGTH_LONG).show();
		} else if(valid == VALID_USER_PASSWORD){			
			startMainActivity();
		}
	}
	
	private int validateLocal(String account, String password){
		/*SharedPreferences pref = Constants.getPref(this);
		String savedAccount =  pref.getString(Constants.PREF_ACCOUNT, null);
		int savedPassHash =  pref.getInt(Constants.PREF_PASSHASH, 0);*/
		String savedAccount =  Ses.getInstance(this).getAccount();
		int savedPassHash =  Ses.getInstance(this).getPassHashcode();
		if(savedAccount != null && account.compareTo(savedAccount)==0){
			if(password.hashCode() == savedPassHash){
				return VALID_USER_PASSWORD;
			}
			return VALID_USER;
		}
		return VALID_NONE;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_MAIN_ACTION && resultCode == RESULT_OK){
			downloadRecievier.downloadFiles();
			Intent mainMenuActivity = new Intent(this, MainMenuActivity.class);
			startActivity(mainMenuActivity);
			
			UpdateDataReciever.getInstance().start();
			//UploadImageBroadcast.getInstance().start();

			WorkflowHelper.uploadImages(this);
		}
    	
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void startMainActivity(){
		
		Intent intentMain = new Intent(getBaseContext(), MainActivity.class);
		startActivityForResult(intentMain, REQUEST_MAIN_ACTION);						
	}
}

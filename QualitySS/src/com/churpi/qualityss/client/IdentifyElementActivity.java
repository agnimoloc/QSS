package com.churpi.qualityss.client;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.churpi.qualityss.Config;
import com.churpi.qualityss.service.VolleySingleton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class IdentifyElementActivity extends Activity {

	public static final String EMPLOYEE_ID = "employeeId";
	public static final String EMPLOYEE_NAME = "employeeName";
	public final String RESULT = "result";

	private final String COUNTER = "counter";

	int employeeId;
	String name;
	int count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identify_element);		
		employeeId = getIntent().getIntExtra(EMPLOYEE_ID, -1);
		name = getIntent().getStringExtra(EMPLOYEE_NAME);
		if(savedInstanceState != null){
			count = savedInstanceState.getInt(COUNTER);
		}else{
			readBarcode();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(COUNTER, count);
		super.onSaveInstanceState(outState);
	}

	private void readBarcode(){
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.setPackage("com.google.zxing.client.android");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		try{
			startActivityForResult(intent, 0);	
		}catch (ActivityNotFoundException e){
			sendResult(false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0){		
			if(resultCode == RESULT_OK){
				String contents = data.getStringExtra("SCAN_RESULT");
				String format = data.getStringExtra("SCAN_RESULT_FORMAT");

				final ProgressDialog progressDialog = ProgressDialog.show(this, 
						getString(R.string.msg_authenitcate), 
						String.format(
								getString(R.string.msg_verify_barcode),
								name));

				StringRequest request = new StringRequest(
						Config.getUrl(Config.ServerAction.GET_BARCODE, contents), 
						new Response.Listener<String>(){
							@Override
							public void onResponse(String arg0) {
								int resultEmployeeId = Integer.parseInt(arg0);
								progressDialog.dismiss();
								if(resultEmployeeId == employeeId){
									sendResult(true);
								}else{
									//showButtons(true);
									Toast.makeText(
											getBaseContext(), 
											String.format(
													getString(R.string.msg_wrong_barcode),
													name), 
											Toast.LENGTH_LONG).show();					
								}
							}
						}, 
						new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
								progressDialog.dismiss();
								//showButtons(true);
								Toast.makeText(getBaseContext(), getString(R.string.msg_error_connection), Toast.LENGTH_LONG).show();
							}
						});
				VolleySingleton.getInstance(this).addToRequestQueue(request);
			}else{
				Toast.makeText(this, getString(R.string.msg_read_barcode_try_again), Toast.LENGTH_LONG).show();
			}
		}
	}

	public void onClick_tryAgain(View v){
		/*if(count++ < 3){
			readBarcode();
		}else{*/			
			sendResult(false);
		//}
	}

	private void sendResult(boolean result){
		Intent intent = new Intent();
		intent.putExtra(RESULT, result);
		intent.putExtra(EMPLOYEE_ID, employeeId);
		intent.putExtra(EMPLOYEE_NAME, name);
		setResult(RESULT_OK, intent);
		finish();
	}

	public void onClick_cancel(View v){
		setResult(RESULT_CANCELED);
		finish();
	}	

}

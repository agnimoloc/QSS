package com.churpi.qualityss.client;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.churpi.qualityss.Config;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSector;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.helper.Alerts;
import com.churpi.qualityss.client.helper.ElementListAdapter;
import com.churpi.qualityss.service.VolleySingleton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.Toast;

public class StaffReviewListActivity extends Activity {

	Cursor c = null;
	ElementListAdapter adapter = null;
	
	int serviceId;
	int employeeId;
	String employeeName;
	
	public static final String FLD_SERVICE_ID = "service_id"; 
	
	private final int REQUEST_BARCODE = 0;
	private final int REQUEST_INVENTORY = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_review_list);
		
		serviceId = getIntent().getIntExtra(FLD_SERVICE_ID, -1);
		
		initCursor();

		String[] from = new String[]{DbSector.CN_NAME};
		int[] to = new int[]{ android.R.id.text1};
		
		GridView grid = (GridView)findViewById(R.id.gridView1);
		adapter = new ElementListAdapter(this, 
				R.layout.item_staff_review, c, 
				from, to, 
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(onSelected);
	}
	
	private void initCursor(){
		c = (Cursor)DbTrans.read(this, new DbTrans.Db(){
			@Override
			public Object onDo(Context context, SQLiteDatabase db) {
				return db.rawQuery(DbQuery.EMPLOYEES_BY_SERVICE,new String[]{String.valueOf(serviceId)});
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		if(c!= null)
			c.close();
		super.onDestroy();
	}
	
	private AdapterView.OnItemClickListener onSelected = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			c.moveToPosition(position);
			int tmpEmployeeId = c.getInt(c.getColumnIndex(DbEmployee._ID));;
			
			if(employeeId != tmpEmployeeId ){
				employeeId = tmpEmployeeId;
				employeeName = c.getString(c.getColumnIndex(DbEmployee.CN_NAME));
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.setPackage("com.google.zxing.client.android");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				try{
					startActivityForResult(intent, REQUEST_BARCODE);	
				}catch (ActivityNotFoundException e){
					openStaffInventoryActivity();
				}
			}else{
				openStaffInventoryActivity();
			}
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == REQUEST_BARCODE){
			if(resultCode == RESULT_OK){
				String contents = data.getStringExtra("SCAN_RESULT");
				//String format = data.getStringExtra("SCAN_RESULT_FORMAT");

				final ProgressDialog progressDialog = ProgressDialog.show(this, 
						getString(R.string.msg_authenitcate), 
						String.format(
								getString(R.string.msg_verify_barcode),
								employeeName));

				StringRequest request = new StringRequest(
						Config.getUrl(Config.ServerAction.GET_BARCODE, contents), 
						new Response.Listener<String>(){
							@Override
							public void onResponse(String arg0) {
								int resultEmployeeId = Integer.parseInt(arg0);
								progressDialog.dismiss();
								if(resultEmployeeId == employeeId){
									DbTrans.write(getBaseContext(), new DbTrans.Db() {
										@Override
										public Object onDo(Context context, SQLiteDatabase db) {
											ContentValues values = new ContentValues();
											values.put(DbEmployee.CN_BARCODECHECK, 1);
											db.update(DbEmployee.TABLE_NAME, values, 
													DbEmployee._ID + "=?", 
													new String[]{String.valueOf(employeeId)});
											return null;
										}
									});
									openStaffInventoryActivity();
								}else{
									Toast.makeText(
											getBaseContext(), 
											String.format(
													getString(R.string.msg_wrong_barcode),
													employeeName), 
											Toast.LENGTH_LONG).show();					
								}
							}
						}, 
						new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
								progressDialog.dismiss();
								Toast.makeText(getBaseContext(), getString(R.string.msg_error_connection), Toast.LENGTH_LONG).show();
							}
						});
				VolleySingleton.getInstance(this).addToRequestQueue(request);
			}
		}else if (requestCode == REQUEST_INVENTORY){
			initCursor();
			Cursor oldCursor = adapter.swapCursor(c);
			oldCursor.close();
		}

	};
	
	private void openStaffInventoryActivity(){
		Intent intent = new Intent(getBaseContext(), StaffInventoryActivity.class);
		intent.putExtra(StaffInventoryActivity.SERVICE_ID, serviceId);
		intent.putExtra(StaffInventoryActivity.ID, employeeId);			
		intent.putExtra(StaffInventoryActivity.NAME, employeeName);
		intent.setAction(StaffInventoryActivity.ACTION_EMPLOYEE);
		startActivityForResult(intent, REQUEST_INVENTORY);
	}
	
	public void onClick_finish(View v){
		
		boolean canFinish = (Boolean)DbTrans.read(this, new DbTrans.Db() {
			
			@Override
			public Object onDo(Context context, SQLiteDatabase db) {
				Cursor cur = db.rawQuery(
						DbQuery.EMPLOYEES_SERVICE_NOT_END, 
						new String[]{String.valueOf(serviceId)});
				
				String msg = null;
				if(cur.getCount() > 0){
					msg = getString(R.string.msg_there_are_employees_not_finalized);
				}
				cur.close();
				
				cur = db.rawQuery(DbQuery.SERVICE_INVENTORY_NULL_RESULT,
						new String[]{ String.valueOf(serviceId)});
				
				if(cur.getCount() > 0){
					msg = (msg != null ? msg + "\n":"")+  getString(R.string.msg_fault_service_inventory);
				}
				
				if(msg != null){
					Alerts.showError(context, msg, R.string.msg_cannot_finish_service);
					return false;
				}
				
				return true;
			}
		});
		
		if(canFinish){
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(R.string.ttl_finish_service);
			dialogBuilder.setMessage(R.string.msg_finish_service);
			dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					DbTrans.write(getBaseContext(), new DbTrans.Db() {
						@Override
						public Object onDo(Context context, SQLiteDatabase db) {
							ContentValues values = new ContentValues();
							values.put(DbService.CN_STATUS, DbService.ServiceStatus.FINALIZED);
							db.update(DbService.TABLE_NAME, values, DbService._ID + "=?", new String[]{String.valueOf(serviceId)});							
							return null;
						}
					});
					Intent intent = new Intent(getApplicationContext(), SectorListActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					dialog.dismiss();

				}
			});
			dialogBuilder.create().show();		
		}
	}
}

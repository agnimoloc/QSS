package com.churpi.qualityss.client;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.churpi.qualityss.Config;
import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSector;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance;
import com.churpi.qualityss.client.helper.ElementListAdapter;
import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.WorkflowHelper;
import com.churpi.qualityss.service.VolleySingleton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.Toast;

public class StaffReviewListActivity extends Activity {

	Cursor c = null;
	ElementListAdapter adapter = null;

	int serviceInstanceId;
	int employeeId;
	int tmpEmployeeId;
	String employeeName;
	String action;

	private final int REQUEST_BARCODE = 0;
	private final int REQUEST_INVENTORY = 1;
	private final int REQUEST_COMMENTS = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_review_list);

		serviceInstanceId = Ses.getInstance(this).getServiceInstanceId();
		
		action = getIntent().getAction();
		if(action != null && Constants.ACTION_EMPLOYEE.compareTo(action)==0){
			action = Constants.ACTION_SERVICE;
		}

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
		
		if(action == null || Constants.ACTION_SERVICE.compareTo(action) != 0){
			Button button = (Button)findViewById(android.R.id.button1);
			button.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(action != null && Constants.ACTION_SERVICE.compareTo(action) == 0 ){
			getMenuInflater().inflate(R.menu.comment_menu, menu);
		}
		if(action != null && Constants.ACTION_SERVICE.compareTo(action) == 0 ){
			menu.removeItem(R.id.action_warning);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_comments) {
			addComments();
			return true;
		}else if (id == R.id.action_requisition) {
			WorkflowHelper.getRequisition(this, action);
			return true;
		}else if(id == R.id.action_warning){
			WorkflowHelper.getWarning(this, action);
		}

		return super.onOptionsItemSelected(item);
	}

	private void initCursor(){
		c = (Cursor)DbTrans.read(this, new DbTrans.Db(){
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				if(action != null && Constants.ACTION_SERVICE.compareTo(action) == 0){
					return db.rawQuery(
						DbQuery.EMPLOYEES_BY_SERVICE,
						new String[]{String.valueOf(serviceInstanceId)});
				}else{
					return db.rawQuery(
						DbQuery.ALL_EMPLOYEES,null);
				}
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

			String status = c.getString(c.getColumnIndex(DbEmployeeInstance.CN_STATUS));
			if(status != null && DbEmployeeInstance.EmployeeStatus.FINALIZED.compareTo(status)==0){
				Toast.makeText(getApplicationContext(), getString(R.string.msg_warning_employee_finalized), Toast.LENGTH_LONG).show();
				return;
			}

			tmpEmployeeId = c.getInt(c.getColumnIndex(DbEmployeeInstance.CN_EMPLOYEE));

			if(employeeId != tmpEmployeeId && 
					action != null && Constants.ACTION_SERVICE.compareTo(action) == 0 ){
				employeeId = tmpEmployeeId;

				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.setPackage("com.google.zxing.client.android");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				try{
					startActivityForResult(intent, REQUEST_BARCODE);	
				}catch (ActivityNotFoundException e){
					openStaffInventoryActivity(false);
				}
			}else{
				openStaffInventoryActivity(false);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(requestCode == REQUEST_BARCODE && resultCode == RESULT_OK){
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
								openStaffInventoryActivity(true);
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
		}else if (requestCode == REQUEST_BARCODE && requestCode == REQUEST_INVENTORY){
			initCursor();
			Cursor oldCursor = adapter.swapCursor(c);
			oldCursor.close();
		} else if(requestCode == REQUEST_COMMENTS && resultCode == RESULT_OK){
			Bundle extras = data.getExtras();
			updateEmployeeComment(extras.getString(CheckpointCommentActivity.FLD_COMMENT));
			Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show();
		}

	};
	
	private void startEmployee(boolean barcodeChequed){
		int employeeInstanceId = (Integer)DbTrans.write(getBaseContext(), barcodeChequed,  new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {				
				boolean barcodeChecked = (Boolean)parameter;
				Cursor c = db.query(DbEmployeeInstance.TABLE_NAME, 
						new String[]{ DbEmployeeInstance._ID}, 
						DbEmployeeInstance.CN_EMPLOYEE + "=? AND " + 
						DbEmployeeInstance.CN_SERVICE_INSTANCE + "=?", 
						new String[]{
							String.valueOf(employeeId),
							String.valueOf(serviceInstanceId)
						}, null, null, null);
				if(c.moveToFirst()){
					return c.getInt(c.getColumnIndex(DbEmployeeInstance._ID));
				}else{
					ContentValues values = new ContentValues();
					values.put(DbEmployeeInstance.CN_BARCODECHECK, barcodeChecked?1:0);
					values.put(DbEmployeeInstance.CN_SERVICE_INSTANCE, serviceInstanceId);
					values.put(DbEmployeeInstance.CN_EMPLOYEE, employeeId);
					return (int)db.insert(DbEmployeeInstance.TABLE_NAME, null, values);
				}
			}
		});
		Ses.getInstance(this).setEmployeeInstanceId(employeeInstanceId);
	}

	private void openStaffInventoryActivity(boolean barcodeChequed){
		if(action != null && Constants.ACTION_SERVICE.compareTo(action) == 0 ){
			startEmployee(barcodeChequed);
		}
		Ses.getInstance(getBaseContext()).setEmployeeId(tmpEmployeeId);
		employeeName = c.getString(c.getColumnIndex(DbEmployee.CN_NAME));
		Ses.getInstance(getParent()).setEmployeeName(employeeName);

		startActivityForResult(
				WorkflowHelper.process(this, 
						R.id.gridView1,
						action == null? null: Constants.ACTION_EMPLOYEE),REQUEST_INVENTORY);
	}

	public void onClick_finish(View v){

		startActivity(
				WorkflowHelper.process(
						StaffReviewListActivity.this,
						android.R.id.button1,
						action
				)
		);
	}

	private void addComments(){
		String comment = (String) DbTrans.read(this, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				Cursor cur = db.query(
						DbServiceInstance.TABLE_NAME, 
						new String[]{DbServiceInstance._ID ,DbServiceInstance.CN_EMPLOYEE_COMMENT}, 
						DbServiceInstance._ID + "=?", 
						new String[]{
								String.valueOf(serviceInstanceId)
						}, null, null, null);
				if(cur.moveToFirst()){
					return cur.getString(cur.getColumnIndex(DbServiceInstance.CN_EMPLOYEE_COMMENT));
				}
				cur.close();
				return null;
			}
		});
		WorkflowHelper.getComments(this, 
				comment, getString(R.string.inst_comment_element_result), 
				REQUEST_COMMENTS);
	}
	private void updateEmployeeComment(String comment){
		DbTrans.write(this, comment, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				String comment = (String)parameter;
				ContentValues values = new ContentValues();
				values.put(DbServiceInstance.CN_EMPLOYEE_COMMENT, comment);
				db.update(DbServiceInstance.TABLE_NAME, 
						values, 
						DbServiceInstance._ID + "=?", 
						new String[]{String.valueOf(serviceInstanceId)});					
				return null;
			}
		});
	}
}

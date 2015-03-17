package com.churpi.qualityss.client;

import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSector;
import com.churpi.qualityss.client.dto.EmployeeDTO;
import com.churpi.qualityss.client.helper.ElementListAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;

public class StaffReviewListActivity extends Activity {

	Cursor c = null;
	int serviceId;
	
	public static final String FLD_SERVICE_ID = "service_id"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_review_list);
		
		serviceId = getIntent().getIntExtra(FLD_SERVICE_ID, -1);
		
		final GridView grid = (GridView)findViewById(R.id.gridView1);
		DbTrans.read(this, new DbTrans.Db(){

			@Override
			public void onDo(Context context, SQLiteDatabase db) {
				c = db.rawQuery(DbQuery.EMPLOYEES_BY_SERVICE,new String[]{String.valueOf(serviceId)});
				String[] from = new String[]{DbSector.CN_NAME};
				int[] to = new int[]{ android.R.id.text1};
				grid.setAdapter(new ElementListAdapter(context, 
						R.layout.item_staff_review, c, 
						from, to, 
						CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
			}
		});
		
		grid.setOnItemClickListener(onSelected);
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
			int employeeId = c.getInt(c.getColumnIndex(DbEmployee._ID));
			String name = c.getString(c.getColumnIndex(DbEmployee.CN_NAME));
			Intent intent = new Intent(getBaseContext(), IdentifyElementActivity.class);			
			intent.putExtra(IdentifyElementActivity.EMPLOYEE_ID, employeeId);			
			intent.putExtra(IdentifyElementActivity.EMPLOYEE_NAME, name);
			
			startActivityForResult(intent, 0);			
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == 0){
			if(resultCode == RESULT_OK){
				int employeeId = data.getIntExtra(IdentifyElementActivity.EMPLOYEE_ID, -1);
				String name = data.getStringExtra(IdentifyElementActivity.EMPLOYEE_NAME);
				Intent intent = new Intent(getBaseContext(), StaffInventoryActivity.class);
				intent.putExtra(StaffInventoryActivity.SERVICE_ID, serviceId);
				intent.putExtra(StaffInventoryActivity.ID, employeeId);			
				intent.putExtra(StaffInventoryActivity.NAME, name);
				intent.setAction(StaffInventoryActivity.ACTION_EMPLOYEE);
				startActivity(intent);
			}
		}

	};
}

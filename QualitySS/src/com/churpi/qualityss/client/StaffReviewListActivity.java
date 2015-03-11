package com.churpi.qualityss.client;

import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.helper.StaffReviewAdapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class StaffReviewListActivity extends Activity {

	Cursor c = null;
	int serviceId;
	
	public static final String FLD_SERVICE_ID = "service_id"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_review_list);
		
		serviceId = getIntent().getIntExtra(FLD_SERVICE_ID, -1);
		
		final ListView list = (ListView)findViewById(R.id.listView1);
		DbTrans.read(this, new DbTrans.Db(){

			@Override
			public void onDo(Context context, SQLiteDatabase db) {
				c = db.query(
						DbEmployee.TABLE_NAME, 
						new String[]{DbEmployee._ID, DbEmployee.CN_NAME}, 
						DbEmployee.CN_SERVICE + "=?", 
						new String[]{ String.valueOf(serviceId) }, 
						null, null, null, null);
				
				String[] from = new String[]{DbEmployee.CN_NAME};
				int[] to = new int[]{ android.R.id.text1};
				list.setAdapter(new StaffReviewAdapter(
						context, 
						R.layout.item_staff_review, c, 
						from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		if(c!= null)
			c.close();
		super.onDestroy();
	}
	
	public void onClick_Start(View v){
		int employeeId = (Integer)v.getTag();
		if(employeeId==1){
			
		}
	}
}

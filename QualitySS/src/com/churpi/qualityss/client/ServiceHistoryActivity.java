package com.churpi.qualityss.client;

import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceType;
import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ServiceHistoryActivity extends Activity {

	private Cursor c;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_history);
		
		DbTrans.read(this, new DbTrans.Db() {
			
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				c = db.rawQuery(DbQuery.GET_SERVICE_HISTORY_LIST, null);
				return null;
			}
		});
		
		ListView list = (ListView)findViewById(R.id.listView1);
		
		String[] from = new String[]{ 
				DbServiceType.CN_TITLE,
				DbServiceInstance.CN_FINISH_DATETIME,
				DbService.CN_DESCRIPTION
		};
		
		int[] to = new int[]{
				android.R.id.text1,
				android.R.id.text2,
				android.R.id.custom
		};
		
		list.setAdapter(new SimpleCursorAdapter(this,
				R.layout.item_service_history, c, 
				from, to, 
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Ses.getInstance(getApplicationContext()).setServiceInstanceId((int)id);
				startActivityForResult(WorkflowHelper.process(ServiceHistoryActivity.this, R.id.listView1), 0);				
			}
		});
	}
	
	
	
	public void onClick_close(View v){
		this.finish();
	}
	
	@Override
	protected void onDestroy() {
		if(c != null && !c.isClosed()){
			c.close();
		}
		super.onDestroy();
	}

}

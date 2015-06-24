package com.churpi.qualityss.client;

import java.util.ArrayList;
import java.util.List;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbNotification;
import com.churpi.qualityss.client.helper.NotificationListAdapter;
import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;

public class NotificationsActivity extends Activity {

	Cursor c = null;
	NotificationListAdapter adapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications);
		
		if(initCursor()){
			adapter = new NotificationListAdapter(
				this, R.layout.item_notification, c,
				null, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
			ListView list = (ListView)findViewById(R.id.listView1);
			list.setAdapter(adapter);
		}
	}
	
	private boolean initCursor(){
		DbTrans.read(this, new DbTrans.Db() {
			
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				String where = 
						DbNotification.CN_SERVICE + " = ? AND " +
						DbNotification.CN_STATUS + " != 3 AND ";
				List<String> args = new ArrayList<String>();
				args.add(String.valueOf(Ses.getInstance(context).getServiceId()));
				if(getIntent().getAction().compareTo(Constants.ACTION_EMPLOYEE)==0){
					args.add(String.valueOf(Ses.getInstance(context).getEmployeeId()));
					where = where + DbNotification.CN_EMPLOYEE + " = ? ";
				}else if(getIntent().getAction().compareTo(Constants.ACTION_SERVICE)==0){
					where = where + DbNotification.CN_EMPLOYEE + " IS NULL ";
				}
				c = db.query(
						DbNotification.TABLE_NAME, 
						new String[]{ 
							DbNotification._ID,
							DbNotification.CN_TITLE, 
							DbNotification.CN_MESSAGE,
							DbNotification.CN_PRIORITY
						}, where , args.toArray(new String[args.size()]), null, null, 
						DbNotification.CN_PRIORITY + " DESC ");		
				return null;
			}
		});
		if(!c.moveToFirst()){
			this.finish();
			moveNext();
			return false;
		}
		return true;
	}
	
	@Override
	protected void onDestroy() {
		if(c != null)
			c.close();
		super.onDestroy();
	}
	
	public void onClick_Close(View v){
		this.finish();
	}

	private void moveNext(){
		if(getIntent().getAction().compareTo(Constants.ACTION_EMPLOYEE) == 0){
			startActivity(WorkflowHelper.process(this, android.R.id.button1, getIntent().getAction()));
		}else if(getIntent().getAction().compareTo(Constants.ACTION_SERVICE) == 0){
			startActivity(WorkflowHelper.process(this, android.R.id.button2, getIntent().getAction()));
		}
	}
	public void onClick_Next(View v){
		moveNext();
	}
}

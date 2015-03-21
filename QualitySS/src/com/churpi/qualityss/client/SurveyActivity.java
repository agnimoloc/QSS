package com.churpi.qualityss.client;

import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestionAnswer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SurveyActivity extends Activity {

	protected static final String EMPLOYEE_ID = "employeeId";
	public static final String SERVICE_ID = "serviceId";

	int employeeId;
	int serviceId;
	int questionId;
	
	Cursor c;
	SimpleCursorAdapter adapter ;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey);
		
		Bundle extras = getIntent().getExtras();
		employeeId = extras.getInt(EMPLOYEE_ID);
		serviceId = extras.getInt(SERVICE_ID);

		createCursor();
		ListView list = (ListView)findViewById(R.id.listView1);
		
		String[] from = new String[]{ DbQuestion.CN_DESCRIPTION};
		int[] to = new int[]{ android.R.id.text1 };				
		adapter = new SimpleCursorAdapter(this, 
				R.layout.item_survey, c, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		list.setAdapter(adapter);
		list.setOnItemClickListener(selectItem);
	}
	private void createCursor(){
		c = (Cursor)DbTrans.read(this, new DbTrans.Db() {			
			@Override
			public Object onDo(Context context, SQLiteDatabase db) {				
				return db.rawQuery(DbQuery.STAFF_SURVEY, new String[]{
						String.valueOf(employeeId),
						String.valueOf(serviceId)
				});
			}
		});
		if(adapter != null){
			Cursor cur = adapter.swapCursor(c);
			cur.close();
		}
	}
	
	AdapterView.OnItemClickListener selectItem = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			c.moveToPosition(position);
			questionId = c.getInt(c.getColumnIndex(DbQuestion._ID));
			String result = c.getString(c.getColumnIndex(DbSurveyQuestionAnswer.CN_RESULT));
			String question = c.getString(c.getColumnIndex(DbQuestion.CN_DESCRIPTION));
			Intent intent = new Intent(getBaseContext(), CheckpointCommentActivity.class);
			intent.putExtra(CheckpointCommentActivity.FLD_COMMENT, result);
			intent.putExtra(CheckpointCommentActivity.FLD_TEXT, question);
			
			startActivityForResult(intent, 0);
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		
		if(requestCode == 0){
			if(resultCode == RESULT_OK){
				DbTrans.write(this, new DbTrans.Db() {
					@Override
					public Object onDo(Context context, SQLiteDatabase db) {
						String whereClause = DbSurveyQuestionAnswer.CN_SERVICE + "=? AND "
								+ DbSurveyQuestionAnswer.CN_EMPLOYEE + "=? AND "
								+ DbSurveyQuestionAnswer.CN_QUESTION + "=?";
						String[] whereArgs = new String[]{
								String.valueOf(serviceId), 
								String.valueOf(employeeId), 
								String.valueOf(questionId) }; 
						
						Cursor cur = db.query(DbSurveyQuestionAnswer.TABLE_NAME,
								new String[]{ DbSurveyQuestionAnswer.CN_RESULT},
								whereClause, whereArgs, null, null, null);
						String result = data.getStringExtra(CheckpointCommentActivity.FLD_COMMENT);
						boolean saveResult = false;
						if(cur.moveToFirst()){		
							String savedResult = cur.getString(
									cur.getColumnIndex(DbSurveyQuestionAnswer.CN_RESULT));
							if(result != null && result.compareTo(savedResult) != 0){
								saveResult = true;
							}							
						}else if (result != null && result.length() > 0 ){
							saveResult = true;
						}
						cur.close();
						if(saveResult){
							DbEmployee.setStatus(db, employeeId, DbEmployee.EmployeeStatus.CURRENT);
							
							ContentValues values = new ContentValues();
							values.put(DbSurveyQuestionAnswer.CN_RESULT, result);
							int count = db.update(DbSurveyQuestionAnswer.TABLE_NAME, values, 
									whereClause, whereArgs);

							if(count == 0){
								values.put(DbSurveyQuestionAnswer.CN_SERVICE, serviceId);
								values.put(DbSurveyQuestionAnswer.CN_EMPLOYEE, employeeId);
								values.put(DbSurveyQuestionAnswer.CN_QUESTION, questionId);
								db.insert(DbSurveyQuestionAnswer.TABLE_NAME, null, values);
							}
						}
						return null;
					}
				});
				createCursor();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	};
	
	protected void onDestroy() {
		if(c != null){
			c.close();
		}
		super.onDestroy();
	};
	
	public void onClick_previous(View v){
		finish();
	}

}

package com.churpi.qualityss.client;

import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestionAnswer;
import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SurveyActivity extends Activity {

	private static final int REQUEST_SURVEY_RESPONSE = 0;
	private static final int REQUEST_SURVEY_COMMENT = 1;
	private static final int REQUEST_SURVEY_COMMENTS = 2;


	int employeeInstanceId;
	int serviceInstanceId;
	int questionId;
	String questionComment;

	Cursor c;
	SimpleCursorAdapter adapter ;
	ActionMode mActionMode;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey);

		employeeInstanceId = Ses.getInstance(this).getEmployeeInstanceId();
		serviceInstanceId = Ses.getInstance(this).getServiceInstanceId();

		createCursor();
		ListView list = (ListView)findViewById(R.id.listView1);

		String[] from = new String[]{ DbQuestion.CN_DESCRIPTION, DbSurveyQuestionAnswer.CN_RESULT };
		int[] to = new int[]{ android.R.id.text1, android.R.id.text2 };				
		adapter = new SimpleCursorAdapter(this, 
				R.layout.item_survey, c, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		list.setAdapter(adapter);
		list.setOnItemClickListener(selectItem);
		registerForContextMenu(list);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {


		getMenuInflater().inflate(R.menu.comment_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_comments) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			c.moveToPosition(info.position);
			questionId = c.getInt(c.getColumnIndex(DbQuestion._ID));
			String comment = c.getString(c.getColumnIndex(DbSurveyQuestionAnswer.CN_COMMENT));

			WorkflowHelper.getComments(SurveyActivity.this, comment, getString(R.string.inst_comment_survey), REQUEST_SURVEY_COMMENT);
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.comment_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_comments) {
			addComments();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void createCursor(){
		c = (Cursor)DbTrans.read(this, new DbTrans.Db() {			
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {				
				return db.rawQuery(DbQuery.STAFF_SURVEY, new String[]{
						String.valueOf(employeeInstanceId)
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

			WorkflowHelper.getComments(SurveyActivity.this, result, question, REQUEST_SURVEY_RESPONSE);
		}
	};

	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		if((requestCode == REQUEST_SURVEY_COMMENT || requestCode == REQUEST_SURVEY_RESPONSE) && resultCode == RESULT_OK){
			DbTrans.write(this, data, new DbTrans.Db() {
				@Override
				public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
					Intent data = (Intent)parameter;
					String whereClause = DbSurveyQuestionAnswer.CN_EMPLOYEE_INSTANCE + "=? AND "
							+ DbSurveyQuestionAnswer.CN_QUESTION + "=?";
					String[] whereArgs = new String[]{
							String.valueOf(employeeInstanceId), 
							String.valueOf(questionId) }; 

					String result = data.getStringExtra(CheckpointCommentActivity.FLD_COMMENT);

					ContentValues values = new ContentValues();
					if(requestCode == REQUEST_SURVEY_RESPONSE) 
						values.put(DbSurveyQuestionAnswer.CN_RESULT, result);
					else if (requestCode == REQUEST_SURVEY_COMMENT )
						values.put(DbSurveyQuestionAnswer.CN_COMMENT, result);

					int count = db.update(DbSurveyQuestionAnswer.TABLE_NAME, values, 
							whereClause, whereArgs);

					if(count == 0){
						DbEmployeeInstance.setStatus(db, employeeInstanceId, DbEmployeeInstance.EmployeeStatus.CURRENT);

						values.put(DbSurveyQuestionAnswer.CN_EMPLOYEE_INSTANCE, employeeInstanceId);
						values.put(DbSurveyQuestionAnswer.CN_QUESTION, questionId);
						db.insert(DbSurveyQuestionAnswer.TABLE_NAME, null, values);
					}
					return null;
				}
			});
			createCursor();
		} else if(requestCode == REQUEST_SURVEY_COMMENTS && resultCode == RESULT_OK){
			Bundle extras = data.getExtras();
			updateSurveyComment(extras.getString(CheckpointCommentActivity.FLD_COMMENT));
			Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show();
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

	private void updateSurveyComment(String comment){
		DbTrans.write(this, comment, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				String comment = (String)parameter;
				ContentValues values = new ContentValues();
				values.put(DbEmployeeInstance.CN_SURVEY_COMMENT, comment);
				db.update(DbEmployeeInstance.TABLE_NAME, 
						values, 
						DbEmployeeInstance._ID + "=?", 
						new String[]{String.valueOf(employeeInstanceId)});
				return null;
			}
		});
	}

	private void addComments(){
		String comment = (String) DbTrans.read(this, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				Cursor cur = db.query(
						DbEmployeeInstance.TABLE_NAME, 
						new String[]{DbEmployeeInstance.CN_SURVEY_COMMENT}, 
						DbEmployeeInstance._ID + "=?", 
						new String[]{
								String.valueOf(employeeInstanceId)
						}, null, null, null);
				if(cur.moveToFirst()){
					return cur.getString(cur.getColumnIndex(DbEmployeeInstance.CN_SURVEY_COMMENT));
				}
				cur.close();	
				return null;
			}
		});
		WorkflowHelper.getComments(this, 
				comment, getString(R.string.inst_comment_survey_result), 
				REQUEST_SURVEY_COMMENTS);
	}

}

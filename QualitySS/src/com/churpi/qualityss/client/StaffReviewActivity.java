package com.churpi.qualityss.client;

import java.util.ArrayList;
import java.util.List;

import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswer;
import com.churpi.qualityss.client.dto.QuestionDTO;
import com.churpi.qualityss.client.helper.Alerts;
import com.churpi.qualityss.client.helper.StaffReviewQuestionAdapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class StaffReviewActivity extends Activity {

	protected static final String EMPLOYEE_ID = "employeeId";
	protected static final String EMPLOYEE_NAME = "employeeName";
	public static final String SERVICE_ID = "serviceId";

	int employeeId;
	int serviceId;
	String employeeName;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_review);
		
		Bundle extras = getIntent().getExtras();
		serviceId = extras.getInt(SERVICE_ID);
		employeeId = extras.getInt(EMPLOYEE_ID);
		employeeName = extras.getString(EMPLOYEE_NAME);
		
		@SuppressWarnings("unchecked")
		List<QuestionDTO> questions = (List<QuestionDTO>)DbTrans.read(this, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, SQLiteDatabase db) {
				Cursor c = db.rawQuery(DbQuery.STAFF_REVIEW, new String[]{String.valueOf(employeeId), String.valueOf(serviceId)});
				List<QuestionDTO> questions = new ArrayList<QuestionDTO>(); 
				if(c.moveToFirst()){
					do{
						QuestionDTO question = new QuestionDTO();
						question.fillFromCursor(c);
						questions.add(question);
					}while(c.moveToNext());
				}
				return questions;
			}
		});
		
		ExpandableListView list = (ExpandableListView)findViewById(R.id.expandableListView1);
		list.setAdapter(new StaffReviewQuestionAdapter(this, 
				questions.toArray(new QuestionDTO[0]), 
				R.id.checkBox1, R.id.checkBox2, R.id.checkBox3));
	}
	
	public void onClick_radio(View v){
		RadioGroup group = (RadioGroup) v.getParent();
		final QuestionDTO currentQuestion = (QuestionDTO)group.getTag();
		
		RadioButton radio = (RadioButton)group.findViewById(R.id.checkBox1);
		
		
		if(!radio.isChecked()){
			radio = (RadioButton)group.findViewById(R.id.checkBox2);
			if(!radio.isChecked()){
				radio = (RadioButton)group.findViewById(R.id.checkBox3);
			}
		}
		final String value = radio.getTag().toString();
		currentQuestion.setResultado(value);
		
		DbTrans.write(this, new DbTrans.Db() {
			
			@Override
			public Object onDo(Context context, SQLiteDatabase db) {
				
				DbEmployee.setStatus(db, employeeId, DbEmployee.EmployeeStatus.CURRENT);
				
				ContentValues values = new ContentValues();
				values.put(DbReviewQuestionAnswer.CN_RESULT, value);
				int count = db.update(DbReviewQuestionAnswer.TABLE_NAME, 
						values, 
						DbReviewQuestionAnswer.CN_SERVICE + " =? AND "+ 
						DbReviewQuestionAnswer.CN_EMPLOYEE + " =? AND "+
						DbReviewQuestionAnswer.CN_QUESTION + " =?"
						, new String[]{
							String.valueOf(serviceId),
							String.valueOf(employeeId),
							String.valueOf(currentQuestion.getPreguntaId()),
						}
				);
				if(count == 0){
					values.put(DbReviewQuestionAnswer.CN_SERVICE , serviceId);
					values.put(DbReviewQuestionAnswer.CN_EMPLOYEE , employeeId);
					values.put(DbReviewQuestionAnswer.CN_QUESTION , currentQuestion.getPreguntaId());
					db.insert(DbReviewQuestionAnswer.TABLE_NAME, null, values);
				}
				return null;
			}
		});
		
	}
	
	public void onClick_previous(View v){
		
		finish();
	}
	
	public void onClick_finish(View v){
		boolean finish = (Boolean)DbTrans.write(this, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, SQLiteDatabase db) {
				String msg = null;
				Cursor cur = db.rawQuery(DbQuery.STAFF_INVENTORY_NULL_RESULT,  
						new String[]{String.valueOf(employeeId)});
				if(cur.getCount() > 0){
					msg = getString(R.string.msg_fault_staff_inventory);
				}
				cur.close();
				cur = db.rawQuery(DbQuery.STAFF_REVIEW_NULL_RESULT,
						new String[]{String.valueOf(employeeId),String.valueOf(serviceId)
				});
				if(cur.getCount() > 0){
					msg = (msg != null ? msg + "\n":"")+ getString(R.string.msg_fault_staff_review);
				}
				cur.close();
				if(msg == null){
					DbEmployee.setStatus(db, employeeId, DbEmployee.EmployeeStatus.FINALIZED);
					return true;
				}else{					
					Alerts.showError(context, msg, R.string.msg_cannot_finish_staff_review);
				}
				return false;
			}
		}); 
		if(finish){
			Intent intent = new Intent(getApplicationContext(), StaffReviewListActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(StaffReviewListActivity.FLD_SERVICE_ID, serviceId);
			startActivity(intent);
		}
	}

	public void onClick_survey(View v){
		
		Intent intent = new Intent(this, SurveyActivity.class);
		intent.putExtra(SurveyActivity.SERVICE_ID, serviceId);
		intent.putExtra(SurveyActivity.EMPLOYEE_ID, employeeId);		
		
		startActivity(intent);
	}
}

package com.churpi.qualityss.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance;
import com.churpi.qualityss.client.dto.QuestionDTO;
import com.churpi.qualityss.client.helper.Alerts;
import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.StaffReviewQuestionAdapter;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class StaffReviewActivity extends Activity {

	public static final String ACTION_EMPLOYEE = "employee";
	public static final String ACTION_SERVICE = "service";

	int employeeId;
	int serviceInstanceId;
	String employeeName;
	QuestionDTO currentQuestion;

	private static final int REQUEST_PHOTO = 0;
	private static final int REQUEST_IMAGE_SHOW = 1;
	private static final int REQUEST_SINGLE_COMMENT = 2;
	private static final int REQUEST_SINGLE_COMMENTS = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_review);

		serviceInstanceId = Ses.getInstance(this).getServiceInstanceId();
		Button button = (Button)findViewById(R.id.button4);
		if(getIntent().getAction().compareTo(ACTION_EMPLOYEE)==0){
			employeeId = Ses.getInstance(this).getEmployeeId();
			employeeName = Ses.getInstance(this).getEmployeeName();
			button.setVisibility(View.GONE);			
		}else{
			button = (Button)findViewById(android.R.id.button1);
			button.setVisibility(View.GONE);
			button = (Button)findViewById(android.R.id.button3);
			button.setVisibility(View.GONE);
		}
		

		@SuppressWarnings("unchecked")
		List<QuestionDTO> questions = (List<QuestionDTO>)DbTrans.read(this, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				Cursor c = null;
				if(getIntent().getAction().compareTo(ACTION_EMPLOYEE)==0){
					c = db.rawQuery(DbQuery.STAFF_REVIEW, 
							new String[]{
							String.valueOf(employeeId), 
							String.valueOf(serviceInstanceId)
					}
							);
				}else if(getIntent().getAction().compareTo(ACTION_SERVICE)==0){
					c = db.rawQuery(DbQuery.SERVICE_REVIEW, 
							new String[]{
							String.valueOf(serviceInstanceId)
					}
							);

				}
				List<QuestionDTO> questions = new ArrayList<QuestionDTO>(); 
				if(c.moveToFirst()){
					do{
						QuestionDTO question = new QuestionDTO();
						question.fillFromCursor(c);
						questions.add(question);
					}while(c.moveToNext());
				}else{
					moveNext();
					finish();
				}
				return questions;
			}
		});

		ExpandableListView list = (ExpandableListView)findViewById(R.id.expandableListView1);
		list.setAdapter(new StaffReviewQuestionAdapter(this, 
				questions.toArray(new QuestionDTO[0]), 
				R.id.checkBox1, R.id.checkBox2, R.id.checkBox3));
		list.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;//Disabling collapse and expand
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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

	public void onClick_radio(View v){
		RadioGroup group = (RadioGroup) v.getParent();
		currentQuestion = (QuestionDTO)((View)group.getParent()).getTag();

		RadioButton radio = (RadioButton)group.findViewById(R.id.checkBox1);


		if(!radio.isChecked()){
			radio = (RadioButton)group.findViewById(R.id.checkBox2);
			if(!radio.isChecked()){
				radio = (RadioButton)group.findViewById(R.id.checkBox3);
			}
		}
		currentQuestion.setResultado(radio.getTag().toString());
		updateItem(currentQuestion);		
	}

	public void onClick_previous(View v){
		finish();
	}

	public void onClick_finish(View v){
		boolean finish = (Boolean)DbTrans.write(this, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				String msg = null;
				Cursor cur = db.rawQuery(DbQuery.STAFF_INVENTORY_NULL_RESULT,  
						new String[]{String.valueOf(employeeId)});
				if(cur.getCount() > 0){
					msg = getString(R.string.msg_fault_staff_inventory);
				}
				cur.close();
				cur = db.rawQuery(DbQuery.STAFF_REVIEW_NULL_RESULT,
						new String[]{String.valueOf(employeeId),String.valueOf(serviceInstanceId)
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
			moveNext();
		}
	}

	public void onClick_survey(View v){				
		startActivity(
				WorkflowHelper.process(this, 
						android.R.id.button1
						)
				);
	}

	public void onClick_comment(View v){
		currentQuestion = (QuestionDTO)((View)((View)v.getParent()).getParent()).getTag();		
		WorkflowHelper.getComments(this, 
				currentQuestion.getComentarios(), 
				getString(R.string.inst_comment_review), 
				REQUEST_SINGLE_COMMENT);

	}
	
	public void onClick_takephoto(View v){
		currentQuestion = (QuestionDTO)((View)((View)v.getParent()).getParent()).getTag();		
		File dest = getDestImage(currentQuestion.getPreguntaId());
		if(dest.exists()){
			Intent showPhotoIntent = new Intent(this, ShowPhotoActivity.class);
			showPhotoIntent.putExtra(ShowPhotoActivity.FILE_URI, Uri.fromFile(dest));
			showPhotoIntent.putExtra(ShowPhotoActivity.SHOW_NEW_BUTTON, true);
			startActivityForResult(showPhotoIntent,REQUEST_IMAGE_SHOW);
		}else{
			WorkflowHelper.takePhoto(this, dest, REQUEST_PHOTO);
		}
	}
	
	public void onClick_next(View v){
		moveNext();
	}
	
	private void moveNext(){
		startActivity(
				WorkflowHelper.process(this, android.R.id.button3)
				);
	}

	public void updateItem(QuestionDTO currentQuestion){
		DbTrans.write(this, currentQuestion, new DbTrans.Db() {

			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				QuestionDTO currentQuestion = (QuestionDTO)parameter;

				if(getIntent().getAction().compareTo(ACTION_EMPLOYEE)==0){
					DbEmployee.setStatus(db, employeeId, DbEmployee.EmployeeStatus.CURRENT);

					ContentValues values = new ContentValues();
					if(currentQuestion.isResultado())
						values.put(DbReviewQuestionAnswerEmployee.CN_RESULT, currentQuestion.getResultado());
					else
						values.put(DbReviewQuestionAnswerEmployee.CN_COMMENT, currentQuestion.getComentarios());

					int count = db.update(DbReviewQuestionAnswerEmployee.TABLE_NAME, 
							values, 
							DbReviewQuestionAnswerEmployee.CN_SERVICE_INSTANCE + " =? AND "+ 
									DbReviewQuestionAnswerEmployee.CN_EMPLOYEE + " =? AND "+
									DbReviewQuestionAnswerEmployee.CN_QUESTION + " =?"
									, new String[]{
							String.valueOf(serviceInstanceId),
							String.valueOf(employeeId),
							String.valueOf(currentQuestion.getPreguntaId()),
					}
							);
					if(count == 0){
						values.put(DbReviewQuestionAnswerEmployee.CN_SERVICE_INSTANCE , serviceInstanceId);
						values.put(DbReviewQuestionAnswerEmployee.CN_EMPLOYEE , employeeId);
						values.put(DbReviewQuestionAnswerEmployee.CN_QUESTION , currentQuestion.getPreguntaId());
						db.insert(DbReviewQuestionAnswerEmployee.TABLE_NAME, null, values);
					}
				}else{
					ContentValues values = new ContentValues();
					if(currentQuestion.isResultado())
						values.put(DbReviewQuestionAnswerService.CN_RESULT, currentQuestion.getResultado());
					else
						values.put(DbReviewQuestionAnswerService.CN_COMMENT, currentQuestion.getComentarios());

					int count = db.update(DbReviewQuestionAnswerService.TABLE_NAME, 
							values, 
							DbReviewQuestionAnswerService.CN_SERVICE_INSTANCE + " =? AND "+ 
									DbReviewQuestionAnswerService.CN_QUESTION + " =?"
									, new String[]{
							String.valueOf(serviceInstanceId),
							String.valueOf(currentQuestion.getPreguntaId()),
					}
							);
					if(count == 0){
						values.put(DbReviewQuestionAnswerService.CN_SERVICE_INSTANCE , serviceInstanceId);
						values.put(DbReviewQuestionAnswerService.CN_QUESTION , currentQuestion.getPreguntaId());
						db.insert(DbReviewQuestionAnswerService.TABLE_NAME, null, values);
					}
					
				}
				return null;
			}
		});

	}

	private void updateReviewComment(String comment){
		DbTrans.write(this, comment, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				String comment = (String)parameter;
				ContentValues values = new ContentValues();
				if(getIntent().getAction().compareTo(ACTION_EMPLOYEE)==0){
					values.put(DbEmployee.CN_REVIEW_COMMENT, comment);
					db.update(DbEmployee.TABLE_NAME, 
							values, 
							DbEmployee._ID + "=?", 
							new String[]{String.valueOf(Ses.getInstance(context).getEmployeeId())});
				}else{
					values.put(DbServiceInstance.CN_REVIEW_COMMENT, comment);
					db.update(DbServiceInstance.TABLE_NAME, 
							values, 
							DbServiceInstance._ID + "=?", 
							new String[]{String.valueOf(Ses.getInstance(context).getServiceInstanceId())});					
				}
				return null;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_PHOTO && resultCode == RESULT_OK){
			Toast.makeText(this, getString(R.string.msg_photo_take_successfully), Toast.LENGTH_SHORT).show();
		} else if(requestCode == REQUEST_IMAGE_SHOW && resultCode == RESULT_OK){
			File dest = new File(((Uri)data.getExtras().get(ShowPhotoActivity.FILE_URI)).getPath());
			WorkflowHelper.takePhoto(this, dest, REQUEST_PHOTO);
		} else if(requestCode == REQUEST_SINGLE_COMMENT && resultCode == RESULT_OK){
			Bundle extras = data.getExtras();
			currentQuestion.setComentarios(extras.getString(CheckpointCommentActivity.FLD_COMMENT));
			updateItem(currentQuestion);
			Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show();
		} else if(requestCode == REQUEST_SINGLE_COMMENTS && resultCode == RESULT_OK){
			Bundle extras = data.getExtras();
			updateReviewComment(extras.getString(CheckpointCommentActivity.FLD_COMMENT));
			Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show();
		}		
		super.onActivityResult(requestCode, resultCode, data);
	}

	private File getDestImage(int id){
		String fileName = null;
		String action = getIntent().getAction();
		if(action.compareTo(ACTION_EMPLOYEE)== 0){
			fileName = String.format(Constants.PHOTO_REVIEW_EMPLOYEE ,
					Ses.getInstance(this).getServiceInstanceKey(),
					Ses.getInstance(this).getEmployeeId(),
					id);
		}else if(action.compareTo(ACTION_SERVICE)== 0){
			fileName = String.format(Constants.PHOTO_REVIEW_SERVICE ,
					Ses.getInstance(this).getServiceInstanceKey(),
					id);			
		}

		return new File (getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
	}

	private void addComments(){
		String comment = (String) DbTrans.read(this, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				if(getIntent().getAction().compareTo(ACTION_EMPLOYEE)==0){
					Cursor cur = db.query(
							DbEmployee.TABLE_NAME, 
							new String[]{DbEmployee.CN_REVIEW_COMMENT}, 
							DbEmployee._ID + "=?", 
							new String[]{
									String.valueOf(Ses.getInstance(context).getEmployeeId())
							}, null, null, null);
					if(cur.moveToFirst()){
						return cur.getString(cur.getColumnIndex(DbEmployee.CN_REVIEW_COMMENT));
					}
					cur.close();	
				}else{
					Cursor cur = db.query(
							DbServiceInstance.TABLE_NAME, 
							new String[]{DbServiceInstance._ID ,DbServiceInstance.CN_REVIEW_COMMENT}, 
							DbServiceInstance._ID + "=?", 
							new String[]{
									String.valueOf(Ses.getInstance(context).getServiceInstanceId())
							}, null, null, null);
					if(cur.moveToFirst()){
						return cur.getString(cur.getColumnIndex(DbServiceInstance.CN_REVIEW_COMMENT));
					}
					cur.close();
				}
				return null;
			}
		});
		WorkflowHelper.getComments(this, 
				comment, getString(R.string.inst_comment_review_result), 
				REQUEST_SINGLE_COMMENTS);
	}
}

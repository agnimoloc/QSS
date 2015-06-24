package com.churpi.qualityss.client;

import java.io.File;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestionAnswer;
import com.churpi.qualityss.client.dto.ServiceInstanceDTO;
import com.churpi.qualityss.client.helper.Alerts;
import com.churpi.qualityss.client.helper.DateHelper;
import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SummaryActivity extends Activity {

	private LinearLayout layout; 
	
	private int serviceInstanceId;
	public String mAction;
	private LayoutInflater mInflater;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);

		layout = (LinearLayout)findViewById(R.id.LinearLayout2);
		
		serviceInstanceId = Ses.getInstance(getBaseContext()).getServiceInstanceId();

		mAction = getIntent().getAction();
		
		mInflater = this.getLayoutInflater();
		

		DbTrans.read(this, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				if(mAction != null && mAction.compareTo(Constants.ACTION_EMPLOYEE)==0){
					Cursor c = db.rawQuery(DbQuery.GET_EMPLOYEE_DATA,   
							new String[]{String.valueOf(Ses.getInstance(context).getEmployeeInstanceId())});
					if(c.moveToFirst()){
						do{
							SummaryActivity.this.setTitle(getString(R.string.title_activity_summary, c.getString(c.getColumnIndex(DbEmployee.CN_NAME))));
							LinearLayout employeeLayout = (LinearLayout)mInflater.inflate(R.layout.summary_employee, layout, false);							
							fillLayoutEmployee(db, c, employeeLayout);
							layout.addView(employeeLayout);
						}while(c.moveToNext());
					}
					c.close();
				}else if (mAction == null || mAction.compareTo(Constants.ACTION_SERVICE)==0){
					LinearLayout serviceLayout = (LinearLayout)mInflater.inflate(R.layout.summary_service, layout, false);
					fillLayoutService(db, serviceLayout);
					layout.addView(serviceLayout);
				}
				return null;
			}
		});
		
		if(mAction == null ){
			Button button = (Button)findViewById(R.id.button2);
			button.setVisibility(View.GONE);
		}

	}
	
	public void onClick_Finish(View v){
		
		if(mAction != null && mAction.compareTo(Constants.ACTION_EMPLOYEE)==0){
			finishEmployee();
		}else if (mAction != null && mAction.compareTo(Constants.ACTION_SERVICE)==0){
			finishService();
			
		}
	}
	public void onClick_Cancel(View v){
		this.finish();
	}
	
	private void addComment(int resourceTitleId, String comment, LinearLayout parent){
		
		LinearLayout item =  (LinearLayout)mInflater.inflate(R.layout.summary_comment, parent, false);
		
		TextView titleComment = (TextView)item.findViewById(android.R.id.text1);
		titleComment.setText(resourceTitleId);		
		TextView titleReview = (TextView)item.findViewById(android.R.id.text2);
		titleReview.setText(comment);
		
		parent.addView(item);
	}
	private void fillInventory(Cursor c, String resultColumnName, String commentColumnName, LinearLayout parent, boolean isService){
		if(c.moveToFirst()){
			TextView titleInventory = (TextView)parent.findViewById(android.R.id.text1);
			if(isService){
				titleInventory.setText(R.string.sum_inventory_service);
			}else{
				titleInventory.setText(R.string.sum_inventory_employee);				
			}			
			do{
				LinearLayout item =  (LinearLayout)mInflater.inflate(R.layout.summary_list_item, parent, false);
				parent.addView(item);				
				/*TextView equipmentFault = new TextView(this);
				equipmentFault.setTextAppearance(this, android.R.style.TextAppearance_Medium);*/
				TextView equipmentFault = (TextView)item.findViewById(android.R.id.text1);				
				String result = 
						c.getString(c.getColumnIndex(DbEquipment.CN_DESCRIPTION)) +
						" [" + getString(R.string.sum_result) + " " +
						(c.getInt(c.getColumnIndex(resultColumnName)) == 0 ?
								getString(R.string.no):getString(R.string.yes)) + "]";
				equipmentFault.setText(result);
				/*layout.addView(equipmentFault);*/	
				String comment =c.getString(c.getColumnIndex(commentColumnName));
				
				TextView equipmentComment = (TextView)item.findViewById(android.R.id.text2);
				if(comment != null && comment.trim().length() > 0){
					/*TextView equipmentComment = new TextView(this);
					equipmentComment.setTextAppearance(this, android.R.style.TextAppearance_Small);*/
					equipmentComment.setText(getString(R.string.sum_comment) + " " + comment);
					/*layout.addView(equipmentComment);*/				
				}else{
					equipmentComment.setVisibility(View.GONE);
				}
			}while(c.moveToNext());
		}
	}
	
	private void fillReview(Cursor c, String resultColumnName, String commentColumnName, LinearLayout parent){
		if(c.moveToFirst()){
			String section = "";
			do{
				String currentSection = c.getString(c.getColumnIndex(DbReviewQuestion.CN_SECTION_NAME));
				if(currentSection.compareTo(section)!= 0){
					section = currentSection;
					LinearLayout title =  (LinearLayout)mInflater.inflate(R.layout.summary_list_title, parent, false);
					TextView sectionText = (TextView)title.findViewById(android.R.id.text1);
					sectionText.setText(currentSection);
					parent.addView(title);
				}
				LinearLayout item =  (LinearLayout)mInflater.inflate(R.layout.summary_list_item, parent, false);
				TextView equipmentFault = (TextView)item.findViewById(android.R.id.text1);
				equipmentFault.setTextAppearance(this, android.R.style.TextAppearance_Medium);
				
				String txtResult = c.getString(c.getColumnIndex(resultColumnName));
				
				if(txtResult != null){
					if(txtResult.compareTo("E")==0){
						txtResult = getString(R.string.excelent);
					}else if(txtResult.compareTo("G")==0){
						txtResult = getString(R.string.good);
					}else if(txtResult.compareTo("B")==0){
						txtResult = getString(R.string.bad);
					}
				}else{
					txtResult = getString(R.string.sum_no_response);
				}
				
				String result = 
						c.getString(c.getColumnIndex(DbQuestion.CN_DESCRIPTION)) +
						" [" + getString(R.string.sum_result) + " " +
						txtResult + "]";
				equipmentFault.setText(result);
				
				String comment =c.getString(c.getColumnIndex(commentColumnName));
				if(comment != null && comment.trim().length() > 0){					
					TextView questionReview = (TextView)item.findViewById(android.R.id.text2);
					questionReview.setText(getString(R.string.sum_comment) + " " + comment);									
				}
				parent.addView(item);
			}while(c.moveToNext());
		}

	}
	
	private void fillSurvey(Cursor c, String resultColumnName, String commentColumnName, LinearLayout parent){
		if(c.moveToFirst()){
			String section = "";
			do{
				String question = c.getString(c.getColumnIndex(DbQuestion.CN_DESCRIPTION));
				if(question.compareTo(section)!= 0){					
					LinearLayout title =  (LinearLayout)mInflater.inflate(R.layout.summary_list_title, parent, false);				
					TextView questionText = (TextView)title.findViewById(android.R.id.text1);
					questionText.setText(question);
					parent.addView(title);
				}
				String result =c.getString(c.getColumnIndex(resultColumnName));
				LinearLayout item =  (LinearLayout)mInflater.inflate(R.layout.summary_list_item, parent, false);		
				TextView questionResult = (TextView)item.findViewById(android.R.id.text1);
				if(result != null && result.trim().length() > 0){
					questionResult.setText(result);
				}else{
					questionResult.setText("[" + getString(R.string.sum_no_response)+"]");
				}
				String comment =c.getString(c.getColumnIndex(commentColumnName));
				TextView questionComment = (TextView)item.findViewById(android.R.id.text2);
				if(comment != null && comment.trim().length() > 0){					
					questionComment.setText(getString(R.string.sum_comment)+" "+comment);
				}else{
					questionComment.setVisibility(View.GONE);
				}
				parent.addView(item);				
			}while(c.moveToNext());
		}

	}

	private void fillLayoutEmployee(SQLiteDatabase db, Cursor c, LinearLayout parent){
		
		boolean nonData = true;
		
		int employeeInstanceId = c.getInt(c.getColumnIndex(DbEmployeeInstance._ID));
		String inventoryComment = c.getString(c.getColumnIndex(DbEmployeeInstance.CN_INVENTORY_COMMENT));
		String reviewComment = c.getString(c.getColumnIndex(DbEmployeeInstance.CN_REVIEW_COMMENT));
		String surveyComment = c.getString(c.getColumnIndex(DbEmployeeInstance.CN_SURVEY_COMMENT));
		
		Cursor cInventory = db.rawQuery(DbQuery.EMPLOYEE_INVENTORY_FAULT, 
				new String[]{ String.valueOf(employeeInstanceId)});
		if(cInventory.moveToFirst()){
			nonData = false;
			LinearLayout inventoryLayout = (LinearLayout)mInflater.inflate(R.layout.summary_inventory, parent, false);
			fillInventory(cInventory, DbEmployeeEquipmentInventory.CN_CHECKED, DbEmployeeEquipmentInventory.CN_COMMENT, inventoryLayout, false);
			parent.addView(inventoryLayout);
		}
		cInventory.close();
		
		if(inventoryComment != null){
			nonData = false;
			addComment(R.string.sum_inventory_comment, inventoryComment, parent);
		}
		
		Cursor cReview = db.rawQuery(DbQuery.EMPLOYEE_REVIEW_SUMMARY, 
				new String[]{ String.valueOf(employeeInstanceId)});
		if(cReview.moveToFirst()){
			nonData = false;
			LinearLayout reviewLayout = (LinearLayout)mInflater.inflate(R.layout.summary_review, parent, false);
			fillReview(cReview, DbReviewQuestionAnswerEmployee.CN_RESULT, DbReviewQuestionAnswerEmployee.CN_COMMENT, reviewLayout);
			parent.addView(reviewLayout);
		}
		cReview.close();
		
		if(reviewComment != null){
			nonData = false;
			addComment(R.string.sum_review_comment, reviewComment, parent);
		}

		Cursor cSurvey = db.rawQuery(DbQuery.EMPLOYEE_SURVEY_SUMMARY, 
				new String[]{ String.valueOf(employeeInstanceId)});
		if(cSurvey.moveToFirst()){
			LinearLayout surveyLayout = (LinearLayout)mInflater.inflate(R.layout.summary_survey, parent, false);
			fillSurvey(cSurvey, DbSurveyQuestionAnswer.CN_RESULT, DbSurveyQuestionAnswer.CN_COMMENT, surveyLayout);
			parent.addView(surveyLayout);
		}
		cSurvey.close();
		
		if(surveyComment != null){
			nonData = false;
			addComment(R.string.sum_survey_comment, surveyComment, parent);
		}
		
		if(nonData){
			addNoData(parent);	
		}

	}
	private void finishEmployee(){
		boolean finish = (Boolean)DbTrans.write(this, Ses.getInstance(this).getEmployeeInstanceId(), new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				int employeeInstanceId = (Integer)parameter;
				String msg = null;
				Cursor cur = db.rawQuery(DbQuery.STAFF_INVENTORY_NULL_RESULT,  
						new String[]{String.valueOf(employeeInstanceId)});
				if(cur.getCount() > 0){
					msg = getString(R.string.msg_fault_staff_inventory);
				}
				cur.close();
				cur = db.rawQuery(DbQuery.STAFF_REVIEW_NULL_RESULT,
						new String[]{String.valueOf(employeeInstanceId)
				});
				if(cur.getCount() > 0){
					msg = (msg != null ? msg + "\n":"")+ getString(R.string.msg_fault_staff_review);
				}
				cur.close();
				if(msg == null){
					DbEmployeeInstance.setStatus(db, employeeInstanceId, DbEmployeeInstance.EmployeeStatus.FINALIZED);
					return true;
				}else{					
					Alerts.showError(context, msg, R.string.msg_cannot_finish_staff_review);
				}
				return false;
			}
		}); 
		if(finish){
			startActivity(WorkflowHelper.process(this,android.R.id.button1, mAction));
		}
	}
	
	private void fillLayoutService(SQLiteDatabase db, LinearLayout parent){

		boolean nonData = true;
		Cursor cServiceInstance = db.query(DbServiceInstance.TABLE_NAME, null, DbServiceInstance._ID + " = ? ", 
				new String[]{String.valueOf(serviceInstanceId)}, null, null, null);
		ServiceInstanceDTO serviceInstance = new ServiceInstanceDTO();
		if(cServiceInstance.moveToFirst()){			
			serviceInstance.fillFromCursor(cServiceInstance);
		}else{
			return;
		}
		cServiceInstance.close();

		Cursor cService = db.query(DbService.TABLE_NAME, 
				new String[]{ DbService.CN_DESCRIPTION, DbService.CN_CODE }, 
				DbService._ID + " = ? ", 
				new String[]{String.valueOf(serviceInstance.getServicioId())},
				null, null, null);
		
		cService.moveToFirst();
		
		
		File dir = new File(
				getDir(Constants.IMG_SERVICE, Context.MODE_PRIVATE), 
				"S" + cService.getString(cService.getColumnIndex(DbService.CN_CODE)).trim() + ".jpg");
		ImageView img = (ImageView)parent.findViewById(android.R.id.icon);
		if(dir.exists()){
			img.setImageURI(Uri.fromFile(dir));
		}else{
			img.setImageResource(R.drawable.no_image_service);
		}
		
		String serviceName = cService.getString(cService.getColumnIndex(DbService.CN_DESCRIPTION));
		TextView text = (TextView)parent.findViewById(android.R.id.text1);
		text.setText(serviceName);
		this.setTitle(getString(R.string.title_activity_summary, serviceName));
		
		cService.close();
		
		Cursor cInventory = db.rawQuery(DbQuery.SERVICE_INVENTORY_FAULT, 
				new String[]{ String.valueOf(serviceInstanceId)});
		if(cInventory.moveToFirst()){
			nonData = false;
			LinearLayout inventoryLayout = (LinearLayout)mInflater.inflate(R.layout.summary_inventory, parent, false);		
			fillInventory(cInventory, DbServiceEquipmentInventory.CN_CHECKED, DbServiceEquipmentInventory.CN_COMMENT, inventoryLayout, true);			
			parent.addView(inventoryLayout);
		}
		cInventory.close();
		
		if(serviceInstance.getComentariosInventario() != null){
			nonData = false;
			addComment(R.string.sum_inventory_comment, serviceInstance.getComentariosInventario(), parent);
		}
		
		Cursor cReview = db.rawQuery(DbQuery.SERVICE_REVIEW_SUMMARY, 
				new String[]{ String.valueOf(serviceInstanceId)});
		if(cReview.moveToFirst()){
			nonData = false;
			LinearLayout reviewLayout = (LinearLayout)mInflater.inflate(R.layout.summary_review, parent, false);
			fillReview(cReview, DbReviewQuestionAnswerService.CN_RESULT, DbReviewQuestionAnswerService.CN_COMMENT, reviewLayout);
			parent.addView(reviewLayout);
		}
		cReview.close();

		
		if(serviceInstance.getComentariosCheckList() != null){
			nonData = false;
			addComment(R.string.sum_review_comment, serviceInstance.getComentariosCheckList(), parent);
		}

		Cursor cEmployee = db.rawQuery(DbQuery.EMPLOYEES_SERVICE_FINISHED, 
				new String[]{ String.valueOf(serviceInstanceId)});
		if(cEmployee.moveToFirst()){
			nonData = false;
			do{
				LinearLayout employeeLayout = (LinearLayout)mInflater.inflate(R.layout.summary_employee, parent, false);	
				dir = new File(
						getDir(Constants.IMG_EMPLOYEE, Context.MODE_PRIVATE), 
						cEmployee.getString(cEmployee.getColumnIndex(DbEmployee.CN_CODE)).trim() + ".jpg");
				img = (ImageView)employeeLayout.findViewById(android.R.id.icon);
				if(dir.exists()){
					img.setImageURI(Uri.fromFile(dir));
				}else{
					img.setImageResource(R.drawable.no_image_element);
				}
				
				String name = cEmployee.getString(cEmployee.getColumnIndex(DbEmployee.CN_NAME));
				text = (TextView)employeeLayout.findViewById(android.R.id.text1);
				text.setText(name);				
				 
				fillLayoutEmployee(db, cEmployee, employeeLayout);
				parent.addView(employeeLayout);	
			}while(cEmployee.moveToNext());
			
		}
		cEmployee.close();
		
		if(serviceInstance.getComentariosElementos() != null){
			nonData = false;
			addComment(R.string.sum_employee_comment, serviceInstance.getComentariosElementos(), parent);
		}

		if(nonData){
			addNoData(parent);	
		}
	}
	
	private void addNoData(LinearLayout parent){
		TextView titleComment = new TextView(this);
		titleComment.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		titleComment.setText(R.string.sum_no_important_data);
		parent.addView(titleComment);
	}

	private void finishService(){
		
		boolean canFinish = (Boolean)DbTrans.read(this, new DbTrans.Db() {

			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				Cursor cur = db.rawQuery(
						DbQuery.EMPLOYEES_SERVICE_NOT_END, 
						new String[]{String.valueOf(serviceInstanceId)});

				String msg = null;
				if(cur.getCount() > 0){
					msg = getString(R.string.msg_there_are_employees_not_finalized);
				}
				cur.close();

				cur = db.rawQuery(DbQuery.SERVICE_INVENTORY_NULL_RESULT,
						new String[]{ 
							String.valueOf(serviceInstanceId)
						}
				);

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
						public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
							ContentValues values = new ContentValues();
							values.put(DbServiceInstance.CN_STATUS, DbServiceInstance.ServiceStatus.FINALIZED);
							values.put(DbServiceInstance.CN_FINISH_DATETIME, DateHelper.getCurrentTime());
							db.update(DbServiceInstance.TABLE_NAME, values, DbServiceInstance._ID + "=?", new String[]{String.valueOf(serviceInstanceId)});							
							return null;
						}
					});
					
					WorkflowHelper.pullPushData(getApplicationContext());
					
					startActivity(WorkflowHelper.process(SummaryActivity.this,android.R.id.button2, mAction));			
					
					dialog.dismiss();

				}
			});
			dialogBuilder.create().show();		
		}
		
	}

}

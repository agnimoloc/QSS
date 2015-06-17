package com.churpi.qualityss.client;

import java.io.File;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeInstance;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance;
import com.churpi.qualityss.client.helper.InventoryListAdapter;
import com.churpi.qualityss.client.helper.Ses;
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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class StaffInventoryActivity extends Activity {
	
	private String mAction;
	private int selectedId;
	
	private static final int REQUEST_PHOTO = 0;
	private static final int REQUEST_IMAGE_SHOW = 1;
	private static final int REQUEST_SINGLE_COMMENT = 2;
	private static final int REQUEST_SINGLE_COMMENTS = 3;
	
	
	private Cursor c;
	private InventoryListAdapter adapter = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_inventory);
		
		TextView text = (TextView)findViewById(android.R.id.text1);
		mAction =getIntent().getAction(); 
		if(mAction.compareTo(Constants.ACTION_EMPLOYEE)== 0){
			setTitle(R.string.title_activity_staff_inventory);
			text.setText(String.format(getString(R.string.inst_equipment_exist_element, Ses.getInstance(this).getEmployeeName())));
		}else{
			setTitle(R.string.title_activity_service_inventory);
			text.setText(String.format(getString(R.string.inst_equipment_exist_service, Ses.getInstance(this).getServiceDescription())));
		}
			
		initCursor();
		
		String[] from = new String[]{DbEquipment.CN_DESCRIPTION};
		int[] to = new int[]{ android.R.id.text1, R.id.checkBox1, R.id.checkBox2};
		
		adapter = new InventoryListAdapter(
				this, R.layout.item_inventory, c,
				from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		ListView list = (ListView)findViewById(R.id.listView1);
		list.setAdapter(adapter);
		
	}
	
	private void initCursor(){
		c = (Cursor)DbTrans.read(this, new DbTrans.Db(){
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				if(mAction.compareTo(Constants.ACTION_EMPLOYEE)== 0){
					return db.rawQuery(DbQuery.STAFF_INVENTORY, 
							new String[]{String.valueOf(Ses.getInstance(context).getEmployeeInstanceId())});
				}else{
					return db.rawQuery(DbQuery.SERVICE_INVENTORY, 
							new String[]{
								String.valueOf(Ses.getInstance(context).getServiceInstanceId())
							}
					);
				}
			}
		});
		if(!c.moveToFirst()){
			startActivity(
					WorkflowHelper.process(
							this,
							android.R.id.button1, 
							mAction
					)
			);	
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		if(c != null){
			c.close();
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comment_menu, menu);
		if(mAction.compareTo(Constants.ACTION_SERVICE) == 0){
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
			WorkflowHelper.getRequisition(this, mAction);
			return true;
		}else if(id == R.id.action_warning){
			WorkflowHelper.getWarning(this, mAction);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onClick_radio(View v){
		RadioButton radio = (RadioButton)v;
		int value = Integer.parseInt(radio.getTag().toString());
		selectedId = Integer.parseInt(((RadioGroup)radio.getParent()).getTag().toString());
		InventoryUpdateItem item = new InventoryUpdateItem();
		if(radio.isChecked()){
			item.setValue(value);
		}else{
			item.setValue(value==1?0:1);
		}
		updateInventory(item);
	}
	
	private File getDestImage(int id){
		String fileName = null;
		
		if(mAction.compareTo(Constants.ACTION_EMPLOYEE)== 0){
			fileName = String.format(Constants.PHOTO_INVENTORY_EMPLOYEE ,
					Ses.getInstance(this).getServiceInstanceKey(),
					Ses.getInstance(this).getEmployeeId(),
					id);
		}else if(mAction.compareTo(Constants.ACTION_SERVICE)== 0){
			fileName = String.format(Constants.PHOTO_INVENTORY_SERVICE ,
					Ses.getInstance(this).getServiceInstanceKey(),
					id);			
		}
		
		return new File (getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
	}
	
	public void onClick_TakePhoto(View v){
		int id = (Integer)v.getTag();
		File dest = getDestImage(id);
		if(dest.exists()){
			WorkflowHelper.showPhoto(this, dest, REQUEST_IMAGE_SHOW);
		}else{
			WorkflowHelper.takePhoto(this, dest, REQUEST_PHOTO);
		}
	}
	
	public void onClick_Comment(View v){
		selectedId = (Integer)v.getTag();
		String comment = (String) DbTrans.read(this, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				if(mAction.compareTo(Constants.ACTION_EMPLOYEE)==0){
					Cursor cur = db.query(
							DbEmployeeEquipmentInventory.TABLE_NAME, 
							new String[]{DbEmployeeEquipmentInventory.CN_COMMENT}, 
							DbEmployeeEquipmentInventory.CN_EMPLOYEE_INSTANCE + "=? AND " +
							DbEmployeeEquipmentInventory.CN_EQUIPMENT + "=?", 
							new String[]{
									String.valueOf(Ses.getInstance(context).getEmployeeInstanceId()), 
									String.valueOf(selectedId)
							}, null, null, null);
					if(cur.moveToFirst()){
						return cur.getString(cur.getColumnIndex(DbEmployeeEquipmentInventory.CN_COMMENT));
					}
					cur.close();	
				}else{
					Cursor cur = db.query(
							DbServiceEquipmentInventory.TABLE_NAME, 
							new String[]{DbServiceEquipmentInventory.CN_COMMENT}, 
							DbServiceEquipmentInventory.CN_SERVICE_INSTANCE + "=? AND " +
									DbServiceEquipmentInventory.CN_EQUIPMENT + "=?", 
							new String[]{
									String.valueOf(Ses.getInstance(context).getServiceInstanceId()), 
									String.valueOf(selectedId)
							}, null, null, null);
					if(cur.moveToFirst()){
						return cur.getString(cur.getColumnIndex(DbEmployeeEquipmentInventory.CN_COMMENT));
					}
				}
				return null;
			}
		});
		WorkflowHelper.getComments(this, 
				comment, getString(R.string.inst_comment_inventory),
				REQUEST_SINGLE_COMMENT);
	}
	public void addComments(){
		String comment = (String) DbTrans.read(this, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				if(mAction.compareTo(Constants.ACTION_EMPLOYEE)==0){
					Cursor cur = db.query(
							DbEmployeeInstance.TABLE_NAME, 
							new String[]{DbEmployeeInstance.CN_INVENTORY_COMMENT}, 
							DbEmployeeInstance._ID + "=?", 
							new String[]{
									String.valueOf(Ses.getInstance(context).getEmployeeInstanceId())
							}, null, null, null);
					if(cur.moveToFirst()){
						return cur.getString(cur.getColumnIndex(DbEmployeeInstance.CN_INVENTORY_COMMENT));
					}
					cur.close();	
				}else{
					Cursor cur = db.query(
							DbServiceInstance.TABLE_NAME, 
							new String[]{DbServiceInstance._ID ,DbServiceInstance.CN_INVENTORY_COMMENT}, 
							DbServiceInstance._ID + "=?", 
							new String[]{
									String.valueOf(Ses.getInstance(context).getServiceInstanceId())
							}, null, null, null);
					if(cur.moveToFirst()){
						return cur.getString(cur.getColumnIndex(DbServiceInstance.CN_INVENTORY_COMMENT));
					}
					cur.close();
				}
				return null;
			}
		});
		WorkflowHelper.getComments(this, 
				comment, getString(R.string.inst_comment_inventory_result), 
				REQUEST_SINGLE_COMMENTS);
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
	    	InventoryUpdateItem item = new InventoryUpdateItem();
	    	item.setComment(extras.getString(CheckpointCommentActivity.FLD_COMMENT));
	    	updateInventory( item);
	    	Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show();
	    } else if(requestCode == REQUEST_SINGLE_COMMENTS && resultCode == RESULT_OK){
	    	Bundle extras = data.getExtras();
	    	updateInventoryComment(extras.getString(CheckpointCommentActivity.FLD_COMMENT));
	    	Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show();
	    }		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private class InventoryUpdateItem{
		boolean iscomment = false;
		boolean isvalue = false;
		String comment;
		int value;
		public boolean isComment() {
			return iscomment;
		}
		public boolean isValue() {
			return isvalue;
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String commentText) {
			iscomment = true;
			this.comment = commentText;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			isvalue = true;
			this.value = value;
		}
		
		
	}
	
	private void updateInventoryComment(String comment){
		DbTrans.write(this, comment, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				String comment = (String)parameter;
				ContentValues values = new ContentValues();
				if(mAction.compareTo(Constants.ACTION_EMPLOYEE)==0){
					values.put(DbEmployeeInstance.CN_INVENTORY_COMMENT, comment);
					db.update(DbEmployeeInstance.TABLE_NAME, 
							values, 
							DbEmployeeInstance._ID + "=?", 
							new String[]{String.valueOf(Ses.getInstance(context).getEmployeeInstanceId())});
				}else{
					values.put(DbServiceInstance.CN_INVENTORY_COMMENT, comment);
					db.update(DbServiceInstance.TABLE_NAME, 
							values, 
							DbServiceInstance._ID + "=?", 
							new String[]{String.valueOf(Ses.getInstance(context).getServiceInstanceId())});					
				}
				return null;
			}
		});
	}
	
	private void updateInventory(InventoryUpdateItem item){
		DbTrans.write(this, item, new DbTrans.Db() {			
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				InventoryUpdateItem item = (InventoryUpdateItem)parameter;
				
				ContentValues values = new ContentValues();
				if(mAction.compareTo(Constants.ACTION_EMPLOYEE)==0){
					int employeeInstanceId = Ses.getInstance(context).getEmployeeInstanceId();
					DbEmployeeInstance.setStatus(db, employeeInstanceId, DbEmployeeInstance.EmployeeStatus.CURRENT);
					if(item.isValue())
						values.put(DbEmployeeEquipmentInventory.CN_CHECKED, item.getValue());
					else if (item.isComment())
						values.put(DbEmployeeEquipmentInventory.CN_COMMENT, item.getComment());
					
					int count = db.update(DbEmployeeEquipmentInventory.TABLE_NAME, 
							values, 
							DbEmployeeEquipmentInventory.CN_EMPLOYEE_INSTANCE + "=? AND " +
							DbEmployeeEquipmentInventory.CN_EQUIPMENT + "=?", 
							new String[]{String.valueOf(employeeInstanceId), String.valueOf(selectedId)});
					if(count == 0){
						values.put(DbEmployeeEquipmentInventory.CN_EMPLOYEE_INSTANCE, employeeInstanceId);
						values.put(DbEmployeeEquipmentInventory.CN_EQUIPMENT, selectedId);
						db.insert(DbEmployeeEquipmentInventory.TABLE_NAME, null, values);
					}
				}else{
					if(item.isValue())
						values.put(DbServiceEquipmentInventory.CN_CHECKED, item.getValue());
					else if(item.isComment())
						values.put(DbServiceEquipmentInventory.CN_COMMENT, item.getComment());
					
					int serviceInstanceId = Ses.getInstance(context).getServiceInstanceId();
					int count = db.update(DbServiceEquipmentInventory.TABLE_NAME, 
							values, 
							DbServiceEquipmentInventory.CN_SERVICE_INSTANCE + "=? AND " +
							DbServiceEquipmentInventory.CN_EQUIPMENT + "=?", 
							new String[]{String.valueOf(serviceInstanceId), String.valueOf(selectedId)});
					if(count == 0){
						values.put(DbServiceEquipmentInventory.CN_SERVICE_INSTANCE, serviceInstanceId);
						values.put(DbServiceEquipmentInventory.CN_EQUIPMENT, selectedId);
						db.insert(DbServiceEquipmentInventory.TABLE_NAME, null, values);
					}
				}
				return null;
			}
		});
		
		initCursor();
		
		Cursor oldCursor = adapter.swapCursor(c);
		oldCursor.close();
		
	}
	
	public void onClick_next(View v){
		startActivity(
				WorkflowHelper.process(
						this,
						android.R.id.button1, 
						mAction
				)
		);		
	}
}

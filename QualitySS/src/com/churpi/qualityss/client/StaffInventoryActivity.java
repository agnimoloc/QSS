package com.churpi.qualityss.client;

import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipmentInventory;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSector;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipmentInventory;
import com.churpi.qualityss.client.dto.EmployeeDTO;
import com.churpi.qualityss.client.helper.InventoryListAdapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class StaffInventoryActivity extends Activity {

	public static final String SERVICE_ID = "serviceId";
	public static final String ID = "currentId";
	public static final String NAME = "currentName";
	public static final String ACTION_EMPLOYEE = "employee";
	public static final String ACTION_SERVICE = "service";
	
	private int mId;
	private int mServiceId;
	private String mName;
	private String mAction;
	
	
	private Cursor c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_inventory);
		
		Bundle extras = getIntent().getExtras();
		mId = extras.getInt(ID);
		mServiceId = extras.getInt(SERVICE_ID);
		mName = extras.getString(NAME);
		TextView text = (TextView)findViewById(android.R.id.text1);
		mAction =getIntent().getAction(); 
		if(mAction.compareTo(ACTION_EMPLOYEE)== 0){
			text.setText(String.format(getString(R.string.inst_equipment_exist_element, mName)));
		}else{
			text.setText(String.format(getString(R.string.inst_equipment_exist_service, mName)));
		}
			
		c = (Cursor)DbTrans.read(this, new DbTrans.Db(){

			@Override
			public Object onDo(Context context, SQLiteDatabase db) {
				if(mAction.compareTo(ACTION_EMPLOYEE)== 0){
					return db.rawQuery(DbQuery.STAFF_INVENTORY, 
							new String[]{String.valueOf(mId)});
				}else{
					return db.rawQuery(DbQuery.SERVICE_INVENTORY, 
							new String[]{String.valueOf(mId)});
				}
			}
			
		});
		String[] from = new String[]{DbEquipment.CN_DESCRIPTION};
		int[] to = new int[]{ android.R.id.text1, R.id.checkBox1, R.id.checkBox2};
		
		ListView list = (ListView)findViewById(R.id.listView1);
		list.setAdapter(new InventoryListAdapter(
				this, R.layout.item_inventory, c,
				from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
		
	}
	
	@Override
	protected void onDestroy() {
		if(c != null){
			c.close();
		}
		super.onDestroy();
	}
	
	public void onClick_radio(View v){
		RadioButton radio = (RadioButton)v;
		int value = Integer.parseInt(radio.getTag().toString());
		int equipmentId = Integer.parseInt(((RadioGroup)radio.getParent()).getTag().toString());
		if(radio.isChecked()){
			updateInventory(equipmentId, value);			
		}else{
			updateInventory(equipmentId, value==1?0:1);
		}
		
	}
	
	private void updateInventory(final int equipmentId, final int checked){
		DbTrans.write(this, new DbTrans.Db() {			
			@Override
			public Object onDo(Context context, SQLiteDatabase db) {
				ContentValues values = new ContentValues();
				if(mAction.compareTo(ACTION_EMPLOYEE)==0){
					DbEmployee.setStatus(db, mId, DbEmployee.EmployeeStatus.CURRENT);
					
					values.put(DbEmployeeEquipmentInventory.CN_CHECKED, checked);
					int count = db.update(DbEmployeeEquipmentInventory.TABLE_NAME, 
							values, 
							DbEmployeeEquipmentInventory.CN_EMPLOYEE + "=? AND " +
							DbEmployeeEquipmentInventory.CN_EQUIPMENT + "=?", 
							new String[]{String.valueOf(mId), String.valueOf(equipmentId)});
					if(count == 0){
						values.put(DbEmployeeEquipmentInventory.CN_EMPLOYEE, mId);
						values.put(DbEmployeeEquipmentInventory.CN_EQUIPMENT, equipmentId);
						db.insert(DbEmployeeEquipmentInventory.TABLE_NAME, null, values);
					}
				}else{
					values.put(DbServiceEquipmentInventory.CN_CHECKED, checked);
					int count = db.update(DbServiceEquipmentInventory.TABLE_NAME, 
							values, 
							DbServiceEquipmentInventory.CN_SERVICE + "=? AND " +
							DbServiceEquipmentInventory.CN_EQUIPMENT + "=?", 
							new String[]{String.valueOf(mId), String.valueOf(equipmentId)});
					if(count == 0){
						values.put(DbServiceEquipmentInventory.CN_SERVICE, mId);
						values.put(DbServiceEquipmentInventory.CN_EQUIPMENT, equipmentId);
						db.insert(DbServiceEquipmentInventory.TABLE_NAME, null, values);
					}
				}
				return null;
			}
		});
	}
	
	public void onClick_next(View v){
		if(mAction.compareTo(ACTION_EMPLOYEE)==0){
			Intent intent = new Intent(this, StaffReviewActivity.class);
			intent.putExtra(StaffReviewActivity.SERVICE_ID, mServiceId);
			intent.putExtra(StaffReviewActivity.EMPLOYEE_ID, mId);
			intent.putExtra(StaffReviewActivity.EMPLOYEE_NAME, mName);
			startActivity(intent);		
		}else{
			Intent intent = new Intent(this, StaffReviewListActivity.class);
			intent.putExtra(StaffReviewListActivity.FLD_SERVICE_ID, mId);
			startActivity(intent);
		}
	}
}

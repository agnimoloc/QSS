package com.churpi.qualityss.client;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbCustomer;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.dto.AddressDTO;
import com.churpi.qualityss.client.dto.CustomerDTO;
import com.churpi.qualityss.client.dto.ServiceDTO;
import com.churpi.qualityss.client.helper.DateHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceDetailActivity extends Activity {

	public static final String SERVICE_ID = "serviceId";

	int mServiceId;

	ServiceDTO mService = new ServiceDTO();
	CustomerDTO mCustomer = new CustomerDTO();
	AddressDTO mAddress = new AddressDTO();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_detail);

		mServiceId = getIntent().getIntExtra(SERVICE_ID, -1);



		DbTrans.read(this, new DbTrans.Db(){

			@Override
			public Object onDo(Context context, SQLiteDatabase db) {
				Cursor c = null;
				try{
					c = db.query(DbService.TABLE_NAME, null, 
							DbService._ID + "=?", new String[]{String.valueOf(mServiceId)}, 
							null, null, null);
					c.moveToFirst();
					mService.fillFromCursor(c);
					c.close();
					c = db.query(DbCustomer.TABLE_NAME, null, 
							DbCustomer._ID + "=?", new String[]{String.valueOf(mService.getClienteId())}, 
							null, null, null);
					if(c.getCount() != 0){
						c.moveToFirst();
						mCustomer.fillFromCursor(c);
					}
					c.close();
					c = db.rawQuery(DbQuery.GET_ADDRESS, 
							new String[]{String.valueOf(mService.getDomicilio().getDomicilioId())});
					if(c.getCount() != 0){
						c.moveToFirst();
						mAddress.fillFromCursor(c);
					}
				}finally{
					if(c != null){
						c.close();
					}
				}		
				return null;
			}

		});


		TextView text = (TextView)findViewById(R.id.fldCode);
		text.setText(mService.getCode());

		text = (TextView)findViewById(R.id.fldAddress);
		text.setText(mAddress.getDomicilioString());

		text = (TextView)findViewById(R.id.fldDescription);
		text.setText(mService.getDescripcion());

		text = (TextView)findViewById(R.id.fldCustomer);
		text.setText(mCustomer.getDescripcion());


	}

	public void onClick_cancel(View v){
		finish();
	}

	public void onClick_maps(View v){
		Uri uri = Uri.parse("geo:0,0")
				.buildUpon()
				.appendQueryParameter("q", mAddress.getMapsAddress())
				.build();		
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);

	}

	public void onClick_next(View v){
		if(mService.canOpen()){
			if(mService.canStart()){
				DbTrans.write(this, new DbTrans.Db() {
					@Override
					public Object onDo(Context context, SQLiteDatabase db) {
						ContentValues values = new ContentValues();
						SharedPreferences pref = Constants.getPref(context);
						int userEmployeeId = pref.getInt(Constants.PREF_EMPLOYEE, -1);
						values.put(DbService.CN_EMPLOYEEREVIEW, userEmployeeId);
						values.put(DbService.CN_DATETIME, DateHelper.getCurrentTime());
						values.put(DbService.CN_STATUS, ServiceDTO.ServiceStatus.CURRENT);
						db.update(DbService.TABLE_NAME, 
								values, DbService._ID +"=?",
								new String[]{String.valueOf(mService.getServicioId())});
						return null;
					}
				});
			}
			Intent intent = new Intent(this, StaffInventoryActivity.class);
			intent.putExtra(StaffInventoryActivity.ID, mService.getServicioId());
			intent.putExtra(StaffInventoryActivity.NAME, mService.getDescripcion());
			intent.setAction(StaffInventoryActivity.ACTION_SERVICE);
			startActivity(intent);
		}
	}

	public void onClick_documents(View v){
		Toast.makeText(this, "No hay documentos para mostrar", Toast.LENGTH_SHORT).show();
	}	
}

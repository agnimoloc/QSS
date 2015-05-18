package com.churpi.qualityss.client;

import java.util.Calendar;
import java.util.Date;

import com.churpi.qualityss.Config;
import com.churpi.qualityss.client.db.DbActions;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbCustomer;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceConfiguration;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance;
import com.churpi.qualityss.client.dto.AddressDTO;
import com.churpi.qualityss.client.dto.CustomerDTO;
import com.churpi.qualityss.client.dto.ServiceDTO;
import com.churpi.qualityss.client.dto.ServiceInstanceDTO;
import com.churpi.qualityss.client.helper.DateHelper;
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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceDetailActivity extends Activity {
	int mServiceId;

	ServiceDTO mService = new ServiceDTO();
	ServiceInstanceDTO mServiceInstance = new ServiceInstanceDTO();
	CustomerDTO mCustomer = new CustomerDTO();
	AddressDTO mAddress = new AddressDTO();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_detail);

		mServiceId = Ses.getInstance(this).getServiceId();

		DbTrans.read(this, new DbTrans.Db(){

			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
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
		ServiceInstanceDTO serviceInstance = (ServiceInstanceDTO)DbTrans.read(this, new DbTrans.Db() {
			
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				Cursor c = null;
				ServiceInstanceDTO si = new ServiceInstanceDTO();
				try{
					c = db.query(DbServiceInstance.TABLE_NAME, null, 
							DbServiceInstance.CN_SERVICE + "=? AND " +
							DbServiceInstance.CN_ACTIVITY_TYPE + "=?"
							, new String[]{
								String.valueOf(mServiceId),
								String.valueOf(Ses.getInstance(context).getActivityType())
							}, 
							null, null, null);
					if(c.moveToFirst())
						si.fillFromCursor(c);
					c.close();
				}catch(Exception e){
					Log.e("ServiceDetailActivity", e.getMessage());
				}finally{
				
					if(c != null){
						c.close();
					}
				}
				return si;
			}
		});
		if(DbActions.checkClearService(this, serviceInstance)){
			if(serviceInstance.getStatus()==null){
				DbTrans.write(this, serviceInstance, new DbTrans.Db() {
					@Override
					public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
						
						Cursor cur = db.query(DbServiceConfiguration.TABLE_NAME, 
								new String[]{DbServiceConfiguration._ID}, 
								DbServiceConfiguration.CN_SERVICE + "=? AND "+ 
								DbServiceConfiguration.CN_ACTIVITY_TYPE +"=?", 
								new String[]{
									String.valueOf(mService.getServicioId()),
									String.valueOf(Ses.getInstance(context).getActivityType())
								}, 
								null, null, null);
						int serviceConfigurationID = 0;
						if(cur.moveToFirst())
							serviceConfigurationID = cur.getInt(cur.getColumnIndex(DbServiceConfiguration._ID));
						cur.close();
						ServiceInstanceDTO si = (ServiceInstanceDTO) parameter;
					
						int userEmployeeId = Ses.getInstance(context).getEmployee();
						String currentDate = DateHelper.getCurrentTime();
						
						si.setServicioConfiguracionId(serviceConfigurationID);
						si.setEmpleadoRevision(userEmployeeId);
						si.setFechaInicio(currentDate);
						si.setServicioId(mService.getServicioId());
						si.setStatus(DbServiceInstance.ServiceStatus.CURRENT);
						si.setTipo(Ses.getInstance(context).getActivityType());
						
						ContentValues values = si.getContentValues(); 
 
						si.setServicioInstanciaId((int)
								db.insert(DbServiceInstance.TABLE_NAME, null, values));
						
						return null;
					}
				});
			}
						
			Ses.getInstance(this).edit()
				.setServiceDescription(mService.getDescripcion())
				.setServiceInstanceId(serviceInstance.getServicioInstanciaId())
				.setServiceInstanceKey(serviceInstance.getKey())
				.commit();

			startActivity(
					WorkflowHelper.process(this,
							R.id.button4,
							StaffInventoryActivity.ACTION_SERVICE));
		}else{
			Date sentDate =  DateHelper.getDateFromDb(serviceInstance.getFechaFin());
			Calendar cal = Calendar.getInstance();
			cal.setTime(sentDate);
			cal.add(Calendar.HOUR_OF_DAY, Config.HOURS_TO_RESET_SENT_SERVICE);
			long diff = cal.getTime().getTime() - Calendar.getInstance().getTime().getTime();
			long hour = diff / (60 * 60 * 1000); 
			long min = diff / (60 * 1000);
			
			Toast.makeText(this, 
					getString(R.string.msg_warning_service_finalized, hour, min), Toast.LENGTH_LONG).show();
		}
	}

	public void onClick_documents(View v){
		Toast.makeText(this, "No hay documentos para mostrar", Toast.LENGTH_SHORT).show();
	}	
}

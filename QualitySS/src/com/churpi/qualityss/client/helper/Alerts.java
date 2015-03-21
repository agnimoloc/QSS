package com.churpi.qualityss.client.helper;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.ScrollView;
import android.widget.TextView;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.StaffInventoryActivity;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbCustomer;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.dto.AddressDTO;
import com.churpi.qualityss.client.dto.CustomerDTO;
import com.churpi.qualityss.client.dto.ServiceDTO;

public class Alerts {
	public static void showGenericError(Context context){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.ttl_error);
		dialogBuilder.setMessage(context.getString(R.string.msg_generic_error));
		dialogBuilder.create().show();		
	}
	
	public static void showError(Context context, String message){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.ttl_error);
		dialogBuilder.setMessage(message);
		dialogBuilder.create().show();
	}
	
	/*public static void showServiceDetail(final Context context, final int serviceId){
		
		final ServiceDTO mService = new ServiceDTO();
		final CustomerDTO mCustomer = new CustomerDTO();
		final AddressDTO mAddress = new AddressDTO();
		
		DbTrans.read(context, new DbTrans.Db(){

			@Override
			public Object onDo(Context context, SQLiteDatabase db) {
				Cursor c = null;
				try{
						c = db.query(DbService.TABLE_NAME, null, 
								DbService._ID + "=?", new String[]{String.valueOf(serviceId)}, 
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
		
		
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
		ScrollView layout = (ScrollView)inflater.inflate(R.layout.dialog_service_detail, null);

		
		TextView text = (TextView)layout.findViewById(R.id.fldCode);
		text.setText(mService.getCode());
		
		text = (TextView)layout.findViewById(R.id.fldAddress);
		text.setText(mAddress.getDomicilioString());

		text = (TextView)layout.findViewById(R.id.fldDescription);
		text.setText(mService.getDescripcion());
		
		text = (TextView)layout.findViewById(R.id.fldCustomer);
		text.setText(mCustomer.getDescripcion());
		
		//AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogApptheme);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(layout);
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();				
			}			
		});
		
		builder.setPositiveButton(R.string.btn_start_service, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();	
				if(mService.getFechaRevision() == null){
					DbTrans.write(context, new DbTrans.Db() {
						@Override
						public Object onDo(Context context, SQLiteDatabase db) {
							ContentValues values = new ContentValues();
							SharedPreferences pref = Constants.getPref(context);
							int userEmployeeId = pref.getInt(Constants.PREF_EMPLOYEE, -1);
							values.put(DbService.CN_EMPLOYEEREVIEW, userEmployeeId);
							values.put(DbService.CN_DATETIME, DateHelper.getCurrentTime());
							
							db.update(DbService.TABLE_NAME, 
									values, 
									DbService._ID +"=?", 
									new String[]{String.valueOf(mService.getServicioId())});
							return null;
						}
					});
				}
				
				Intent intent = new Intent(context, StaffInventoryActivity.class);
				intent.putExtra(StaffInventoryActivity.ID, mService.getServicioId());
				intent.putExtra(StaffInventoryActivity.NAME, mService.getDescripcion());
				intent.setAction(StaffInventoryActivity.ACTION_SERVICE);
				context.startActivity(intent);
			}			
		});
		
		builder.setNeutralButton(R.string.btn_maps, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Uri uri = Uri.parse("geo:0,0")
									.buildUpon()
									.appendQueryParameter("q", mAddress.getMapsAddress())
									.build();		
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				context.startActivity(intent);
						
				
				dialog.cancel();				
			}
		});
		
		builder.setTitle(R.string.title_activity_service_detail);
		
		AlertDialog dialog = builder.create();
		
		dialog.show();
	}*/
}

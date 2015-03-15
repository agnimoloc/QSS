package com.churpi.qualityss.client.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.widget.ScrollView;
import android.widget.TextView;

import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.StaffReviewListActivity;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbCustomer;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
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
	
	public static void showServiceDetail(final Context context, final int serviceId){
		
		final ServiceDTO mService = new ServiceDTO();
		final CustomerDTO mCustomer = new CustomerDTO();
		
		DbTrans.read(context, new DbTrans.Db(){

			@Override
			public void onDo(Context context, SQLiteDatabase db) {
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
							c.close();				
						}
				}finally{
					if(c != null){
						c.close();
					}
				}				
			}
			
		});
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
		ScrollView layout = (ScrollView)inflater.inflate(R.layout.dialog_service_detail, null);

		
		TextView text = (TextView)layout.findViewById(R.id.fldCode);
		text.setText(mService.getCode());
		
		text = (TextView)layout.findViewById(R.id.fldAddress);
		text.setText(mService.getDomicilio());

		text = (TextView)layout.findViewById(R.id.fldDescription);
		text.setText(mService.getDescripcion());
		
		text = (TextView)layout.findViewById(R.id.fldCustomer);
		text.setText(mCustomer.getDescripcion());
		
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
				Intent intent = new Intent(context, StaffReviewListActivity.class);
				intent.putExtra(StaffReviewListActivity.FLD_SERVICE_ID, mService.getServicioId());
				context.startActivity(intent);
			}			
		});
		
		builder.setNeutralButton(R.string.btn_maps, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();				
			}
		});
		
		builder.setTitle(R.string.title_activity_service_detail);
		
		builder.create().show();
	}
}

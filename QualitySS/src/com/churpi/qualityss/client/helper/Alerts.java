package com.churpi.qualityss.client.helper;

import android.app.AlertDialog;
import android.content.Context;

import com.churpi.qualityss.client.R;

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
}
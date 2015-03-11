package com.churpi.qualityss.client;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);	
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Toast.makeText(this, "Saliendo", Toast.LENGTH_LONG).show();
		Intent startMain = new Intent(this, MainActivity.class);
	    startMain.setAction(Constants.END_APPLICATION);
	    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(startMain);
	}
	
	public void onClick_Login(View v){
		TextView accountField = (TextView)findViewById(R.id.txtUser);
		TextView passwordField = (TextView)findViewById(R.id.txtPassword);
		final String account = accountField.getText().toString().trim();
		final String password = passwordField.getText().toString();
		DbTrans.read(v.getContext(), new DbTrans.Db() {
			@Override
			public void onDo(Context context, SQLiteDatabase db) {
				Cursor cursor = db.query(
						DbUser.TABLE_NAME, 
						new String[]{DbUser._ID,DbUser.CN_NAME, DbUser.CN_PASSWORD}, 
						DbUser.CN_ACCOUNT +"=? AND "+ DbUser.CN_PASSWORD +"=?", 
						new String[]{account, password}, 
						null, null, null, "1");
				if(cursor.moveToFirst()){
					Intent loginActivity = new Intent(context, ServiceListActivity.class);
			    	startActivity(loginActivity);
				}else{
					AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
					dialogBuilder.setTitle(R.string.ttl_invalid_login);
					dialogBuilder.setMessage(R.string.msg_invalid_login);
					dialogBuilder.setPositiveButton(R.string.ok, null);
					dialogBuilder.create().show();
				}
					
			}
		});
	}
}

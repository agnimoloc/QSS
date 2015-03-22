package com.churpi.qualityss.client;


import com.churpi.qualityss.Constants;
import com.churpi.qualityss.service.PullPushDataService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	private static final String STATE_STATUS = "status";
	private static final String STATE_DESCRIPTION = "description";
	private static final String STATE_PROGRESS = "progress";
	
	BroadcastReceiver dataReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(final Context context, Intent intent) {
			String status = intent.getStringExtra(Constants.PULL_PUSH_DATA_STATUS);
			if(status.compareTo(Constants.PULL_PUSH_DATA_REFRESH)==0){
				enqueue = intent.getLongExtra(Constants.PULL_PUSH_DATA_DATA, 0);
				
				int progress = intent.getIntExtra(Constants.PULL_PUSH_DATA_PROGRESS, 0);
				String description = intent.getStringExtra(Constants.PULL_PUSH_DATA_DESCRIPTION);
				
				refreshUserInfo(description, progress);
				
				if(progress == 100){
					SharedPreferences pref = Constants.getPref(context);
					SharedPreferences.Editor editor = pref.edit();
					editor.putBoolean(Constants.PREF_FILLED_DB, true);
					editor.commit();
					finishOK();
				}
			}else if(status.compareTo(Constants.PULL_PUSH_DATA_FAIL)==0){
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
				dialogBuilder.setTitle(R.string.ttl_error);
				dialogBuilder.setMessage(R.string.qst_not_connection_retry);
				dialogBuilder.setPositiveButton(R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						refreshUserInfo(getString(R.string.msg_connect_to_server), 0);
						Intent getData = new Intent(context,PullPushDataService.class);
						startService(getData);
					}
				});
				dialogBuilder.setNegativeButton(R.string.cancel, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finishCancel();			
					}
				});
				AlertDialog alert = dialogBuilder.create();
				alert.setCancelable(false);
				alert.show();
			}
		}
	};
	
	long enqueue;
	
			
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(dataReceiver, new IntentFilter(Constants.PULL_PUSH_DATA_ACTION));
	    setContentView(R.layout.activity_main);
		Intent getdata = new Intent(this,PullPushDataService.class);
		startService(getdata);

	    if(!isDBFilled()){
	    	if(savedInstanceState != null){
	    		TextView label1 = (TextView)findViewById(R.id.textView1);
	    		label1.setText(savedInstanceState.getString(STATE_STATUS));
	    		TextView label2 = (TextView)findViewById(R.id.lblType);
	    		label2.setText(savedInstanceState.getString(STATE_DESCRIPTION));
	    		ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar2);
	    		bar.setProgress(savedInstanceState.getInt(STATE_PROGRESS));
	    	}
	    }else{
	    	finishOK();
	    }
    }
    
    private void finishOK(){
    	setResult(RESULT_OK);
    	finish();
    }
    private void finishCancel(){
    	finish();
    }
    
    @Override
    protected void onDestroy() {
    	unregisterReceiver(dataReceiver);    	
    	super.onDestroy();
    }
        
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	
    	TextView label1 = (TextView)findViewById(R.id.textView1);
    	
    	outState.putString(STATE_STATUS, label1.getText().toString());
    	TextView label2 = (TextView)findViewById(R.id.lblType);
    	outState.putString(STATE_DESCRIPTION, label2.getText().toString());
    	ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar2);
    	outState.putInt(STATE_PROGRESS, bar.getProgress());
    	
    	super.onSaveInstanceState(outState);
    }

    private boolean isDBFilled(){    	
    	SharedPreferences pref = Constants.getPref(getBaseContext());
    	return pref.getBoolean(Constants.PREF_FILLED_DB, false);    	
    }
    
    private void refreshUserInfo(String description, int progress){
		TextView label2 = (TextView)findViewById(R.id.lblType);
		label2.setText(description);
		ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar2);
		bar.setProgress(progress);
    }
    
}

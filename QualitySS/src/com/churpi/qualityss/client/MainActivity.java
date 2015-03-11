package com.churpi.qualityss.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.QualitySSDbHelper;
import com.churpi.qualityss.client.helper.Alerts;
import com.churpi.qualityss.service.PullPushDataService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
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
					SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();
					editor.putBoolean(Constants.PREF_FILLED_DB, true);
					editor.commit();
					openLoginActivity();
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
						quit();						
					}
				});
				AlertDialog alert = dialogBuilder.create();
				alert.setCancelable(false);
				alert.show();
			}
		}
	};
	
	long enqueue;
	
	private void quit(){
		this.finish();
        System.exit(0);
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                Query query = new Query();
                query.setFilterById(enqueue);
                Cursor c = dm.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c
                            .getInt(columnIndex)) {
                        
                        String filenameString = c
                                .getString(c
                                        .getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                        
                        File source = new File(filenameString);
                        
                        File dest = new File (context.getDir(Constants.JSON_DIR, Context.MODE_PRIVATE), Constants.JSON_NAME);
                        if(dest.exists())
                        	dest.delete();
                        FileInputStream fis;
                        FileOutputStream fos;
						try {
							fis = new FileInputStream(source);
	                        fos = new FileOutputStream(dest);
	                        
	                        byte[] buf = new byte[1024];
	                        int len;
	                        while ((len = fis.read(buf)) > 0) {
	                            fos.write(buf, 0, len);
	                        }
	                        fis.close();
	                        fos.close();
	                        source.delete();
						} catch (FileNotFoundException e) {
							Alerts.showGenericError(context);
							e.printStackTrace();
						} catch (IOException e) {
							Alerts.showGenericError(context);
							e.printStackTrace();
						}
						sendBroadCastResult(Constants.PULL_PUSH_DATA_REFRESH, getString(R.string.msg_update_database), 70);
		        		QualitySSDbHelper dbHelper = new QualitySSDbHelper(context);
		        		dbHelper.loadDataFromJSON();

                    }
                }
            }
		}
	};
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        registerReceiver(dataReceiver, new IntentFilter(Constants.PULL_PUSH_DATA_ACTION));
        if(getIntent().getAction() == Constants.END_APPLICATION){
        	quit();
        }else{
	        setContentView(R.layout.activity_main);	   	        
	        if(!isDBFilled()){
		        if(savedInstanceState == null){
	        		Intent getdata = new Intent(this,PullPushDataService.class);
	        		startService(getdata);
	        		/*QualitySSDbHelper dbHelper = new QualitySSDbHelper(this);
	        		dbHelper.loadDataFromJSON();*/
	        	}else{
	        		TextView label1 = (TextView)findViewById(R.id.textView1);
	        		label1.setText(savedInstanceState.getString(STATE_STATUS));
	        		TextView label2 = (TextView)findViewById(R.id.lblType);
	        		label2.setText(savedInstanceState.getString(STATE_DESCRIPTION));
	        		ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar2);
	        		bar.setProgress(savedInstanceState.getInt(STATE_PROGRESS));
	        	}
	        }else{
	        	openLoginActivity();
	        }
        }
    }
    
    @Override
    protected void onDestroy() {
    	unregisterReceiver(receiver);
    	unregisterReceiver(dataReceiver);
    	
    	super.onDestroy();
    }
    
    private void openLoginActivity(){
    	Intent loginActivity = new Intent(this, LoginActivity.class);
    	startActivity(loginActivity);
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
    	SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
    	return pref.getBoolean(Constants.PREF_FILLED_DB, false);    	
    }
    
    private void refreshUserInfo(String description, int progress){
		TextView label2 = (TextView)findViewById(R.id.lblType);
		label2.setText(description);
		ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar2);
		bar.setProgress(progress);
    }
    
    private void sendBroadCastResult(String statusDescription, String description, int progress){
		Intent status = new Intent();
		status.setAction(Constants.PULL_PUSH_DATA_ACTION);
		status.putExtra(Constants.PULL_PUSH_DATA_STATUS, statusDescription);
		status.putExtra(Constants.PULL_PUSH_DATA_DESCRIPTION, description);
		status.putExtra(Constants.PULL_PUSH_DATA_PROGRESS, progress);
		status.putExtra(Constants.PULL_PUSH_DATA_DATA, 0);
		sendBroadcast(status);
		
	}
    
}

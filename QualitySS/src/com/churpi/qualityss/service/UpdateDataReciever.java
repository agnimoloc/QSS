package com.churpi.qualityss.service;

import com.churpi.qualityss.Constants;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

public class UpdateDataReciever extends BroadcastReceiver {
	
	
	AlarmManager alarmManager;
	PendingIntent pi;
	Context mContext;
	
	public UpdateDataReciever(Context context){
		alarmManager = (AlarmManager)(context.getSystemService( Context.ALARM_SERVICE ));
    	pi = PendingIntent.getBroadcast( context, 
    			0,new Intent(Constants.UPDATE_DATA_ACTION),0 );
	}
	
	public void start(){
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
    			SystemClock.elapsedRealtime() + 3000, pi);
	}
	
	public void dispose(){
		alarmManager.cancel(pi);
	}
	
	
	 public void onReceive(Context context, Intent intent) {
		 //TODO: call update data
		 start();
    }
}

package com.churpi.qualityss.service;

import com.churpi.qualityss.Config;
import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class UpdateDataReciever extends BroadcastReceiver {

	private static UpdateDataReciever INSTANCE = null;

	AlarmManager alarmManager;
	PendingIntent pi;
	Context mContext;
		
	public static synchronized void createInstance(Context context){
		if(INSTANCE == null){
			INSTANCE = new UpdateDataReciever(context);
		}
	}
	
	public static UpdateDataReciever getInstance(){
		return INSTANCE;
	}
	
	public UpdateDataReciever(Context context){
		alarmManager = (AlarmManager)(context.getSystemService( Context.ALARM_SERVICE ));
		pi = PendingIntent.getBroadcast( context, 
				0,new Intent(Constants.UPDATE_DATA_ACTION),0 );
		mContext = context;
	}

	public void start(){
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
				SystemClock.elapsedRealtime() + Config.REFRESH_TIME, pi);
	}
		
	public void dispose(){
		alarmManager.cancel(pi);
	}

	public void onReceive(final Context context, Intent intent) {		 

		WorkflowHelper.pullPushData(context);
	}
	
}

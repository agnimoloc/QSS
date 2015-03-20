package com.churpi.qualityss.client.helper;

import com.churpi.qualityss.Constants;

import android.app.Activity;
import android.content.SharedPreferences;

public class SavedActivityManager {

	public static boolean navigate(Activity activity){
		//TODO: implement the navigation stack
		/*SharedPreferences pref = Constants.getPref(activity);
		if(pref.contains(Constants.PREF_ACTIVITY)){
			String activityName = activity.getIntent().getComponent().getClassName();
			String activityComparision = pref.getString(Constants.PREF_ACTIVITY, null); 			
			if(activityName.compareTo(activityComparision)!=0){
				return true;
			}
		}*/
		return false;
	}
	
	public static void saveActivity(Activity activity){
		Constants.getPref(activity)
			.edit()
			.putString(Constants.PREF_ACTIVITY, 
					activity.getIntent().getComponent().getClassName())
			.commit();		
	}
}

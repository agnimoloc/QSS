package com.churpi.qualityss.client.helper;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import com.churpi.qualityss.Config;
import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance;
import com.churpi.qualityss.client.dto.ServiceDTO;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class ServiceListAdapter extends SimpleCursorAdapter{
	LayoutInflater mLayoutInflater;
	Context mContext;
	
	public ServiceListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		mContext = context;
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FrameLayout layout;
		if(convertView == null){
			layout = (FrameLayout) mLayoutInflater.inflate(
					R.layout.item_service, 
					parent, false); 
		}else{
			layout = (FrameLayout)convertView;
		}		
		ImageView img = (ImageView)layout.findViewById(android.R.id.button1);
		
		Cursor c = getCursor();
		c.moveToPosition(position);
		String serviceCode = c.getString(c.getColumnIndex(DbService.CN_CODE));
		layout.setBackground(null);
		
		
		if(!c.isNull(c.getColumnIndex(DbServiceInstance.CN_STATUS))){
			String status = c.getString(c.getColumnIndex(DbServiceInstance.CN_STATUS));
			if(DbServiceInstance.ServiceStatus.CURRENT.compareTo(status)==0){
				layout.setBackgroundColor(mContext.getResources().getColor(R.color.started));
			} else if(DbServiceInstance.ServiceStatus.FINALIZED.compareTo(status)==0){
				layout.setBackgroundColor(mContext.getResources().getColor(R.color.finalized));
			} else if(DbServiceInstance.ServiceStatus.SENT.compareTo(status)==0){
				Calendar cal = Calendar.getInstance();
				cal.setTime(DateHelper.getDateFromDb(c.getString(c.getColumnIndex(DbServiceInstance.CN_FINISH_DATETIME))));
				cal.add(Calendar.HOUR_OF_DAY, Config.HOURS_TO_RESET_SENT_SERVICE);				
				long diff = cal.getTime().getTime() - Calendar.getInstance().getTime().getTime();
				if(diff >0 ){
					layout.setBackgroundColor(mContext.getResources().getColor(R.color.sent));
				}
			}
		}

		File dir = new File(
				mContext.getDir(Constants.IMG_SERVICE, Context.MODE_PRIVATE), 
				"S" + serviceCode + ".jpg");
		if(dir.exists()){
			img.setImageURI(Uri.fromFile(dir));
		}else{
			img.setImageResource(R.drawable.no_image_service);
		}			
		return layout;
	}
}

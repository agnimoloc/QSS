package com.churpi.qualityss.client.helper;

import java.io.File;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;

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

			ImageView img = (ImageView)layout.findViewById(android.R.id.button1);
			
			Cursor c = getCursor();
			c.moveToPosition(position);
			int serviceId = c.getInt(c.getColumnIndex(DbService._ID));

			File dir = new File(
					mContext.getDir(Constants.IMG_SERVICE, Context.MODE_PRIVATE), 
					"IMGSRV_" + String.valueOf(serviceId) + ".jpg");
			if(dir.exists()){
				img.setImageURI(Uri.fromFile(dir));
			}else{
				img.setImageResource(R.drawable.ic_launcher);
			}			
		}else{
			layout = (FrameLayout)convertView;
		}		
		return layout;
	}
}

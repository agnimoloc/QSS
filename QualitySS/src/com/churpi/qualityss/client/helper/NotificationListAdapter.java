package com.churpi.qualityss.client.helper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbNotification;

public class NotificationListAdapter extends SimpleCursorAdapter {
	
	Context mContext;
	LayoutInflater mLayoutInflater;
	
	public NotificationListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, new String[]{}, new int[]{}, flags);
		mContext = context;
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewGroup layout;
		if(convertView == null){
			layout = (ViewGroup)mLayoutInflater.inflate(R.layout.item_notification, parent, false);
		}else{
			layout = (ViewGroup)convertView;
		}
		Cursor c = getCursor();
		c.moveToPosition(position);
		
		String title = c.getString(c.getColumnIndex(DbNotification.CN_TITLE));
		String message = c.getString(c.getColumnIndex(DbNotification.CN_MESSAGE));
		int priority = c.getInt(c.getColumnIndex(DbNotification.CN_PRIORITY));
		
		TextView text = (TextView)layout.findViewById(android.R.id.text1);
		text.setText(title);
		text = (TextView)layout.findViewById(android.R.id.text2);
		text.setText(message);
		
		ImageView image = (ImageView)layout.findViewById(android.R.id.icon);
		if(priority == 2){
			image.setImageResource(android.R.drawable.ic_dialog_alert);
		}else if(priority == 1){
			image.setImageResource(android.R.drawable.ic_dialog_info);
		}else if(priority == 0){
			image.setImageResource(android.R.drawable.ic_menu_info_details);
		}
		
		layout.refreshDrawableState();
		return layout;
	}
}

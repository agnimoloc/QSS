package com.churpi.qualityss.client.helper;

import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class StaffReviewAdapter extends SimpleCursorAdapter {
	
	LayoutInflater mLayoutInflater;
	
	public StaffReviewAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout itemView;
		if(convertView == null){
			itemView = (LinearLayout) mLayoutInflater.inflate(
					R.layout.item_staff_review, 
					parent, false); 
		}else{
			itemView = (LinearLayout)convertView;
		}
		Cursor c = getCursor();
		
		c.moveToPosition(position);		
		
		Button button = (Button)itemView.findViewById(android.R.id.button1);
		button.setTag(c.getInt(c.getColumnIndex(DbEmployee._ID)));
		
		TextView text = (TextView)itemView.findViewById(android.R.id.text1);
		text.setText(c.getString(c.getColumnIndex(DbEmployee.CN_NAME)));
				
		
		return itemView;
	}
}

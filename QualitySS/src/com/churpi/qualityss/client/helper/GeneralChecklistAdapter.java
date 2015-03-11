package com.churpi.qualityss.client.helper;

import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbGeneralCheckpoint;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;

public class GeneralChecklistAdapter extends SimpleCursorAdapter {

	LayoutInflater mLayoutInflater;
	
	public GeneralChecklistAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout itemView;
		if(convertView == null){
			itemView = (LinearLayout) mLayoutInflater.inflate(
					R.layout.item_general_checklist, 
					parent, false); 
		}else{
			itemView = (LinearLayout)convertView;
		}
		Cursor c = getCursor();
		
		c.moveToPosition(position);
		CheckBox checkbox = (CheckBox)itemView.findViewById(android.R.id.checkbox);
		checkbox.setText(c.getString(c.getColumnIndex(DbGeneralCheckpoint.CN_NAME)));
		Button button = (Button)itemView.findViewById(android.R.id.button1);
		button.setTag(c.getInt(c.getColumnIndex(DbGeneralCheckpoint._ID)));
		button = (Button)itemView.findViewById(android.R.id.button2);
		button.setTag(c.getInt(c.getColumnIndex(DbGeneralCheckpoint._ID)));		
		return itemView;
	}
	

}

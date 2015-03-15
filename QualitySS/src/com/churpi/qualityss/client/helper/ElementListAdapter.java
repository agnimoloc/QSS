package com.churpi.qualityss.client.helper;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.dto.EmployeeDTO;

public class ElementListAdapter extends SimpleCursorAdapter{
	LayoutInflater mLayoutInflater;
	Context mContext;
	
	public ElementListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		mContext = context;
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout;
		if(convertView == null){
			layout = (LinearLayout) mLayoutInflater.inflate(
					R.layout.item_staff_review, 
					parent, false); 

			ImageView img = (ImageView)layout.findViewById(android.R.id.icon);
			
			Cursor c = getCursor();
			c.moveToPosition(position);
			EmployeeDTO employee = new EmployeeDTO();
			employee.fillFromCursor(c);

			File dir = new File(
					mContext.getDir(Constants.IMG_EMPLOYEE, Context.MODE_PRIVATE), 
					"IMGEMP_" + String.valueOf(employee.getElementoId()) + ".jpg");
			if(dir.exists()){
				img.setImageURI(Uri.fromFile(dir));
			}else{
				img.setImageResource(R.drawable.ic_launcher);
			}
			
			TextView text = (TextView)layout.findViewById(android.R.id.text1);
			text.setText(employee.getCode());
			text = (TextView)layout.findViewById(android.R.id.text2);
			text.setText(employee.getNombre());
			text = (TextView)layout.findViewById(android.R.id.content);
			text.setText(employee.getMatricula());			
		}else{
			layout = (LinearLayout)convertView;
		}		
		return layout;
	}
}
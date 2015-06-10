package com.churpi.qualityss.client.helper;

import com.churpi.qualityss.client.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class InventoryListAdapter extends SimpleCursorAdapter {
	
	Context mContext;
	LayoutInflater mLayoutInflater;
	String[] mFrom;
	int[] mTo;
	int mLayout;
	/**
     * Standard constructor.
     * This Adapter set to parent the first column like int Id to parent tag property
     * 
     * @param context The context where the ListView associated with this
     *            InventoryListFactory is running
     * @param layout resource identifier of a layout file that defines the views
     *            for this list item. The layout file should include at least
     *            those named views defined in "to"
     * @param c The database cursor.  Can be null if the cursor is not available yet.
     * @param from A list of column names representing the data to bind to the UI.  Can be null 
     *            if the cursor is not available yet.
     * @param to The views that should display column in the "from" parameter.
     *            These should all be TextViews. The first N views in this list
     *            are given the values of the first N columns in the from
     *            parameter.  Can be null if the cursor is not available yet.
     * @param flags Flags used to determine the behavior of the adapter,
     * as per {@link CursorAdapter#CursorAdapter(Context, Cursor, int)}.
     */
	public InventoryListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, new String[]{}, new int[]{}, flags);
		mContext = context;
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFrom = from;
		mTo = to;
		mLayout = layout;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewGroup layout;
		if(convertView == null){
			layout = (ViewGroup)mLayoutInflater.inflate(mLayout, parent, false);
		}else{
			layout = (ViewGroup)convertView;
		}
		Cursor c = getCursor();
		c.moveToPosition(position);
		int id = c.getInt(0);
		Integer checked = null;
		if(!c.isNull(1)){
			checked = c.getInt(1);
		}
		RadioGroup group = (RadioGroup)layout.findViewById(R.id.radioGroup);
		group.clearCheck();
		group.setTag(id);
		for(int i = 0; i < mTo.length; i++){
			if(mFrom.length > i){
				String text = c.getString(c.getColumnIndex(mFrom[i]));
				TextView field = (TextView)layout.findViewById(mTo[i]);
				field.setText(text);
			}else{
				RadioButton field = (RadioButton)group.findViewById(mTo[i]);
				if(checked != null && checked == Integer.parseInt(field.getTag().toString())){
					field.setChecked(true);
				}
			}
		}	
		
		ImageButton btn = (ImageButton)layout.findViewById(R.id.btnPicture);
		btn.setTag(id);
		btn = (ImageButton)layout.findViewById(R.id.btnComment);
		btn.setTag(id);
		
		layout.refreshDrawableState();
		return layout;
	}
}

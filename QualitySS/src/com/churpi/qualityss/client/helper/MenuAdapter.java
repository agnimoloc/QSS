package com.churpi.qualityss.client.helper;

import com.churpi.qualityss.client.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {

	public static class MenuItem{
		private int resourceId;
		private String title;
		private int key;
		
		public int getKey() {
			return key;
		}
		public void setKey(int key) {
			this.key = key;
		}
		public int getResourceId() {
			return resourceId;
		}
		public void setResourceId(int resourceId) {
			this.resourceId = resourceId;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}		
	}
	
	private MenuItem[] items;
	private LayoutInflater mLayoutInflater;
	
	public MenuAdapter(Context context, MenuItem...items){
		this.items = items;
		
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return items[position].getKey();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout;
		if(convertView == null){
			layout = (LinearLayout) mLayoutInflater.inflate(
					R.layout.item_menu, 
					parent, false); 
		}else{
			layout = (LinearLayout)convertView;
		}		
		MenuItem item = (MenuItem)getItem(position);
		TextView text = (TextView)layout.findViewById(android.R.id.text1);
		text.setText(item.getTitle());
		
		ImageView image = (ImageView)layout.findViewById(android.R.id.icon);
		image.setImageResource(item.getResourceId());
		
		return layout;
	}

}

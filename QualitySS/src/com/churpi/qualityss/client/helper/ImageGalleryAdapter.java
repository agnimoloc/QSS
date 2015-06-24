package com.churpi.qualityss.client.helper;

import java.io.File;

import com.churpi.qualityss.client.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ImageGalleryAdapter  extends BaseAdapter{
	
	File[] images;
	LayoutInflater mLayoutInflater;
	
	public ImageGalleryAdapter(Context context, File... images){
		this.images = images;
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public Object getItem(int position) {
		return images[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FrameLayout layout;
		if(convertView == null){
			layout = (FrameLayout) mLayoutInflater.inflate(
					R.layout.item_gallery, 
					parent, false); 
		}else{
			layout = (FrameLayout)convertView;
		}		
		
		ImageView image = (ImageView)layout.findViewById(android.R.id.icon);
		
		ResizeImageTask task = new ResizeImageTask(image);
		task.execute(images[position].getAbsolutePath(), "100");
		
		return layout;
	}
	public void updateList(File[] files) {
		this.images = files;
		this.notifyDataSetChanged();
	}

}

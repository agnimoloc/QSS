package com.churpi.qualityss.client.helper;

import java.util.List;

import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.dto.WarningDetailDTO;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class WarningReasonListAdapter extends BaseAdapter {


	

	Context mContext;
	LayoutInflater mLayoutInflater;
	List<WarningDetailDTO> list;
	
	
	public WarningReasonListAdapter(Context context, List<WarningDetailDTO> data) {
		mContext = context;
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewGroup layout;
		if(convertView == null){
			layout = (ViewGroup)mLayoutInflater.inflate(R.layout.item_warning_reason, parent, false);
		}else{
			layout = (ViewGroup)convertView;
		}
		
		WarningDetailDTO detail = list.get(position);
		CheckBox check = (CheckBox)layout.findViewById(R.id.checkBox1);
		check.setEnabled(detail.getWarningId()==0);
		check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				WarningDetailDTO detail = (WarningDetailDTO)buttonView.getTag();
				detail.setId(isChecked?-1:0);				
			}
		});
		check.setText(detail.getWarningReasonDesc());
		check.setTag(detail);
		check.setChecked(detail.getId()!=0);
		return layout;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position).getWarningReasonId();
	}

}

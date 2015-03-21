package com.churpi.qualityss.client.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.dto.QuestionDTO;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class StaffReviewQuestionAdapter extends BaseExpandableListAdapter {

	List<String> headers;
	HashMap<String, List<QuestionDTO>> data;
	LayoutInflater mLInflater;
	int[] mRadioIds;

	public StaffReviewQuestionAdapter(Context context, QuestionDTO[] questions, int... radioIds){
		mLInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		mRadioIds = radioIds;
		data = new HashMap<String, List<QuestionDTO>>();
		headers = new ArrayList<String>();
		for(QuestionDTO question : questions){
			String section = question.getNombreSeccion();
			if(!data.containsKey(section)){
				data.put(section, new ArrayList<QuestionDTO>());
				headers.add(section);
			}
			List<QuestionDTO> list = data.get(section);
			list.add(question);
		}		
	}

	@Override
	public int getGroupCount() {
		return headers.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		List<QuestionDTO> list = data.get(headers.get(groupPosition));
		return list.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return headers.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		List<QuestionDTO> list = data.get(headers.get(groupPosition));		
		return list.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewGroup layout;
		if(convertView == null){
			layout = (ViewGroup)mLInflater.inflate(R.layout.item_group_question, parent, false);
		}else{
			layout = (ViewGroup)convertView;
		}
		TextView text = (TextView)layout.findViewById(android.R.id.text1);
		text.setText(headers.get(groupPosition));
		return layout;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewGroup layout;
		if(convertView == null){
			layout = (ViewGroup)mLInflater.inflate(R.layout.item_staff_review_question, parent, false);
		}else{
			layout = (ViewGroup)convertView;
		}
		QuestionDTO question = ((QuestionDTO)getChild(groupPosition, childPosition));

		TextView text = (TextView)layout.findViewById(android.R.id.text1);
		text.setText(question.getDescripcion());

		RadioGroup group = (RadioGroup)layout.findViewById(R.id.radioGroup);
		group.setTag(question);



		for(int radioId : mRadioIds){
			RadioButton button = (RadioButton)layout.findViewById(radioId);
			if(question.getResultado() != null){
				if(button.getTag().toString().compareTo(question.getResultado())==0){
					button.setChecked(true);
				}else{
					button.setChecked(false);
				}
			}
		}			
		return layout;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}

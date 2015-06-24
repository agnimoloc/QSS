package com.churpi.qualityss.client.helper.workflow;

import android.app.Activity;
import android.util.SparseArray;


public class ActivityProcess{
	public ActivityProcess(Class<? extends Activity> activity, TransitionButton ...buttons){
		this.activity = activity;
		for(TransitionButton button : buttons){
			this.transitionButtons.put(button.getKey(), button);
		}
	}
	private Class<? extends Activity> activity;
	private SparseArray<TransitionButton> transitionButtons = new SparseArray<TransitionButton>();
	
	public Class<? extends Activity> getActivity(){
		return activity;
	}
	public TransitionButton getTransitionButton(int key){
		return transitionButtons.get(key);
	}
}

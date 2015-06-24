package com.churpi.qualityss.client.helper.workflow;

import android.app.Activity;

public class TransitionButton {
	private int key;
	private boolean close;
	private Class<? extends Activity> nextActivity;			
	public TransitionButton(int key, boolean close, Class<? extends Activity> nextActivity){
		this.key = key;
		this.close = close;
		this.nextActivity = nextActivity;
	}
	public TransitionButton(int key, Class<? extends Activity> nextActivity){
		this.key = key;
		this.close = false;
		this.nextActivity = nextActivity;
	}
	public int getKey() {
		return key;
	}
	public boolean isClose() {
		return close;
	}
	public Class<? extends Activity> getNextActivity() {
		return nextActivity;
	}	
}

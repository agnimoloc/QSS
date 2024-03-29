package com.churpi.qualityss.service;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.content.Context;

public class VolleySingleton {

	private static VolleySingleton mInstance;
	private RequestQueue mRequestQueue;
	private static Context mContext;

	public VolleySingleton(Context context){
		mContext = context;
		mRequestQueue = getRequestQueue();
	}

	public static synchronized VolleySingleton getInstance(Context context){

		if(mInstance == null){
			mInstance = new VolleySingleton(context);
		}

		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			// getApplicationContext() is key, it keeps you from leaking the
			// Activity or BroadcastReceiver if someone passes one in.
			mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}

}

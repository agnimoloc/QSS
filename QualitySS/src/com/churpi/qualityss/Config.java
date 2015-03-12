package com.churpi.qualityss;

import android.net.Uri;

import com.churpi.qualityss.client.BuildConfig;

public class Config {
	
	private static final String SERVER_URL = BuildConfig.DEBUG ?  
			"http://192.168.1.69":"";
	private static final String SERVER_GET_DATA = "GiaWebSite/api/ServiceApi/GetServices?lastUpdate=null";
	//private static final String SERVER_GET_DATA = "QSSWeb/Data/GetData";
	private static final String SERVER_GET_FILE = "QSSWeb/Data/GetDataFile";
	public static final String SERVER_GET_FILE_FLD_TIMESTAMP = "timestamp";
	public static final String SERVER_GET_DATA_FLD_LAST_UPDATE = "lastUpdate";
	
	public static final int SERVER_GET_FILE_TIMEOUT = 1000 * 60; 
	
	public class ServerAction{
		public static final int GET_DATA = 0;
		public static final int GET_FILE = 1;
	}
	
	public static Uri getUrl(int serverAction){
		Uri.Builder builder = Uri.parse(SERVER_URL).buildUpon();
		if(serverAction == ServerAction.GET_DATA){
			builder.appendEncodedPath(SERVER_GET_DATA);
		}else if(serverAction == ServerAction.GET_FILE){
			builder.appendEncodedPath(SERVER_GET_FILE);
		}
		return builder.build();
	}
}

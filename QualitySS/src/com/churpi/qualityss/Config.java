package com.churpi.qualityss;

import android.net.Uri;

import com.churpi.qualityss.client.BuildConfig;

public class Config {
	
	private static final String SERVER_URL = BuildConfig.DEBUG ?  
			"http://giatest2.cloudapp.net":"http://giatest2.cloudapp.net";
	private static final String SERVER_GET_DATA = "api/ServiceApi/GetServices";
	private static final String SERVER_AUTHENTICATE = "api/ServiceApi/ValidateUserPassword";
	private static final String SERVER_SEND_DATA = "api/EvaluacionApi/SendServices";
	private static final String SERVER_SEND_FILE = "api/EvaluacionApi/FileUpload";
	
	public static final String SERVER_GET_FILE_FLD_TIMESTAMP = "timestamp";
	
	public static final int SERVER_GET_FILE_TIMEOUT = 1000 * 60;
	public static final int REFRESH_TIME = 6000;
	
	public static final int HOURS_TO_RESET_SENT_SERVICE = 1;
	
	public class ServerAction{
		public static final int GET_DATA = 0;
		public static final int GET_AUTHENTICATE = 1;
		public static final int GET_BARCODE = 2;
		public static final int SEND_DATA = 3;
		public static final int DOWNLOAD_FILE = 4;
		public static final int SEND_FILE = 5;
	}
	
	public class FileType{
		public static final int SERVICE_IMAGE = 0;
		public static final int EMPLOYEE_IMAGE = 1;
		public static final int DOCUMENT_IMAGE = 2;
	}
	
	public static String getUrl(int serverAction, String... args){
		Uri.Builder builder = Uri.parse(SERVER_URL).buildUpon();
		if(serverAction == ServerAction.GET_DATA){
			builder.appendEncodedPath(SERVER_GET_DATA);
			builder.appendQueryParameter("lastUpdate", args.length == 0 ? null : args[0]);
		}else if(serverAction == ServerAction.GET_AUTHENTICATE){
			builder.appendEncodedPath(SERVER_AUTHENTICATE);
			builder.appendQueryParameter("userName", args[0]);
			builder.appendQueryParameter("password", args[1]);
		}else if(serverAction == ServerAction.GET_BARCODE){
			builder.appendEncodedPath(SERVER_AUTHENTICATE);
			builder.appendQueryParameter("code", args[0]);
		}else if(serverAction == ServerAction.SEND_DATA){
			builder.appendEncodedPath(SERVER_SEND_DATA);
		}else if(serverAction == ServerAction.DOWNLOAD_FILE ){
			builder = Uri.parse(args[0]).buildUpon();
			builder.appendPath(args[1]);
		}else if(serverAction == ServerAction.SEND_FILE){
			builder.appendEncodedPath(SERVER_SEND_FILE);
		}
		return builder.build().toString();
	}
}

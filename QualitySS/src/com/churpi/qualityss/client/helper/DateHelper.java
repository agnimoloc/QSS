package com.churpi.qualityss.client.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateHelper {

	private static String DATE_FORMAT = "yyyyMMddhhmmss";
	
	public static String getCurrentTime(){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);													
		return format.format(calendar.getTime());
	}
}

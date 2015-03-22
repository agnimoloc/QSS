package com.churpi.qualityss.client.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {

	private static String DATE_FORMAT = "yyyyMMddhhmmss";
	private static String JSON_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.0000000+00:00";
	
	public static String getCurrentTime(){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);													
		return format.format(calendar.getTime());
	}
	
	public static String getJSONDate(String date){
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
		try {
			Date d = format.parse(date);
			SimpleDateFormat formatJson = new SimpleDateFormat(JSON_DATE_FORMAT);
			formatJson.setTimeZone(TimeZone.getTimeZone("UTC"));
			return formatJson.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String fromJSONDate(String date){
		SimpleDateFormat formatJson = new SimpleDateFormat(JSON_DATE_FORMAT);
		formatJson.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			Date d = formatJson.parse(date);
			SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
			return format.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}

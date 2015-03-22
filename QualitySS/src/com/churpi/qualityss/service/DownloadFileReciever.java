package com.churpi.qualityss.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.churpi.qualityss.Config;
import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.BuildConfig;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class DownloadFileReciever extends BroadcastReceiver {

	DownloadManager dm;
	Context mContext;
	HashMap<Long, Integer> enqueue;
	
	
	public DownloadFileReciever(Context context){		
		dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		mContext = context;
		enqueue = new HashMap<Long, Integer>();
	}
	
	public void downloadFile(int fileType, String filename){		
		String url = "";
		
		if(fileType == Config.FileType.DOCUMENT_IMAGE){
			url = Constants.getPref(mContext).getString(Constants.PREF_IMAGEURL, "");
		}else if(fileType == Config.FileType.EMPLOYEE_IMAGE){
			url = Constants.getPref(mContext).getString(Constants.PREF_IMAGEURL, "");
		}else if(fileType == Config.FileType.SERVICE_IMAGE){
			url = Constants.getPref(mContext).getString(Constants.PREF_IMAGEURL, "");
		}
		
		if(BuildConfig.DEBUG){
			url = "http://192.168.0.69/Images";
		}
		url = Config.getUrl(Config.ServerAction.DOWNLOAD_FILE, url, filename);
		Request request = new Request( Uri.parse(url));
		enqueue.put(dm.enqueue(request), fileType);
	}
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			long downloadId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, 0);

			long[] enqueueList = new long[enqueue.size()];
			int i = 0;
			for(long key : enqueue.keySet()){
				enqueueList[i++] = key;
			}
			Query query = new Query();
			query.setFilterById(enqueueList);
			Cursor c = dm.query(query);
			if (c.moveToFirst()) {
				do{
					int columnIndex = c
							.getColumnIndex(DownloadManager.COLUMN_STATUS);
					if (DownloadManager.STATUS_SUCCESSFUL == c
							.getInt(columnIndex)) {
						long id = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID));
						int fileType = enqueue.get(id);
						File to = null;
						if(fileType == Config.FileType.EMPLOYEE_IMAGE){
							to = context.getDir(Constants.IMG_EMPLOYEE, Context.MODE_PRIVATE);	
						}else if(fileType == Config.FileType.SERVICE_IMAGE){
							to = context.getDir(Constants.IMG_SERVICE, Context.MODE_PRIVATE);	
						}else if(fileType == Config.FileType.DOCUMENT_IMAGE){
							to = context.getDir(Constants.DOC_DIR, Context.MODE_PRIVATE);	
						}
						if(to != null){
							String uriString = c
								.getString(c
										.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
							File from = new File(uriString);							
							try {
								copyFile(from, to);
								from.delete();
							} catch (IOException e) {
								e.printStackTrace();
							}							
						}
					}
				}while(c.moveToNext());
			}
			c.close();
		}
	}
	
	private static void copyFile(File source, File dest)
			throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}

}

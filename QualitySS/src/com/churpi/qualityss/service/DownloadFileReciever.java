package com.churpi.qualityss.service;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import com.churpi.qualityss.client.dto.DataDTO;
import com.churpi.qualityss.client.dto.EmployeeDTO;
import com.churpi.qualityss.client.dto.ServiceDTO;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class DownloadFileReciever extends BroadcastReceiver {

	DownloadManager dm;
	Context mContext;
	HashMap<Long, FileRetrieve> enqueue;

	private class FileRetrieve{
		int fileType;
		String fileName;
		public int getFileType() {
			return fileType;
		}
		public void setFileType(int fileType) {
			this.fileType = fileType;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

	}

	private static DataDTO globalData =null;

	public static void setDataDTO(DataDTO data){
		globalData = data;
	}


	public DownloadFileReciever(Context context){		
		dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		mContext = context;
		enqueue = new HashMap<Long, FileRetrieve>();
	}

	public void downloadFiles(){
		if(globalData != null){
			for(ServiceDTO service : globalData.getServicios()){
				downloadFile(Config.FileType.SERVICE_IMAGE, "S"+ service.getCode()+".jpg");

			}
			for(EmployeeDTO employee : globalData.getElementos()){
				downloadFile(Config.FileType.EMPLOYEE_IMAGE, employee.getCode()+".jpg");
			}
			//TODO: add way to save documents
		}
	}

	public void downloadFile(int fileType, String fileName){		
		String url = "";

		if(fileType == Config.FileType.DOCUMENT_IMAGE){
			url = Constants.getPref(mContext).getString(Constants.PREF_IMAGEURL, "");
		}else if(fileType == Config.FileType.EMPLOYEE_IMAGE){
			url = Constants.getPref(mContext).getString(Constants.PREF_IMAGEURL, "");
		}else if(fileType == Config.FileType.SERVICE_IMAGE){
			url = Constants.getPref(mContext).getString(Constants.PREF_IMAGEURL, "");
		}

		if(BuildConfig.DEBUG){
			url = "http://192.168.1.69/GiaWebSite/ServiceImages";
		}
		url = Config.getUrl(Config.ServerAction.DOWNLOAD_FILE, url, fileName);
		Request request = new Request( Uri.parse(url));
		FileRetrieve fileMeta = new FileRetrieve();
		fileMeta.setFileName(fileName);
		fileMeta.setFileType(fileType);
		enqueue.put(dm.enqueue(request), fileMeta);
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if(enqueue.containsKey(referenceId)){
				Query query = new Query();
				query.setFilterById(referenceId);
				Cursor c = dm.query(query);
				if (c.moveToFirst()) {
					int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
					if (DownloadManager.STATUS_SUCCESSFUL == status) {
						FileRetrieve fileMeta = enqueue.get(referenceId);
						File to = null;
						if(fileMeta.getFileType() == Config.FileType.EMPLOYEE_IMAGE){
							to = context.getDir(Constants.IMG_EMPLOYEE, Context.MODE_PRIVATE);	
						}else if(fileMeta.getFileType() == Config.FileType.SERVICE_IMAGE){
							to = context.getDir(Constants.IMG_SERVICE, Context.MODE_PRIVATE);	
						}else if(fileMeta.getFileType() == Config.FileType.DOCUMENT_IMAGE){
							to = context.getDir(Constants.DOC_DIR, Context.MODE_PRIVATE);	
						}
						to = new File(to, fileMeta.getFileName());
						if(to != null){
							String localFileString = c.getString(
									c.getColumnIndex(
											DownloadManager.COLUMN_LOCAL_FILENAME));
							File from = new File(localFileString);							
							try {
								copyFile(from, to);
								from.delete();
							} catch (IOException e) {
								e.printStackTrace();
							}							
						}
					}
				}
				c.close();
			}
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

	public void dispose(){
		dm = null;
	}

}

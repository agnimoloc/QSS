package com.churpi.qualityss.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.churpi.qualityss.Config;
import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceFile;
import com.churpi.qualityss.client.dto.DataDTO;
import com.churpi.qualityss.client.dto.EmployeeDTO;
import com.churpi.qualityss.client.dto.ServiceDTO;
import com.churpi.qualityss.client.dto.ServiceFilesDTO;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.LongSparseArray;

public class DownloadFileReciever extends BroadcastReceiver {

	DownloadManager dm;
	Context mContext;
	LongSparseArray<FileRetrieve> enqueue;
	private static final int SERVICE_IMAGE = 0;
	private static final int EMPLOYEE_IMAGE = 1;
	private static final int DOCUMENT_FILE = 2;

	private class FileRetrieve{
		int fileType;
		String fileName;
		Map<String, String> extraData;
		
		public FileRetrieve(int fileType, String fileName, Map<String, String> extra){
			this.fileType = fileType;
			this.fileName = fileName;
			this.extraData = extra;
		}
						
		public Map<String, String> getExtraData() {
			return extraData;
		}

		public int getFileType() {
			return fileType;
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
		enqueue = new LongSparseArray<FileRetrieve>();
	}

	public void downloadFiles(){
		if(globalData != null){
			for(ServiceDTO service : globalData.getServicios()){
				downloadFile(SERVICE_IMAGE, "S" + service.getCode().trim() + ".jpg", null);
				
			}
			for(EmployeeDTO employee : globalData.getElementos()){
				downloadFile(EMPLOYEE_IMAGE, employee.getCode().trim() + ".jpg", null);
			}
			
			if(globalData.getArchivosServicio() != null){
				for(ServiceFilesDTO serviceFiles : globalData.getArchivosServicio() ){
					if(serviceFiles.getArchivosReferencia() != null && serviceFiles.getArchivosReferencia().keySet() != null){
						for(String key : serviceFiles.getArchivosReferencia().keySet()){
							Map<String, String> extra = new HashMap<String, String>();
							extra.put(DbServiceFile.CN_SERVICE, String.valueOf(serviceFiles.getServicioId()));
							extra.put(DbServiceFile.CN_NAME, key);
							downloadFile(DOCUMENT_FILE, 
									serviceFiles.getArchivosReferencia().get(key),
									extra);
						}
					}
						
				}
			}

		}
	}

	public void downloadFile(int fileType, String fileName, Map<String, String> extraData){		
		String url = "";

		if(fileType == DOCUMENT_FILE){
			//url = Constants.getPref(mContext).getString(Constants.PREF_IMAGEURL, "");
			url = Config.getUrl(Config.ServerAction.GET_DOCUMENTS);
		}else if(fileType == EMPLOYEE_IMAGE || fileType == SERVICE_IMAGE){
			url = Config.getUrl(Config.ServerAction.GET_IMAGES);
		}

		url = Config.getUrl(Config.ServerAction.DOWNLOAD_FILE, url, fileName);
		Request request = new Request( Uri.parse(url));
		FileRetrieve fileMeta = new FileRetrieve(fileType, fileName, extraData);
		
		enqueue.put(dm.enqueue(request), fileMeta);
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if(enqueue.indexOfKey(referenceId) > -1){
				Query query = new Query();
				query.setFilterById(referenceId);
				Cursor c = dm.query(query);
				if (c.moveToFirst()) {
					int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
					if (DownloadManager.STATUS_SUCCESSFUL == status) {
						FileRetrieve fileMeta = enqueue.get(referenceId);
						File to = null;
						if(fileMeta.getFileType() == EMPLOYEE_IMAGE){
							to = context.getDir(Constants.IMG_EMPLOYEE, Context.MODE_PRIVATE);
						}else if(fileMeta.getFileType() == SERVICE_IMAGE){
							to = context.getDir(Constants.IMG_SERVICE, Context.MODE_PRIVATE);	
						}else if(fileMeta.getFileType() == DOCUMENT_FILE){
							to = new File(context.getFilesDir(),Constants.DOC_DIR);
							if(!to.exists()){
								to.mkdir();
							}
							DbTrans.write(context, fileMeta, new DbTrans.Db() {								
								@Override
								public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
									FileRetrieve fileMeta = (FileRetrieve)parameter;
									File file = new File(fileMeta.getFileName());
									fileMeta.setFileName(file.getName());
									Cursor c = db.query(DbServiceFile.TABLE_NAME,
											new String[]{ DbServiceFile._ID },
											DbServiceFile.CN_SERVICE + "=? AND "+ DbServiceFile.CN_NAME + "=?",
											new String[]{
												fileMeta.getExtraData().get(DbServiceFile.CN_SERVICE), 
												fileMeta.getExtraData().get(DbServiceFile.CN_NAME)},
											null,null,null);
									if(c.moveToFirst()){
										String[] parts = fileMeta.getFileName().split("\\.");
										fileMeta.setFileName(
												String.valueOf(c.getInt(c.getColumnIndex(DbServiceFile._ID)) 
												+ "." + parts[parts.length-1]));
									}
									c.close();
									return null;
								}
							});
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

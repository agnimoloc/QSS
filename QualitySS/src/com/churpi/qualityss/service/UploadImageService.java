package com.churpi.qualityss.service;

import java.io.File;

import org.json.JSONArray;

import com.churpi.qualityss.Config;
import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.db.DbActions;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbImageToSend;
import com.churpi.qualityss.client.helper.ProgressListener;
import com.churpi.qualityss.client.helper.SendMultiPartEntity;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class UploadImageService extends IntentService {

	Context mContext;
	NotificationCompat.Builder builder;
	NotificationManager notificationManager ;
	
	public UploadImageService() {
		super("UploadImageService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mContext = getApplicationContext();
		
		builder = new NotificationCompat.Builder(mContext)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle(getString(R.string.msg_send_imgs))
		.setContentText(getString(R.string.msg_send_imgs_start));

		notificationManager =
				(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		sendImages();
		
	}
	
	private void sendImages(){
		String url = (String)DbTrans.read(mContext, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				String key = null;

				Cursor sCursor = db.query(
						DbImageToSend.TABLE_NAME, 
						new String[]{DbImageToSend.CN_URL},
						null, 
						null, 
						null, null, null, "1");
				if(sCursor.moveToFirst()){						
					key = sCursor.getString(sCursor.getColumnIndex(DbImageToSend.CN_URL));
				}
				sCursor.close();
				return key;
			}
		});

		if(url != null){
			File file =new File(url);
			if(file.exists()){
				SendMultiPartEntity sender = new SendMultiPartEntity(
						file, 
						Config.getUrl(Config.ServerAction.SEND_FILE), 
						new ProgressListener() {							
							@Override
							public void onProgress(long progress, long total, File file) {
								builder.setContentText(getString(R.string.msg_send_imgs_sending));
								//builder.setContentText(file.getName());
								double inc = ((double)((double)progress / (double)total)*100);
								builder.setProgress(100, (int)inc , false);
								notificationManager.notify(0, builder.build());
							}

							@Override
							public void onFinish(String response, File file) {
								try{
								JSONArray array = new JSONArray(response);	
								
								if(array.length() > 0 && array.getString(0).contains("File saved")){
									DbActions.deleteImageAndFromQueue(mContext, file);	
									notificationManager.cancel(0);
									
									sendImages();
								}else{
									builder.setContentText(getString(R.string.ttl_error));
									if(array.length() > 0){
										builder.setContentText(array.join(" : "));
									}
									notificationManager.notify(0, builder.build());
								}
								}catch(Exception e){
									builder.setContentTitle(getString(R.string.ttl_error));
									builder.setContentText(getString(R.string.msg_send_imgs_error_response));
									notificationManager.notify(0, builder.build());
									Log.e("UploadImage", response, e);
								}
								
							}

							@Override
							public void onError(Exception e, File file) {
								builder.setContentText("Error");
								builder.setContentText(e.getMessage());
								notificationManager.notify(0, builder.build());
							}
						});
				sender.send();
			}else{
				DbActions.deleteImageAndFromQueue(mContext, file);
				WorkflowHelper.uploadImages(mContext);
			}
		}
	}

}

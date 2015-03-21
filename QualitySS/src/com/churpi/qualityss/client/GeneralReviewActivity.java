package com.churpi.qualityss.client;

import java.io.File;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.helper.GeneralChecklistAdapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class GeneralReviewActivity extends Activity {

	private Cursor c;
	public static final String SERVICE_ID = "service_id";
	int serviceId;
	int checkpointId;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_IMAGE_SHOW = 2;
	static final int REQUEST_SINGLE_COMMENT = 3;
	static final String GENERAL_CHECKLIST_ID = "general_checklist_id"; 

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_general_review);
		
		serviceId = getIntent().getIntExtra(SERVICE_ID, 0);
		
		ListView list = (ListView)findViewById(R.id.listView1);
				
		DbTrans.read(this, new DbTrans.Db() {
			@Override
			public Object onDo(Context context, SQLiteDatabase db) {
				/*c = db.query(
						DbGeneralCheckpoint.TABLE_NAME, 
						new String[]{DbGeneralCheckpoint._ID, DbGeneralCheckpoint.CN_NAME}, 
						null, null, null, null, null, null);*/
				return null;
				
			}
		});		
		/*String[] from = new String[]{DbGeneralCheckpoint.CN_NAME};
		int[] to = new int[]{ android.R.id.checkbox};
		list.setAdapter(new GeneralChecklistAdapter(
				this, 
				R.layout.item_general_checklist, c, 
				from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));*/
	}
	
	@Override
	protected void onDestroy() {
		c.close();
		super.onDestroy();
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.general_review, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
		
	public void onClick_TakePhoto(View v){
		checkpointId = (Integer)v.getTag();
		File dest = getDestImage();
		if(dest.exists()){
			Intent showPhotoIntent = new Intent(this, ShowPhotoActivity.class);
			showPhotoIntent.putExtra(ShowPhotoActivity.FILE_URI, Uri.fromFile(dest));
			showPhotoIntent.putExtra(ShowPhotoActivity.SHOW_NEW_BUTTON, true);
			startActivityForResult(showPhotoIntent,REQUEST_IMAGE_SHOW);
		}else{
			takePhoto();
		}
	}
	
	private File getDestImage(){
		
		File dest = new File (
				getExternalFilesDir(Environment.DIRECTORY_PICTURES), 
        		String.format(Constants.PHOTO_GENERAL_CHECKPOINT,checkpointId, serviceId));
		return dest;
	}
	
	private void takePhoto(){
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			File dest = getDestImage();
	    	takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(dest));
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	public void onClick_Comment(View v){
		checkpointId = (Integer)v.getTag();
		Intent getComment = new Intent(this, CheckpointCommentActivity.class);
		startActivityForResult(getComment, REQUEST_SINGLE_COMMENT);
	}
	
	public void onClick_Next(View v){
		//TODO: add validations to continue
		Intent staffReview = new Intent(this, StaffReviewListActivity.class);
		staffReview.putExtra(StaffReviewListActivity.FLD_SERVICE_ID, serviceId);
		startActivity(staffReview);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			
			//For get an thumbnail is in extra data
	        /*Bundle extras = data.getExtras();
	        Bitmap bmp = (Bitmap) extras.get("data");
	        
	        File dest = new File (
	        		this.getDir(Constants.PHOTO_DIR, Context.MODE_PRIVATE), 
	        		String.format(Constants.PHOTO_GENERAL_CHECKPOINT,checkpointId, serviceId));
            if(dest.exists())
            	dest.delete();
	        FileOutputStream out = null;
			try {
				out = new FileOutputStream(dest);
				bmp.compress(Bitmap.CompressFormat.PNG, 100,out);
				Toast.makeText(this, getString(R.string.msg_photo_take_successfully), Toast.LENGTH_SHORT).show();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Alerts.showGenericError(this);
			} finally {
			    try {
			        if (out != null) {
			            out.close();
			        }
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			}*/
			Toast.makeText(this, getString(R.string.msg_photo_take_successfully), Toast.LENGTH_SHORT).show();	        
	    }else if(requestCode == REQUEST_SINGLE_COMMENT && resultCode == RESULT_OK){
	    	Bundle extras = data.getExtras();
	    	String comment = extras.getString(CheckpointCommentActivity.FLD_COMMENT);
	    	DbTrans.write(this, new DbTrans.Db() {
				@Override
				public Object onDo(Context context, SQLiteDatabase db) {
					/*ContentValues values = new ContentValues();
					values.put(DbGeneralCheckpointResult.CN_SERVICE, serviceId);
					values.put(DbGeneralCheckpointResult.CN_GENERAL_CHECKPOINT, checkpointId);
					values.put(DbGeneralCheckpointResult.CN_COMMENT, comment);
					int affected = db.update(DbGeneralCheckpointResult.TABLE_NAME, 
							values, 
							DbGeneralCheckpointResult.CN_SERVICE + "=? AND " +
							DbGeneralCheckpointResult.CN_GENERAL_CHECKPOINT + "=?"
							, new String[]{
								String.valueOf(serviceId), 
								String.valueOf(checkpointId)});
					if(affected == 0){
						db.insert(DbGeneralCheckpointResult.TABLE_NAME, null, values);
					}*/
					return null;
				}
			});
	    	Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show();	 
	    }else if(requestCode == REQUEST_IMAGE_SHOW && resultCode == RESULT_OK){
	    	takePhoto();
	    }
		super.onActivityResult(requestCode, resultCode, data);
	}
}

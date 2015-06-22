package com.churpi.qualityss.client;

import java.io.File;
import java.net.URI;

import com.churpi.qualityss.client.helper.ImageHelper;
import com.churpi.qualityss.client.helper.ResizeImageTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ShowPhotoActivity extends Activity {

	protected static final String FILE_URI = "file_path";
	protected static final String SHOW_BUTTONS = "show_buttons";
	
	private static final int REQUEST_PHOTO = 0;
	
	private Uri uri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_photo);
		
		Bundle extras = getIntent().getExtras();
		boolean showButton = extras.getBoolean(SHOW_BUTTONS, false); 
		uri = (Uri)extras.get(FILE_URI);
		if(showButton){
			Button text = (Button)findViewById(android.R.id.button2);
			text.setVisibility(View.VISIBLE);
		}
		
		loadPhoto();
	}
	
	public void onClick_Ok(View v){
		setResult(RESULT_CANCELED);
		finish();
	}
	
	public void onClick_New(View v){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.ttl_override_image);
		dialogBuilder.setMessage(R.string.msg_override_image);
		dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				takePhoto();
			}
			
		});
		dialogBuilder.create().show();
	}
	
	public void onClick_delete(View v){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.ttl_delete_image);
		dialogBuilder.setMessage(R.string.msg_delete_image);
		dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deletePhoto();
			}
			
		});
		dialogBuilder.create().show();
		
	}
	
	private void takePhoto(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (intent.resolveActivity(getPackageManager()) != null) {
	    	intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	    	startActivityForResult(intent, REQUEST_PHOTO);
	    }
	}
	private void deletePhoto(){
		File file = new File(URI.create(uri.toString()));
		file.delete();
		setResult(RESULT_OK);
		finish();
	}
	
	private void loadPhoto(){
		File file = new File(URI.create(uri.toString()));
		ImageView image = (ImageView)findViewById(R.id.imageView1);

		ResizeImageTask task = new ResizeImageTask(image);
		task.execute(file.getAbsolutePath(), "500");	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {	
		if(requestCode == REQUEST_PHOTO && resultCode == RESULT_OK){
			loadPhoto();
		}
	}

}

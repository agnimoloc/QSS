package com.churpi.qualityss.client;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;

import com.churpi.qualityss.client.helper.ImageGalleryAdapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

public class ImageGalleryActivity extends Activity {

	public static final String FILE_URI = "file_path";
	public static final String SHOW_NEW_BUTTON = "show_new";
	
	private static final int REQUEST_PHOTO = 0;
	
	private File file;
	File[] files;
	private ImageGalleryAdapter adapter = null;
	
	private class FileFilterStarts implements FilenameFilter{

		private String startWith;
		public FileFilterStarts(String startWith){
			this.startWith = startWith;
		}
		@Override
		public boolean accept(File dir, String filename) {
			if(filename.startsWith(startWith)){
				return true;
			}
			return false;
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_gallery);
		
		Bundle extras = getIntent().getExtras();
		boolean showButton = extras.getBoolean(SHOW_NEW_BUTTON, false); 
		Uri uri = (Uri)extras.get(FILE_URI);
		ImageButton text = (ImageButton)findViewById(android.R.id.button1);
		if(showButton){
			text.setVisibility(View.VISIBLE);
		}else{
			text.setVisibility(View.GONE);
		}		
		file = new File(URI.create(uri.toString()));
		
		
		if(!initListGallery()){
			takePhoto();
		}		
	}
	
	private boolean initListGallery(){
		File dir = file.getParentFile();
		
		String filename = file.getName().substring(0, file.getName().lastIndexOf('.'));
		FilenameFilter filter = new FileFilterStarts(filename);
		files = dir.listFiles(filter);
		if(files.length > 0){
			if(adapter == null){
				GridView grid = (GridView)this.findViewById(R.id.gridView1);
				grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						Intent intent = new Intent(getApplicationContext(), ShowPhotoActivity.class);
						intent.putExtra(ShowPhotoActivity.FILE_URI, Uri.fromFile(files[position]));
						intent.putExtra(ShowPhotoActivity.SHOW_BUTTONS, true);
						startActivityForResult(intent,REQUEST_PHOTO);
						
					}
				});
				adapter = new ImageGalleryAdapter(this, files);
				grid.setAdapter(adapter);
			}else{
				adapter.updateList(files);
			}
			return true;
		}
		return false;
	}
	
	private void takePhoto(){
		String filename = file.getName().substring(0, file.getName().lastIndexOf('.'));
		String ext =  file.getName().substring(file.getName().lastIndexOf('.'));
		int i = 1;
		File fileTmp ;
		do{			
			fileTmp = new File(
					file.getParentFile(), 
					filename + "_"+ String.valueOf(i++) + ext);
		}while(fileTmp.exists());
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (intent.resolveActivity(getPackageManager()) != null) {
	    	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileTmp));
	    	startActivityForResult(intent, REQUEST_PHOTO);
	    }
	}
	
	public void onClick_takePhoto(View v){
		takePhoto();
	}
	
	public void onClick_close(View v){
		this.finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_PHOTO && resultCode == RESULT_OK){			
			initListGallery();
		}else if(requestCode == REQUEST_PHOTO && resultCode == RESULT_CANCELED){
			if(!initListGallery()){
				this.finish();
			}
		}
	}
}

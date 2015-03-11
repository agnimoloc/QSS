package com.churpi.qualityss.client;

import java.io.File;
import java.net.URI;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ShowPhotoActivity extends Activity {

	public static final String FILE_URI = "file_path";
	public static final String SHOW_NEW_BUTTON = "show_new";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_photo);
		
		Bundle extras = getIntent().getExtras();
		boolean showButton = extras.getBoolean(SHOW_NEW_BUTTON, false); 
		Uri uri = (Uri)extras.get(FILE_URI);
		if(showButton){
			Button text = (Button)findViewById(android.R.id.button2);
			text.setVisibility(View.VISIBLE);
		}
		
		File file = new File(URI.create(uri.toString()));
		ImageView image = (ImageView)findViewById(R.id.imageView1);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		options.inSampleSize = calculateInSampleSize(options, 500, 500);
		options.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

		image.setImageBitmap(bmp);
		
		
	}
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
    }

    return inSampleSize;
}
	
	public void onClick_Ok(View v){
		setResult(RESULT_CANCELED);
		finish();
	}
	
	public void onClick_New(View v){
		
		setResult(RESULT_OK);
		finish();
	}

}

package com.churpi.qualityss.client.helper;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ResizeImageTask extends AsyncTask<String, Void, Bitmap> {

	private final WeakReference<ImageView> imageViewReference;

	public ResizeImageTask(ImageView view){
		imageViewReference = new WeakReference<ImageView>(view);
	}
	@Override
	protected Bitmap doInBackground(String... params) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(params[0], options);
		options.inSampleSize = ImageHelper.calculateInSampleSize(options, Integer.parseInt(params[1]), Integer.parseInt(params[1]));
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(params[0], options);
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
	}

}

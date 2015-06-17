package com.churpi.qualityss.client.helper;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceFile;

public class Alerts {
	public static void showGenericError(Context context){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.ttl_error);
		dialogBuilder.setMessage(context.getString(R.string.msg_generic_error));
		dialogBuilder.create().show();		
	}
	
	public static void showError(Context context, String message){
		showError(context, message, R.string.ttl_error);
	}
	public static void showError(Context context, String message, int titleResource){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(titleResource);
		dialogBuilder.setMessage(message);
		dialogBuilder.create().show();
	}	
	public static void showDocuments(final Context context, int serviceId){
        
        final Cursor c = (Cursor)DbTrans.read(context, String.valueOf(serviceId), new DbTrans.Db(){

			@Override
			public Object onDo(Context context, Object parameter,
					SQLiteDatabase db) {
				
				return db.query(DbServiceFile.TABLE_NAME, null, 
						DbServiceFile.CN_SERVICE + "=?",
						new String[]{(String)parameter},
						null,null,null);
			}
        	
        });
        
        if(!c.moveToFirst()){
        	Toast.makeText(context, R.string.msg_without_documents, Toast.LENGTH_LONG).show();
        	return;
        }
        
    	AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select One Name:-");

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, 
        		android.R.layout.select_dialog_singlechoice, 
        		c, new String[]{DbServiceFile.CN_NAME}, 
        		new int[]{android.R.id.text1}, 
        		CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        
        builderSingle.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(adapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	c.moveToPosition(which);                    	
                    	int fileId = c.getInt(c.getColumnIndex(DbServiceFile._ID));
                    	String filePath = c.getString(c.getColumnIndex(DbServiceFile.CN_URL));
                    	String[] parts = filePath.split("\\.");
                    	c.close();
                    	dialog.dismiss();
                    	                  	
                    	File file = new File(new File(context.getFilesDir(),Constants.DOC_DIR), String.valueOf(fileId) + "." + parts[parts.length-1]);
                    		
                    	Uri uri ;
                    	try {

                    		uri = FileProvider.getUriForFile(
                                context,
                                "com.churpi.qualityss.fileprovider",
                                file);
                    	 } catch (IllegalArgumentException e) {
                             Log.e("Reference file",
                                   "The selected file can't be shared: ");
                             return;
                         }

                    	String type = context.getContentResolver().getType(uri);
                    	Intent intent = new Intent(Intent.ACTION_VIEW);
                    	intent.setDataAndType(uri, type);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent);                      	
                    }
                });
        builderSingle.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if(c != null && !c.isClosed()){
					c.close();
				}
				
			}
		});
        builderSingle.show();
	}
}

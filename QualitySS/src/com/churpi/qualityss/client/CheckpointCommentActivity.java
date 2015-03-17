package com.churpi.qualityss.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CheckpointCommentActivity extends Activity {

	public static final String FLD_COMMENT = "comment";
	public static final String FLD_TEXT = "text";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkpoint_comment);
		
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(FLD_COMMENT)){
			TextView text = (TextView)findViewById(android.R.id.text2);		
			text.setText(extras.getString(FLD_COMMENT));	
		}
		if(extras.containsKey(FLD_TEXT)){
			TextView text = (TextView)findViewById(android.R.id.text1);		
			text.setText(extras.getString(FLD_TEXT));	
		}		
	}
	
	public void onClick_Cancel(View v){
		this.setResult(RESULT_CANCELED);
		finish();
	}
	
	public void onClick_Ok(View v){
		TextView text = (TextView)findViewById(android.R.id.text2);		
		Intent intent = new Intent();
		intent.putExtra(FLD_COMMENT, text.getText().toString());
		Activity activity =getParent(); 
		if(activity == null){
			setResult(RESULT_OK, intent);	
		}else{
			activity.setResult(RESULT_OK, intent);
		}
		finish();
	}
}

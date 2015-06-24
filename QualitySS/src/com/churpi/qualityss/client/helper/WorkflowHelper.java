package com.churpi.qualityss.client.helper;

import java.io.File;

import com.churpi.qualityss.client.*;
import com.churpi.qualityss.client.helper.workflow.ActivityProcess;
import com.churpi.qualityss.client.helper.workflow.TransitionButton;
import com.churpi.qualityss.service.PullPushDataService;
import com.churpi.qualityss.service.UploadImageService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.SparseArray;

public class WorkflowHelper {
		
	private static final SparseArray<SparseArray<ActivityProcess>> workflows = new SparseArray<SparseArray<ActivityProcess>>();
		
	private static void initiateWorkflows(){
		
		GenericWorkflow();
		
		RequisitionWorkflow();
		
		WarningWorkflow();
		
		ServiceHistoryWorkflow();
	}
	private static void RequisitionWorkflow(){
		SparseArray<ActivityProcess> workflow = new SparseArray<ActivityProcess>();		
		workflows.put(-1, workflow);
		workflow.put(MainMenuActivity.class.getName().hashCode(),
				new ActivityProcess(MainMenuActivity.class, 
						new TransitionButton(R.id.gridView1, RequisitionListActivity.class)
				)
		);
	}
	
	private static void WarningWorkflow(){
		SparseArray<ActivityProcess> workflow = new SparseArray<ActivityProcess>();		
		workflows.put(-2, workflow);
		workflow.put(MainMenuActivity.class.getName().hashCode(),
				new ActivityProcess(MainMenuActivity.class, 
						new TransitionButton(R.id.gridView1, StaffReviewListActivity.class)
				)
		);
		workflow.put(StaffReviewListActivity.class.getName().hashCode(),
				new ActivityProcess(
						StaffReviewListActivity.class, 
						new TransitionButton(R.id.gridView1, WarningListActivity.class)
				)
		);
	}
	
	private static void ServiceHistoryWorkflow(){
		SparseArray<ActivityProcess> workflow = new SparseArray<ActivityProcess>();		
		workflows.put(-3, workflow);
		workflow.put(MainMenuActivity.class.getName().hashCode(),
				new ActivityProcess(MainMenuActivity.class, 
						new TransitionButton(R.id.gridView1, ServiceHistoryActivity.class)
				)
		);
		workflow.put(ServiceHistoryActivity.class.getName().hashCode(),
				new ActivityProcess(
						ServiceHistoryActivity.class, 
						new TransitionButton(R.id.listView1, SummaryActivity.class)
				)
		);
	}
	
	private static void GenericWorkflow(){
		SparseArray<ActivityProcess> workflow = new SparseArray<ActivityProcess>();		
		workflows.put(0, workflow);
		
		workflow.put(MainMenuActivity.class.getName().hashCode(),
				new ActivityProcess(MainMenuActivity.class, 
						new TransitionButton(R.id.gridView1, SectorListActivity.class)
				)
		);
		
		workflow.put(SectorListActivity.class.getName().hashCode(),
				new ActivityProcess(SectorListActivity.class, 
						new TransitionButton(R.id.gridView1, ServiceDetailActivity.class),
						new TransitionButton(R.id.sector_list, SectorDetailActivity.class)
				)
		);

		workflow.put(SectorDetailActivity.class.getName().hashCode(),
				new ActivityProcess(SectorDetailActivity.class, 
						new TransitionButton(R.id.gridView1, ServiceDetailActivity.class)
				)
		);

		workflow.put(ServiceDetailActivity.class.getName().hashCode(),
				new ActivityProcess(ServiceDetailActivity.class, 
						new TransitionButton(R.id.button4, StaffInventoryActivity.class)
				)
		);
		
		workflow.put(StaffInventoryActivity.class.getName().hashCode(),
				new ActivityProcess(
						StaffInventoryActivity.class, 
						new TransitionButton(android.R.id.button1, StaffReviewActivity.class)
				)
		);

		workflow.put(StaffReviewListActivity.class.getName().hashCode(),
				new ActivityProcess(
						StaffReviewListActivity.class, 
						new TransitionButton(R.id.gridView1, StaffInventoryActivity.class),
						new TransitionButton(android.R.id.button1, SummaryActivity.class)
				)
		);

		workflow.put(StaffReviewActivity.class.getName().hashCode(),
				new ActivityProcess(
						StaffReviewActivity.class, 
						new TransitionButton(android.R.id.button3, NotificationsActivity.class),
						new TransitionButton(android.R.id.button2, NotificationsActivity.class),
						new TransitionButton(android.R.id.button1, SurveyActivity.class)
				)
		);
		workflow.put(SummaryActivity.class.getName().hashCode(),
				new ActivityProcess(
						SummaryActivity.class, 
						new TransitionButton(android.R.id.button1, true, StaffReviewListActivity.class),
						new TransitionButton(android.R.id.button2, true, SectorListActivity.class)
				)
		);

		workflow.put(NotificationsActivity.class.getName().hashCode(),
				new ActivityProcess(
						NotificationsActivity.class, 
						new TransitionButton(android.R.id.button1, SummaryActivity.class),
						new TransitionButton(android.R.id.button2, StaffReviewListActivity.class)
				)
		);

	}
	
	public static Intent process(Activity currentActivity, int transitionButton){
		return process(currentActivity, transitionButton, null);
	}
	
	public static Intent process(Activity currentActivity, int transitionButton, String action){
		
		if(workflows.size() == 0){
			initiateWorkflows();
		}
		Context context = currentActivity.getApplicationContext();
		
		int typeid = Ses.getInstance(context).getActivityType();
		
		if(workflows.indexOfKey(typeid) < 0){
			typeid = 0;
		}
		
		SparseArray<ActivityProcess> workflow = workflows.get(typeid);
		ActivityProcess actPro = workflow.get(currentActivity.getClass().getName().hashCode());
		TransitionButton button = actPro.getTransitionButton(transitionButton);
		
		Intent intent = new Intent(context, button.getNextActivity());
		if(button.isClose()){
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		if(action != null){
			intent.setAction(action);
		}
		return intent;
	}

	public static void getComments(Activity activity, String comment, String label, int requestCode){
		Intent intent = new Intent(activity.getApplicationContext(), CheckpointCommentActivity.class);
		intent.putExtra(CheckpointCommentActivity.FLD_COMMENT, comment);
		intent.putExtra(CheckpointCommentActivity.FLD_TEXT, label);
		activity.startActivityForResult(intent, requestCode);
	}
	
	public static void takePhoto(Activity activity, File dest, int requestCode){
		/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (intent.resolveActivity(activity.getPackageManager()) != null) {
	    	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(dest));
	    	activity.startActivityForResult(intent, requestCode);
	    }*/
		Intent intent = new Intent(activity, ImageGalleryActivity.class);
		intent.putExtra(ImageGalleryActivity.FILE_URI, Uri.fromFile(dest));
		intent.putExtra(ImageGalleryActivity.SHOW_NEW_BUTTON, true);
		activity.startActivityForResult(intent,requestCode);

	}
	/*public static void showPhoto(Activity activity, File dest, int requestCode){
		Intent intent = new Intent(activity, ShowPhotoActivity.class);
		intent.putExtra(ShowPhotoActivity.FILE_URI, Uri.fromFile(dest));
		intent.putExtra(ShowPhotoActivity.SHOW_NEW_BUTTON, true);
		activity.startActivityForResult(intent,requestCode);

	}*/
	
	public static void uploadImages(Context context){
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		boolean isrunning = false;
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (UploadImageService.class.getName().equals(service.service.getClassName())) {
	            isrunning = true;
	            break;
	        }
	    }
	    if(!isrunning){
	    	Intent getdata = new Intent(context,UploadImageService.class);
	    	context.startService(getdata);
	    }
	}
	
	public static void pullPushData(Context context){
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		boolean isrunning = false;
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (PullPushDataService.class.getName().equals(service.service.getClassName())) {
	            isrunning = true;
	            break;
	        }
	    }
	    if(!isrunning){
	    	Intent getdata = new Intent(context, PullPushDataService.class);
	    	context.startService(getdata);
	    }
	}
	
	public static void getRequisition(Activity activity, String action){
		Intent intent = new Intent(activity.getApplicationContext(), RequisitionListActivity.class);
		intent.setAction(action);
		activity.startActivity(intent);
	}
	public static void getWarning(Activity activity, String action){
		Intent intent = new Intent(activity.getApplicationContext(), WarningListActivity.class);
		intent.setAction(action);
		activity.startActivity(intent);
	}
}

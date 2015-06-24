package com.churpi.qualityss.client;

import java.io.StringWriter;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbWarning;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbWarningDetail;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbWarningReason;
import com.churpi.qualityss.client.dto.WarningDTO;
import com.churpi.qualityss.client.dto.WarningDetailDTO;
import com.churpi.qualityss.client.helper.Alerts;
import com.churpi.qualityss.client.helper.DateHelper;
import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.WarningReasonListAdapter;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A fragment representing a single Warning detail screen. This fragment is
 * either contained in a {@link WarningListActivity} in two-pane mode (on
 * tablets) or a {@link WarningDetailActivity} on handsets.
 */
public class WarningDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	public static final String ARG_ACTION = "action";

	private WarningDTO warning = null;
	private View rootView;
	private String mAction;
	//Cursor c;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public WarningDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getArguments().containsKey(ARG_ACTION)){
			mAction = getArguments().getString(ARG_ACTION);
		}
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			
			if(savedInstanceState != null && savedInstanceState.containsKey("listWarnings")){
				warning = (WarningDTO) savedInstanceState.getSerializable("listWarnings");
			}else{				
				warning = new WarningDTO();
				warning.setFechaCreacion(DateHelper.getCurrentTime());
				warning.setCreadorId(Ses.getInstance(getActivity()).getEmployee());
				warning.setElementoId(Ses.getInstance(getActivity()).getEmployeeId());
				warning.setId(Integer.parseInt(getArguments().getString(ARG_ITEM_ID)));
				DbTrans.read(getActivity(), new DbTrans.Db() {

					@Override
					public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
						String[] args = new String[]{String.valueOf(String.valueOf(warning.getId()))};
						Cursor c = db.query(DbWarning.TABLE_NAME, null, 
								DbWarning._ID+"=?", args, 
								null, null, null);

						if(c.moveToFirst()){
							warning.fillFromCursor(c);
						}		
						c.close();

						c = db.rawQuery(DbQuery.GET_WARNING_REASON_LIST, args);
						if(c.moveToFirst()){
							do{
								WarningDetailDTO warningDetail = new WarningDetailDTO();
								warningDetail.setWarningId(warning.getId());
								warningDetail.setWarningReasonId(c.getInt(c.getColumnIndex(DbWarningReason._ID)));
								warningDetail.setWarningReasonDesc(c.getString(c.getColumnIndex(DbWarningReason.CN_DESCRIPTION)));
								warningDetail.setDescripcion(c.getString(c.getColumnIndex(DbWarningDetail.CN_NOTE)));
								warningDetail.setId(c.getInt(c.getColumnIndex(DbWarningDetail.CN_WARNING_REASON)));
								warning.getDetails().add(warningDetail);
							}while(c.moveToNext());
						}
						c.close();
						return null;
					}
				});
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_warning_detail,
				container, false);

		Button createWarning = (Button) rootView.findViewById(R.id.button2);

		if (warning != null) {
			TextView elementText = (TextView)rootView.findViewById(android.R.id.text1);
			elementText.setText(Ses.getInstance(getActivity()).getEmployeeName());
			
			ListView list = (ListView) rootView.findViewById(R.id.listView1);
			list.setAdapter(new WarningReasonListAdapter(
					getActivity(),warning.getDetails()));	
			if(warning.getId() > 0){
				elementText.setEnabled(false);
				
				createWarning.setVisibility(View.GONE);
				EditText text = (EditText)rootView.findViewById(R.id.editText1);
				text.setEnabled(false);
				text.setText(warning.getObservaciones());
				text = (EditText)rootView.findViewById(R.id.editText2);
				text.setEnabled(false);				
				for(WarningDetailDTO detail : warning.getDetails()){
					if(detail.getWarningReasonId() == 7){
						text.setText(detail.getDescripcion());
						break;
					}
				}
			}

		}
		Button backButton = (Button) rootView.findViewById(R.id.button1);
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				close();
			}
		});
		
		createWarning.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createWarning();				
			}
		});
		
		return rootView;
	}	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		outState.putSerializable("listWarnings", warning);
		
		super.onSaveInstanceState(outState);
	}
	
	private void close(){
		if (getActivity() instanceof WarningDetailActivity){
			getActivity().setResult(Activity.RESULT_OK);
		    getActivity().finish();
		    
		}else{ 
			FragmentManager manager = getActivity().getFragmentManager();
		    manager.beginTransaction()
		    	.remove(WarningDetailFragment.this)
		    	.commit();
		    WarningListActivity.refreshList(manager);		    
		}
	}
	
	private void createWarning(){
		
		StringWriter msgError = new StringWriter();
		EditText text = (EditText)rootView.findViewById(R.id.editText2);
		String others = text.getText().toString().trim();
		boolean atLeastOne = false;
		
		for(WarningDetailDTO detail : warning.getDetails()){
			if(detail.getId() != 0){
				atLeastOne = true;
				if(detail.getWarningReasonId() == 7){
					if(others.length() == 0){
						msgError.write(getString(R.string.msg_warning_other_desc_required) + "\n");
					}else{
						detail.setDescripcion(others);
					}
				}
			}
		}
		if(!atLeastOne){
			msgError.write(getString(R.string.msg_warning_at_least_one_warning));
		}
		
		if(msgError.toString().length() > 0){
			Alerts.showError(getActivity(), msgError.toString());
			return;
		}		
		text = (EditText)rootView.findViewById(R.id.editText1);
		warning.setObservaciones(text.getText().toString());		
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		dialogBuilder.setTitle(R.string.title_warning_dialog_create_title);
		
		StringBuilder sb = new StringBuilder();
		for(WarningDetailDTO detail : warning.getDetails()){
			if(detail.getId() != 0){
				if(sb.length() > 0){
					sb.append(", ");
				}
				sb.append("\n");
				sb.append(detail.getWarningReasonDesc());
				if(detail.getWarningReasonId() == 7){
					sb.append("(");
					sb.append(detail.getDescripcion());
					sb.append(")");
				}
			}
		}
		
		dialogBuilder.setMessage(getString(R.string.msg_confirm_warning,Ses.getInstance(getActivity()).getEmployeeName(), sb.toString()));
		dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				DbTrans.write(getActivity(), new DbTrans.Db() {
					@Override
					public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
						ContentValues values = new ContentValues();
						values.put(DbWarning.CN_CREATION_DATE, warning.getFechaCreacion());
						values.put(DbWarning.CN_CREATOR, warning.getCreadorId());						
						values.put(DbWarning.CN_EMPLOYEE, warning.getElementoId());
						values.put(DbWarning.CN_NOTE, warning.getObservaciones());
						if(mAction != null && (mAction.compareTo(Constants.ACTION_EMPLOYEE)==0 || mAction.compareTo(Constants.ACTION_SERVICE)==0)){
							values.put(DbWarning.CN_SERVICE, Ses.getInstance(getActivity()).getServiceId());
						}
						values.put(DbWarning.CN_SENT, 0);
						boolean insert = true;
						String[] whereArgs = new String[]{ String.valueOf(warning.getId())}; 
						if(warning.getId()>0){
							insert = db.update(DbWarning.TABLE_NAME, values, 
									DbWarning._ID + "=?", whereArgs) < 1;
						}
						if(insert){
							warning.setId((int)db.insert(DbWarning.TABLE_NAME, null, values));
						}						
						for(WarningDetailDTO detail : warning.getDetails()){
							whereArgs = new String[]{
									String.valueOf(warning.getId()), 
									String.valueOf(detail.getWarningReasonId())};
							String whereClause = DbWarningDetail.CN_WARNING + "=? AND " +
									DbWarningDetail.CN_WARNING_REASON +"=?";
							if(detail.getId() == 0){
								db.delete(DbWarningDetail.TABLE_NAME, 
										whereClause, whereArgs);
							}else{
								values = new ContentValues();
								if(detail.getWarningReasonId() == 7){
									values.put(DbWarningDetail.CN_NOTE, detail.getDescripcion());
								}
								values.put(DbWarningDetail.CN_WARNING, warning.getId());						
								values.put(DbWarningDetail.CN_WARNING_REASON, detail.getWarningReasonId());
								insert = db.update(DbWarningDetail.TABLE_NAME, values, whereClause, whereArgs) < 1;
								if(insert){
									detail.setId((int)db.insert(DbWarningDetail.TABLE_NAME, null, values));
								}
							}
						}
						return null;
					}
				});
				if(mAction == null){
					WorkflowHelper.pullPushData(getActivity().getApplicationContext());
				}
				Toast.makeText(getActivity(), R.string.msg_warning_created, Toast.LENGTH_LONG).show();
				close();
			}
		});
		dialogBuilder.create().show();	
		
	}
}

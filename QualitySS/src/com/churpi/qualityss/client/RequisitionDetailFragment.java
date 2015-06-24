package com.churpi.qualityss.client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbRequisition;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.dto.RequisitionDTO;
import com.churpi.qualityss.client.helper.Alerts;
import com.churpi.qualityss.client.helper.DateHelper;
import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A fragment representing a single Requisition detail screen. This fragment is
 * either contained in a {@link RequisitionListActivity} in two-pane mode (on
 * tablets) or a {@link RequisitionDetailActivity} on handsets.
 */
public class RequisitionDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	public static final String ARG_ACTION = "action";
	
	private int id = 0;
	private List<String> hremployeeNames;
	private List<Integer> hremployeeIds;
	private TextView viewDateSelect;
	/*private Spinner spinService;
	private Spinner spinEmployee;*/
	private RequisitionDTO requisition;
	private String action = null;

	private SimpleCursorAdapter employeeAdapter;
		
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RequisitionDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.			
			id = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
		}
		if (getArguments().containsKey(ARG_ACTION)) {
			action = getArguments().getString(ARG_ACTION);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_requisition_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		/*if (mItem != null) {
			((TextView) rootView.findViewById(R.id.requisition_detail))
					.setText(mItem.content);
		}*/
		
		hremployeeNames = new ArrayList<String>();
		hremployeeNames.add("");
		hremployeeIds = new ArrayList<Integer>();
		hremployeeIds.add(0);
		
		Cursor sCur = (Cursor)DbTrans.read(getActivity(), new DbTrans.Db() {
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {

				Cursor c = db.rawQuery(DbQuery.GET_HR_EMPLOYEES, null);
				if(c.moveToFirst()){
					do{
						hremployeeNames.add(c.getString(c.getColumnIndex(DbEmployee.CN_NAME)));
						hremployeeIds.add(c.getInt(c.getColumnIndex(DbEmployee._ID)));
					}while(c.moveToNext());
				}
				c.close();
				
				return  db.query(DbService.TABLE_NAME, 
						new String[]{DbService._ID, DbService.CN_DESCRIPTION}, 
						null, null, null, null, DbService.CN_DESCRIPTION + " ASC");
			}
		});
		
		Button button = (Button)rootView.findViewById(android.R.id.button1);
		button.setOnClickListener(onClick_Create);
		
		Spinner spinAssigned = (Spinner)rootView.findViewById(R.id.spinAssigned);
		spinAssigned.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, hremployeeNames));
		
		Spinner spinStatus = (Spinner)rootView.findViewById(R.id.spinStatus);
		spinStatus.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.requisition_status, android.R.layout.simple_spinner_dropdown_item));

		TextView spinStartDate = (TextView)rootView.findViewById(R.id.spinStartDate);
		spinStartDate.setText(DateHelper.getCurrentTimeFormatted());
		
		//spinStartDate.setOnClickListener(onClick_SelectDate);

		TextView spinEndDate = (TextView)rootView.findViewById(R.id.spinEndDate);
		//spinEndDate.setOnClickListener(onClick_SelectDate);
		
		Spinner spinService = (Spinner)rootView.findViewById(R.id.spinService);
		
		spinService.setAdapter(new SimpleCursorAdapter(
				getActivity(), 
				android.R.layout.simple_spinner_dropdown_item, 
				sCur, 
				new String[]{DbService.CN_DESCRIPTION}, 
				new int[]{ android.R.id.text1 }, 
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
		
		
		
		requisition = new RequisitionDTO();
		
		if(id != 0){
			
			DbTrans.read(getActivity(), new DbTrans.Db() {
				@Override
				public Object onDo(Context context, Object parameter, SQLiteDatabase db) {

					Cursor c = db.query(
							DbRequisition.TABLE_NAME, null, 
							DbRequisition._ID + "=?", 
							new String[]{String.valueOf(id)}, null, null, null);
					if(c.moveToFirst()){
						requisition.fillFromCursor(c);
					}
					c.close();
					return null;
				}
			});
			
			
			
			spinAssigned.setEnabled(false);
			int employeePos = hremployeeIds.indexOf(requisition.getResponsableId());
			spinAssigned.setSelection(employeePos);
			
			spinStatus.setEnabled(false);
			spinStatus.setSelection(Integer.parseInt(requisition.getStatus()));
			
			spinStartDate.setEnabled(false);
			spinStartDate.setText(DateHelper.toFormattedString(requisition.getFechaInicio()));
			
			spinEndDate.setEnabled(false);
			spinEndDate.setText(DateHelper.toFormattedString(requisition.getFechaTerminacion()));
			
			EditText editAgreement = (EditText)rootView.findViewById(R.id.editAgreement);
			editAgreement.setEnabled(false);
			editAgreement.setText(requisition.getAcuerdo_Compromiso());
					
			EditText editProgress = (EditText)rootView.findViewById(R.id.editProgress);
			editProgress.setEnabled(false);
			editProgress.setText(requisition.getAvance());
			
			button.setVisibility(View.GONE);			
		}
		if(action != null && 
				(action.compareTo(Constants.ACTION_SERVICE)==0 || 
				action.compareTo(Constants.ACTION_EMPLOYEE)==0) &&
				requisition.getServicioId() == 0){
			requisition.setServicioId(Ses.getInstance(getActivity()).getServiceId());
		}
		
		spinService.setEnabled(!(action != null && 
				(action.compareTo(Constants.ACTION_SERVICE)==0 || 
				action.compareTo(Constants.ACTION_EMPLOYEE)==0)));
		
		
		final Spinner spinEmployee = (Spinner)rootView.findViewById(R.id.spinEmployee);
		if(requisition.getServicioId()!=0){
			Cursor cTemp = ((SimpleCursorAdapter)spinService.getAdapter()).getCursor();
			int index = 0;
			if(cTemp.moveToFirst()){
				int idxId = cTemp.getColumnIndex(DbService._ID);
				do{
					int id = cTemp.getInt(idxId);
					if(id ==  requisition.getServicioId()){
						break;
					}
					index++;
				}while(cTemp.moveToNext());
			}
			spinService.setSelection(index);			
			initEmployeeAdapter(requisition.getServicioId());
			spinEmployee.setTag(requisition.getServicioId());
		}else{
			initEmployeeAdapter((int)spinService.getSelectedItemId());
			spinEmployee.setTag((int)spinService.getSelectedItemId());
		}
		
		if(action != null && 
				action.compareTo(Constants.ACTION_EMPLOYEE)==0 &&
				requisition.getElementoId() == 0){
			requisition.setElementoId(Ses.getInstance(getActivity()).getEmployeeId());
		}
		spinEmployee.setEnabled(!(action != null && 
				action.compareTo(Constants.ACTION_EMPLOYEE)==0));

		if(id != 0){
			spinService.setEnabled(false);
			spinEmployee.setEnabled(false);
		}
		
		spinEmployee.setAdapter(employeeAdapter);
		if(requisition.getElementoId() != 0){
			Cursor cTemp = employeeAdapter.getCursor();
			int index = 0;
			if(cTemp.moveToFirst()){
				int idxId = cTemp.getColumnIndex(DbEmployee._ID);
				do{
					int id = cTemp.getInt(idxId);
					if(id ==  requisition.getElementoId()){
						break;
					}
					index++;
				}while(cTemp.moveToNext());
			}
			spinEmployee.setSelection(index);	
		}
		
		spinService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(id != (Integer)spinEmployee.getTag()){
					spinEmployee.setSelection(0);
					spinEmployee.setTag(id);
					initEmployeeAdapter((int)id);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});

		return rootView;
	}
	
	private void initEmployeeAdapter(int serviceId){
		Cursor c = (Cursor) DbTrans.read(getActivity(), serviceId, new DbTrans.Db() {
			
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				
				String serviceId = String.valueOf((Integer)parameter);
				
				
				return db.rawQuery(DbQuery.GET_EMPLOYEE_BY_SERVICE_PICKER,
						new String[]{serviceId});
			}
		});
		if(employeeAdapter == null){
			employeeAdapter = new SimpleCursorAdapter(
					getActivity(), 
					android.R.layout.simple_spinner_dropdown_item, 
					c, 
					new String[]{DbEmployee.CN_NAME}, 
					new int[]{ android.R.id.text1 }, 
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		}else{
			Cursor cOld = employeeAdapter.swapCursor(c);
			cOld.close();
		}		
	}
	
	OnClickListener onClick_SelectDate = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Calendar c = Calendar.getInstance();
			DatePickerDialog dialog = new DatePickerDialog(getActivity(), datePickerListener, 
	                c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
			dialog.show();
			viewDateSelect = (TextView)v;			
		}
	};
	
	
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			String date = DateHelper.fromYearMonthDay(year,monthOfYear, dayOfMonth);
			viewDateSelect.setText(date);
		}

	};
	
	
	OnClickListener onClick_Create = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Spinner spinAssigned = (Spinner)v.getRootView().findViewById(R.id.spinAssigned);
			requisition.setResponsableId(hremployeeIds.get(spinAssigned.getSelectedItemPosition()));

			Spinner spinStatus = (Spinner)v.getRootView().findViewById(R.id.spinStatus);
			requisition.setStatus(String.valueOf(spinStatus.getSelectedItemPosition()));

			TextView spinStartDate = (TextView)v.getRootView().findViewById(R.id.spinStartDate);
			requisition.setFechaInicio(DateHelper.fromFormattedString(spinStartDate.getText().toString()));

			TextView spinEndDate = (TextView)v.getRootView().findViewById(R.id.spinEndDate);
			requisition.setFechaTerminacion(DateHelper.fromFormattedString(spinEndDate.getText().toString()));

			EditText editAgreement = (EditText)v.getRootView().findViewById(R.id.editAgreement);
			requisition.setAcuerdo_Compromiso(editAgreement.getText().toString());

			EditText editProgress = (EditText)v.getRootView().findViewById(R.id.editProgress);
			requisition.setAvance(editProgress.getText().toString());

			Spinner spinService = (Spinner)v.getRootView().findViewById(R.id.spinService);
			requisition.setServicioId((int)spinService.getSelectedItemId());
			
			Spinner spinEmployee = (Spinner)v.getRootView().findViewById(R.id.spinEmployee);
			requisition.setElementoId((int)spinEmployee.getSelectedItemId());
			
			if(requisition.getAcuerdo_Compromiso() == null || 
					requisition.getResponsableId() == 0 || 
					requisition.getFechaInicio() == null || 
					requisition.getFechaInicio().trim().length() == 0 ||
					requisition.getAcuerdo_Compromiso().trim().length() == 0){
				Alerts.showError(getActivity(), getString(R.string.msg_fault_requisition_data));
				return;
			}
			
			if(action != null && action.compareTo(Constants.ACTION_SERVICE)==0){
				requisition.setServicioId(Ses.getInstance(getActivity()).getServiceId());
				
			}else if(action != null && action.compareTo(Constants.ACTION_EMPLOYEE)==0){
				requisition.setServicioId(Ses.getInstance(getActivity()).getServiceId());
				requisition.setElementoId(Ses.getInstance(getActivity()).getEmployeeId());
			}

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
			dialogBuilder.setTitle(R.string.ttl_confirm_create_requisition);
			dialogBuilder.setMessage( getString(R.string.msg_confirm_requisition,hremployeeNames.get(spinAssigned.getSelectedItemPosition()),requisition.getAcuerdo_Compromiso()));
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
							values.put(DbRequisition.CN_AGREEMENT, requisition.getAcuerdo_Compromiso());
							values.put(DbRequisition.CN_ASSIGN_EMPLOYEE, requisition.getResponsableId());
							values.put(DbRequisition.CN_END_DATE, requisition.getFechaTerminacion());
							if(requisition.getElementoId()> 0){
								values.put(DbRequisition.CN_EMPLOYEE, requisition.getElementoId());
							}
							values.put(DbRequisition.CN_PROGRESS, requisition.getAvance());
							if(requisition.getServicioId()> 0){
								values.put(DbRequisition.CN_SERVICE, requisition.getServicioId());
							}
							values.put(DbRequisition.CN_START_DATE, requisition.getFechaInicio());
							values.put(DbRequisition.CN_STATUS, requisition.getStatus());
							values.put(DbRequisition.CN_SENT, requisition.getSent());
							values.put(DbRequisition.CN_CREATION_DATE, DateHelper.getCurrentTime());
							values.put(DbRequisition.CN_CREATOR, Ses.getInstance(getActivity()).getEmployee());
							db.insert(DbRequisition.TABLE_NAME, null, values);							
							return null;
						}
					});
					
					if(action == null){
						WorkflowHelper.pullPushData(getActivity().getApplicationContext());
					}
					
					//startActivity(WorkflowHelper.process(SummaryActivity.this,android.R.id.button2));			
					
					dialog.dismiss();
					if (getActivity() instanceof RequisitionDetailActivity){
						getActivity().setResult(Activity.RESULT_OK);
					    getActivity().finish();
					    
					}else{ 
					    getActivity().getFragmentManager().beginTransaction().remove(RequisitionDetailFragment.this).commit();
					    RequisitionListFragment fragment = ((RequisitionListFragment) getFragmentManager().findFragmentById(
								R.id.requisition_list));						
						fragment.initCursor();
					}
				}
			});
			dialogBuilder.create().show();		
		}
	};


	/*OnItemSelectedListener selectAssigned = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};*/
	

}

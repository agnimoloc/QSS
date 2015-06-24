package com.churpi.qualityss.client;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbRequisition;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.helper.Ses;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * A list fragment representing a list of Requisitions. This fragment also
 * supports tablet devices by allowing list items to be given an 'activated'
 * state upon selection. This helps indicate which item is currently being
 * viewed in a {@link RequisitionDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class RequisitionListFragment extends ListFragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RequisitionListFragment() {
	}

	Cursor c = null;
	SimpleCursorAdapter adapter = null;
	String mAction;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAction = getActivity().getIntent().getAction();
		
		initCursor();
		String[] from = new String[]{
			DbRequisition.CN_AGREEMENT,
			DbEmployee.CN_NAME,
			DbRequisition.CN_START_DATE,
			DbService.CN_DESCRIPTION,
			DbRequisition.CN_EMPLOYEE
		};
		int[] to = new int[]{ 
			android.R.id.text1,
			android.R.id.text2,
			android.R.id.custom,
			R.id.textView1,
			R.id.textView2
		};
		adapter = new SimpleCursorAdapter(
				getActivity(), 
				R.layout.item_requisition, c, 
				from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		setListAdapter(adapter);
	}
	
	public void initCursor(){
		
		c = (Cursor)DbTrans.read(getActivity(), new DbTrans.Db(){
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				int serviceId = 0;
				int employeeId = 0;
				if(mAction != null && 
						(mAction.compareTo(Constants.ACTION_SERVICE)==0 ||
						mAction.compareTo(Constants.ACTION_EMPLOYEE)==0)){
					serviceId = Ses.getInstance(context).getServiceId();
					if(mAction.compareTo(Constants.ACTION_EMPLOYEE)==0){
						employeeId= Ses.getInstance(context).getEmployeeId();
					}
				}
				return db.rawQuery(DbQuery.GET_REQUISITION_LIST, 
						new String[]{
							String.valueOf(serviceId),
							String.valueOf(employeeId)
						});				
			}
		});
		if(adapter != null){
			Cursor oldCursor = adapter.swapCursor(c);
			oldCursor.close();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(String.valueOf(id));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
}

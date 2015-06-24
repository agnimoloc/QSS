package com.churpi.qualityss.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.churpi.qualityss.client.db.DbQuery;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbWarning;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbWarningDetail;
import com.churpi.qualityss.client.helper.DateHelper;
import com.churpi.qualityss.client.helper.Ses;

/**
 * A list fragment representing a list of Warnings. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link WarningDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class WarningListFragment extends ListFragment {

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
	public WarningListFragment() {
	}

	
	//private SimpleAdapter adapter;
	private List<Map<String, String>> data;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		initCursor();
		
		setAdapter();		
	}
	
	private void setAdapter(){
		
		String[] from = new String[]{
				DbWarningDetail.CN_WARNING_REASON,
				DbWarning.CN_CREATION_DATE,
				DbWarning.CN_NOTE
		};
		int[] to = new int[]{ 
				android.R.id.text1,
				android.R.id.text2,
				android.R.id.custom
		};
		//adapter = new SimpleAdapter(getActivity(), data, R.layout.item_warning, from, to);
		setListAdapter(new SimpleAdapter(getActivity(), data, R.layout.item_warning, from, to));
	}
	
	public void initCursor(){
		data = new ArrayList<Map<String,String>>();
		DbTrans.read(getActivity(), new DbTrans.Db(){
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				Cursor c = db.rawQuery(DbQuery.GET_WARNING_LIST, 
						new String[]{String.valueOf(Ses.getInstance(context).getEmployeeId())});
				if(c.moveToFirst()){
					Map<String, String> item = new HashMap<String, String>();;
					int id = 0;
					StringBuilder sbReason = new StringBuilder();					
					do{
						
						int currentId = c.getInt(c.getColumnIndex(DbWarning._ID));
						if(id != currentId){
							item.put(DbWarningDetail.CN_WARNING_REASON, sbReason.toString());
							sbReason = new StringBuilder();
							item = new HashMap<String, String>();
							item.put(DbWarning._ID, c.getString(c.getColumnIndex(DbWarning._ID)));
							item.put(DbWarning.CN_CREATION_DATE, DateHelper.toFormattedString(c.getString(c.getColumnIndex(DbWarning.CN_CREATION_DATE))));
							item.put(DbWarning.CN_NOTE, c.getString(c.getColumnIndex(DbWarningDetail.CN_NOTE)));
							data.add(item);
							id = currentId;
						}
						if(sbReason.length() != 0){
							sbReason.append(", ");
						}
						sbReason.append(c.getString(c.getColumnIndex(DbWarningDetail.CN_WARNING_REASON)));
					}while(c.moveToNext());	
					item.put(DbWarningDetail.CN_WARNING_REASON, sbReason.toString());
				}
				c.close();
				return null;				
			}
		});
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
		mCallbacks.onItemSelected(data.get(position).get(DbWarning._ID));
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

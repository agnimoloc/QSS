package com.churpi.qualityss.client;

import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.helper.ServiceListAdapter;

import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.GridView;

/**
 * A fragment representing a single Sector detail screen. This fragment is
 * either contained in a {@link SectorListActivity} in two-pane mode (on
 * tablets) or a {@link SectorDetailActivity} on handsets.
 */
public class SectorDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "sector_id";
	
	private final int REQUEST_SERVICEDETAIL = 0;

	Cursor c = null;
	ServiceListAdapter adapter = null;
	
	int sectorId;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SectorDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			sectorId = getArguments().getInt(ARG_ITEM_ID);
			c = initCursor();
		}
	}
	
	private Cursor initCursor(){
		return (Cursor)DbTrans.read(getActivity(), new DbTrans.Db() {
			@Override
			public Object onDo(Context context, SQLiteDatabase db) {				
				return db.query(DbService.TABLE_NAME, 
						new String[]{DbService._ID, DbService.CN_STATUS, DbService.CN_CODE}, 
						DbService.CN_SECTOR + "=?", new String[]{String.valueOf(sectorId)},
						null, null, null);
			}
		});		
	}
	
	@Override
	public void onDestroy() {
		if(c != null){
			c.close();
		}
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sector_detail,
				container, false);

		GridView grid = (GridView)rootView.findViewById(R.id.gridView1);
		String[] from = new String[]{DbService._ID};
		int[] to = new int[]{ android.R.id.text1};
		adapter = new ServiceListAdapter(
				getActivity(), 
				R.layout.item_service, c, 
				from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		grid.setAdapter(adapter);		

		grid.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				c.moveToPosition(position);
				int serviceId = c.getInt(c.getColumnIndex(DbService._ID));
				
				//Alerts.showServiceDetail(getActivity(), serviceId);
				openServiceDetail(serviceId);
			}
			
		});
		
		return rootView;
	}
	
	private void openServiceDetail(int serviceId){
		Intent intent = new Intent(getActivity(), ServiceDetailActivity.class);
		intent.putExtra(ServiceDetailActivity.SERVICE_ID, serviceId);
		startActivityForResult(intent, REQUEST_SERVICEDETAIL);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_SERVICEDETAIL){
			c = initCursor();
			Cursor oldCur = adapter.swapCursor(c);
			oldCur.close();
			oldCur = null;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}

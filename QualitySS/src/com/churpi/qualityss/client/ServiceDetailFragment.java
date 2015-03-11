package com.churpi.qualityss.client;

import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.dto.ServiceDTO;

/**
 * A fragment representing a single Service detail screen. This fragment is
 * either contained in a {@link ServiceListActivity} in two-pane mode (on
 * tablets) or a {@link ServiceDetailActivity} on handsets.
 */
public class ServiceDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ServiceDetailFragment() {
	}
	
	private ServiceDTO service = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			final int id = getArguments().getInt(ARG_ITEM_ID);
			DbTrans.read(getActivity(), new DbTrans.Db() {
				
				@Override
				public void onDo(Context context, SQLiteDatabase db) {
					Cursor c = db.query(DbService.TABLE_NAME, null, 
							DbService._ID + " = ? ", 
							new String[]{String.valueOf(id)}, 
							null, null, null);
					if(c.moveToFirst()){
						service = new ServiceDTO();
						service.setId(c.getInt(c.getColumnIndex(DbService._ID)));
						service.setType(c.getString(c.getColumnIndex(DbService.CN_TYPE)));
						service.setAddress(c.getString(c.getColumnIndex(DbService.CN_ADDRESS)));
						service.setContact(c.getString(c.getColumnIndex(DbService.CN_CONTACT)));
						service.setDescription(c.getString(c.getColumnIndex(DbService.CN_DESCRIPTION)));
						service.setPhone(c.getString(c.getColumnIndex(DbService.CN_PHONE)));
					}
					c.close();
				}
			});
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_service_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (service != null) {
		
			TextView text = (TextView)rootView.findViewById(R.id.fldType);
			text.setText(service.getType());
		
			text = (TextView)rootView.findViewById(R.id.fldAddress);
			text.setText(service.getAddress());
			
			text = (TextView)rootView.findViewById(R.id.fldContact);
			text.setText(service.getContact());
			
			text = (TextView)rootView.findViewById(R.id.fldPhone);
			text.setText(service.getPhone());
			
			text = (TextView)rootView.findViewById(R.id.fldDescription);
			text.setText(service.getDescription());		
			
			Button button = (Button)rootView.findViewById(android.R.id.button1);
			button.setTag(service.getAddress());
			
			button = (Button)rootView.findViewById(android.R.id.button2);	
			button.setTag(service.getId());
			
			
		}

		return rootView;
	}
}

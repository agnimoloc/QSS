package com.churpi.qualityss.client;

import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

/**
 * An activity representing a list of Sectors. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link SectorDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SectorListFragment} and the item details (if present) is a
 * {@link SectorDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link SectorListFragment.Callbacks} interface to listen for item selections.
 */
public class SectorListActivity extends Activity implements
		SectorListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sector_list);

		if (findViewById(R.id.sector_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((SectorListFragment) getFragmentManager().findFragmentById(
					R.id.sector_list)).setActivateOnItemClick(true);
			setTitle(R.string.title_activity_sector_service);
						
		}else{
			
		}

	}

	/**
	 * Callback method from {@link SectorListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int id) {
		
		Ses.getInstance(this).setSectorId(id);
		/*Constants.getPref(this).edit()
			.putInt(Constants.PREF_SECTOR_ID, id)
			.commit();*/
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			/*Bundle arguments = new Bundle();
			arguments.putInt(SectorDetailFragment.ARG_ITEM_ID, id);*/
			SectorDetailFragment fragment = new SectorDetailFragment();
			//fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.sector_detail_container, fragment).commit();

		} else {
			openSectorDetail(id);
		}
	}
	
	private void openSectorDetail(int sectorId){
		Ses.getInstance(this).setSectorId(sectorId);
		//startActivity(WorkflowHelper.process(this, SectorListActivity.class, R.id.sector_list));
		startActivity(WorkflowHelper.process(this, R.id.sector_list));
	}
	
}

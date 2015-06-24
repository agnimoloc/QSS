package com.churpi.qualityss.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;

/**
 * An activity representing a list of Requisitions. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link RequisitionDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link RequisitionListFragment} and the item details (if present) is a
 * {@link RequisitionDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link RequisitionListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class RequisitionListActivity extends Activity implements
		RequisitionListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private final int DETAIL = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_requisition_list);

		if (findViewById(R.id.requisition_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((RequisitionListFragment) getFragmentManager().findFragmentById(
					R.id.requisition_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link RequisitionListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		startDetail(id);
	}
	
	public void onClick_Add(View v){
		startDetail("0");
	}
	
	private void startDetail(String id){
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putString(RequisitionDetailFragment.ARG_ITEM_ID, id);
			if(getIntent().getAction() != null)
				arguments.putString(RequisitionDetailFragment.ARG_ACTION, getIntent().getAction());
			RequisitionDetailFragment fragment = new RequisitionDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.requisition_detail_container, fragment)
					.commit();

		} else {
			Intent detailIntent = new Intent(this,
					RequisitionDetailActivity.class);
			detailIntent.putExtra(RequisitionDetailFragment.ARG_ITEM_ID, id);
			if(getIntent().getAction() != null)
				detailIntent.putExtra(RequisitionDetailFragment.ARG_ACTION, getIntent().getAction());
			startActivityForResult(detailIntent, DETAIL);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == DETAIL && resultCode == RESULT_OK){
			RequisitionListFragment fragment = ((RequisitionListFragment) getFragmentManager().findFragmentById(
					R.id.requisition_list));
			
			fragment.initCursor();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}

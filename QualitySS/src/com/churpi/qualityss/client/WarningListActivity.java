package com.churpi.qualityss.client;

import com.churpi.qualityss.client.helper.Ses;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.app.FragmentManager;

/**
 * An activity representing a list of Warnings. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link WarningDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link WarningListFragment} and the item details (if present) is a
 * {@link WarningDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link WarningListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class WarningListActivity extends Activity implements
		WarningListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private final int DETAIL = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warning_list);

		setTitle(getString(R.string.title_warning_list, Ses.getInstance(this).getEmployeeName()));
		
		if (findViewById(R.id.warning_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((WarningListFragment) getFragmentManager().findFragmentById(
					R.id.warning_list)).setActivateOnItemClick(true);
			
			setTitle(getString(R.string.title_warning_detail, Ses.getInstance(this).getEmployeeName()));
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link WarningListFragment.Callbacks} indicating
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
			arguments.putString(WarningDetailFragment.ARG_ITEM_ID, id);
			WarningDetailFragment fragment = new WarningDetailFragment();
			fragment.setArguments(arguments);
			if(getIntent().getAction() != null)
				arguments.putString(WarningDetailFragment.ARG_ACTION, getIntent().getAction());
			
			getFragmentManager().beginTransaction()
					.replace(R.id.warning_detail_container, fragment).commit();

		} else {
			Intent detailIntent = new Intent(this, WarningDetailActivity.class);
			detailIntent.putExtra(WarningDetailFragment.ARG_ITEM_ID, id);
			if(getIntent().getAction() != null)
				detailIntent.putExtra(WarningDetailFragment.ARG_ACTION, getIntent().getAction());
			startActivityForResult(detailIntent, DETAIL);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == DETAIL && resultCode == RESULT_OK){
			refreshList(getFragmentManager());			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public static void refreshList(FragmentManager manager){
		WarningListFragment fragment = ((WarningListFragment) manager.findFragmentById(
				R.id.warning_list));
		WarningListFragment fragmentNew = new WarningListFragment(); 
		manager.beginTransaction()
			.remove(fragment)
			.replace(R.id.warning_list, fragmentNew)
			.commit();
	}
}

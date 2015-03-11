package com.churpi.qualityss.client;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.view.View;

/**
 * An activity representing a single Service detail screen. This activity is
 * only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a {@link ServiceListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link ServiceDetailFragment}.
 */
public class ServiceDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putInt(ServiceDetailFragment.ARG_ITEM_ID, getIntent()
					.getIntExtra(ServiceDetailFragment.ARG_ITEM_ID, 0));
			ServiceDetailFragment fragment = new ServiceDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.add(R.id.service_detail_container, fragment).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			navigateUpTo(new Intent(this, ServiceListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onClick_StartService(View view){
		Intent intent = new Intent(this, GeneralReviewActivity.class);
		intent.putExtra(GeneralReviewActivity.SERVICE_ID, 
				getIntent().getIntExtra(GeneralReviewActivity.SERVICE_ID,(Integer)view.getTag() ));
    	startActivity(intent);
	}
	
	public void onClick_map(View view){
		Uri uri = Uri.parse("geo:0,0")
			.buildUpon()
			.appendQueryParameter("q", String.format("%s", 
					 ((String)view.getTag())))
			.build();
		//?q=%s(%s)
		
		/*String uristring = String.format(Locale.ENGLISH, GEO_URI +"?q=%s(%s)", (String)view.getTag(), getString(R.string.lbl_geo_mark_name));
		Uri uri = Uri.parse(uristring);*/
		
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
}

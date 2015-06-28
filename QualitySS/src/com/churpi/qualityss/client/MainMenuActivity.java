package com.churpi.qualityss.client;

import java.util.ArrayList;
import java.util.List;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.db.DbTrans;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceType;
import com.churpi.qualityss.client.helper.MenuAdapter;
import com.churpi.qualityss.client.helper.Ses;
import com.churpi.qualityss.client.helper.WorkflowHelper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainMenuActivity extends Activity {

	
	List<MenuAdapter.MenuItem> menu;
	
	int[] resoruceTypes = new int[]{ R.drawable.inmobiliaria, R.drawable.proteccion_civil, R.drawable.custodias  };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		GridView view = (GridView)findViewById(R.id.gridView1);
		
		menu = new ArrayList<MenuAdapter.MenuItem>();
		
		DbTrans.read(this, new DbTrans.Db() {
			
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				
				//Cursor c = db.rawQuery(DbQuery.GET_ACTIVITIES, null);
				Cursor c = db.query(DbServiceType.TABLE_NAME, null, null, null, null, null, null);
				if(c.moveToFirst()){
					do{
						int activityType = c.getInt(c.getColumnIndex(DbServiceType._ID));
						String title = c.getString(c.getColumnIndex(DbServiceType.CN_TITLE));
						MenuAdapter.MenuItem d = new MenuAdapter.MenuItem();
						d.setKey(activityType);
						d.setResourceId(resoruceTypes[activityType-1]);						
						d.setTitle(title);
						
						menu.add(d);
												
					}while(c.moveToNext());
				}
				c.close();
				return null;
			}
		});
		
		
		MenuAdapter.MenuItem d;
		/*MenuAdapter.MenuItem d = new MenuAdapter.MenuItem();		
		d.setResourceId(R.drawable.no_image_element);
		d.setKey(Constants.ACTIVITY_TYPE_PATRIMONY);
		d.setTitle("Patrimonial");
		
		menu.add(d);
		
		d = new MenuAdapter.MenuItem();		
		d.setResourceId(R.drawable.no_image_element);
		d.setKey(Constants.ACTIVITY_TYPE_FIRE);
		d.setTitle("Bomberos");
		
		menu.add(d);*/

		/*d = new MenuAdapter.MenuItem();		
		d.setResourceId(R.drawable.no_image_element);
		d.setKey(Constants.ACTIVITY_TYPE_HEALTH);
		d.setTitle("Salud");
		
		menu.add(d);*/
		
		d = new MenuAdapter.MenuItem();		
		d.setResourceId(R.drawable.requisicion);
		d.setKey(Constants.ACTIVITY_TYPE_REQUISITION);
		d.setTitle("Requisición");
		
		menu.add(d);

		d = new MenuAdapter.MenuItem();		
		d.setResourceId(R.drawable.mazo);
		d.setKey(Constants.ACTIVITY_TYPE_WARNING);
		d.setTitle("Amonestación");
		
		menu.add(d);
		
		d = new MenuAdapter.MenuItem();		
		d.setResourceId(R.drawable.historico_reportes);
		d.setKey(Constants.ACTIVITY_TYPE_SERVICE_HISTORY);
		d.setTitle("Histórico de servicios");
		
		menu.add(d);

		
		view.setAdapter(new MenuAdapter(this, menu.toArray(new MenuAdapter.MenuItem[menu.size()])));
		view.setOnItemClickListener(onClickItem);
	}
	
	private OnItemClickListener onClickItem = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(id < 4){
				Context context = getBaseContext();			
				Ses.getInstance(context).setActivityType((int)id);
				startActivity(WorkflowHelper.process(MainMenuActivity.this, R.id.gridView1));
			}
		}
	};
}

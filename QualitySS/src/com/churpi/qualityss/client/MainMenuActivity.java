package com.churpi.qualityss.client;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		
		List<Integer> access = new ArrayList<Integer>();
		try {
			JSONArray list = new JSONArray(Ses.getInstance(getApplicationContext()).getPermissions());
			for(int i = 0; i < list.length(); i++){
				JSONObject item = list.getJSONObject(i);
				if(item.getBoolean("Acceso")){
					String name = item.getString("Nombre"); 
					
					if(name.compareTo("requisicion")==0)
						access.add(Constants.ACTIVITY_TYPE_REQUISITION);

					if(name.compareTo("Proteccioncivilybomberos")==0)
						access.add(Constants.ACTIVITY_TYPE_FIRE);

					if(name.compareTo("bienesinmuebles")==0)
						access.add(Constants.ACTIVITY_TYPE_PATRIMONY);

					if(name.compareTo("custodias")==0)
						access.add(Constants.ACTIVITY_TYPE_CUSTODY);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DbTrans.read(this, access, new DbTrans.Db() {
			
			@Override
			public Object onDo(Context context, Object parameter, SQLiteDatabase db) {
				@SuppressWarnings("unchecked")
				ArrayList<Integer> access = (ArrayList<Integer>)parameter;
				if(access != null){
					Cursor c = db.query(DbServiceType.TABLE_NAME, null, null, null, null, null, null);
					if(c.moveToFirst()){
						do{
							int activityType = c.getInt(c.getColumnIndex(DbServiceType._ID));
							if(access.contains(activityType)){
								String title = c.getString(c.getColumnIndex(DbServiceType.CN_TITLE));
								MenuAdapter.MenuItem d = new MenuAdapter.MenuItem();
								d.setKey(activityType);
								d.setResourceId(resoruceTypes[activityType-1]);						
								d.setTitle(title);

								menu.add(d);
							}
						}while(c.moveToNext());
					}
					c.close();
				}
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
		
		if(((ArrayList<Integer>)access) != null){ 
			if(access.contains(Constants.ACTIVITY_TYPE_REQUISITION)){
				d = new MenuAdapter.MenuItem();		
				d.setResourceId(R.drawable.requisicion);
				d.setKey(Constants.ACTIVITY_TYPE_REQUISITION);
				d.setTitle("Requisición");

				menu.add(d);
			}
		}

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

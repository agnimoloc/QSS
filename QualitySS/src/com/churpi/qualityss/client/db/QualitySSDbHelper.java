package com.churpi.qualityss.client.db;

import com.churpi.qualityss.Constants;
import com.churpi.qualityss.client.R;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbAddress;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbCustomer;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployeeEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSection;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSector;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceConfiguration;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEquipment;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceType;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbState;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbSurveyQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbTown;
import com.churpi.qualityss.client.dto.AddressDTO;
import com.churpi.qualityss.client.dto.CustomerDTO;
import com.churpi.qualityss.client.dto.DataDTO;
import com.churpi.qualityss.client.dto.EmployeeDTO;
import com.churpi.qualityss.client.dto.EquipmentDTO;
import com.churpi.qualityss.client.dto.QuestionDTO;
import com.churpi.qualityss.client.dto.ReviewDTO;
import com.churpi.qualityss.client.dto.SectionDTO;
import com.churpi.qualityss.client.dto.SectorDTO;
import com.churpi.qualityss.client.dto.ServiceConfigurationDTO;
import com.churpi.qualityss.client.dto.ServiceDTO;
import com.churpi.qualityss.client.dto.ServiceEmployeeDTO;
import com.churpi.qualityss.client.dto.ServiceTypeDTO;
import com.churpi.qualityss.client.dto.StateDTO;
import com.churpi.qualityss.client.dto.TownDTO;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class QualitySSDbHelper extends SQLiteOpenHelper {

	private static final String DATABASENAME = "QSSDB";
	private static final int DATABASEVERSION = 1;
	private final Context context;

	public QualitySSDbHelper(Context context) {
		super(context,DATABASENAME,null, DATABASEVERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(QualitySSDbContract.DbState.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbTown.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbAddress.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbCustomer.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbSector.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbEmployee.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbServiceType.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbService.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbServiceConfiguration.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbServiceInstance.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbServiceEmployee.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbEquipment.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbServiceEquipment.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbServiceEquipmentInventory.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbEmployeeEquipment.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbEmployeeEquipmentInventory.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbSection.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbQuestion.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbReviewQuestion.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbReviewQuestionAnswerEmployee.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbReviewQuestionAnswerService.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbSurveyQuestion.CREATE_TABLE);
		db.execSQL(QualitySSDbContract.DbSurveyQuestionAnswer.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void updateDBfromValue(DataDTO data){
		SQLiteDatabase db = getWritableDatabase();
		try{
			db.beginTransaction();	
			int count = 0;
			if(data.getEstados() != null){
				for(StateDTO state : data.getEstados()){
					ContentValues values = new ContentValues();
					values.put(DbState.CN_NAME, state.getNombre());

					count = db.update(DbState.TABLE_NAME, values, 
							DbState._ID + "=?", 
							new String[]{String.valueOf(state.getEstadoId())});
					if(count==0){
						values.put(DbState._ID, state.getEstadoId());
						db.insert(DbState.TABLE_NAME, null, values);
					}
				}
			}

			if(data.getMunicipios() != null){
				for(TownDTO town : data.getMunicipios()){
					ContentValues values = new ContentValues();
					values.put(DbTown.CN_NAME, town.getNombre());

					count = db.update(DbTown.TABLE_NAME, values, 
							DbTown._ID + "=?", 
							new String[]{String.valueOf(town.getMunicipioId())});
					if(count==0){
						values.put(DbTown._ID, town.getMunicipioId());
						db.insert(DbTown.TABLE_NAME, null, values);
					}
				}
			}

			if(data.getClientes() != null){
				for(CustomerDTO customer : data.getClientes()){
					if(customer.getDomicilio() != null){
						addUpdateAddress(customer.getDomicilio(), db);
					}

					ContentValues values = customer.getContentValues();
					count = db.update(DbCustomer.TABLE_NAME, values, 
							DbCustomer._ID + "=?", 
							new String[]{String.valueOf(customer.getClienteId())});
					if(count == 0){
						values.put(DbCustomer._ID, customer.getClienteId());
						db.insert(DbCustomer.TABLE_NAME, null, values);
					}

				}
			}

			if(data.getSectores() != null){
				for(SectorDTO sector : data.getSectores()){
					ContentValues values = sector.getContentValues();
					count = db.update(DbSector.TABLE_NAME, values, 
							DbSector._ID + "=?", 
							new String[]{String.valueOf(sector.getSectorId())});
					if(count == 0){
						values.put(DbSector._ID, sector.getSectorId());
						db.insert(DbSector.TABLE_NAME, null, values);
					}
				}
			}


			if(data.getElementos() != null){
				for(EmployeeDTO employee : data.getElementos()){
					ContentValues values = employee.getContentValues();

					count = db.update(DbEmployee.TABLE_NAME, values, 
							DbEmployee._ID + "=?", 
							new String[]{String.valueOf(employee.getElementoId())});
					if(count == 0){
						values.put(DbEmployee._ID, employee.getElementoId());
						db.insert(DbEmployee.TABLE_NAME, null, values);
					}

					db.delete(DbEmployeeEquipment.TABLE_NAME, 
							DbEmployeeEquipment.CN_EMPLOYEE +"=?", 
							new String[]{String.valueOf(employee.getElementoId())});

					for(EquipmentDTO equipment : employee.getEquipo()){
						addUpdateEquipment(equipment, db);

						ContentValues vEqui = new ContentValues();
						vEqui.put(DbEmployeeEquipment.CN_EMPLOYEE, employee.getElementoId());
						vEqui.put(DbEmployeeEquipment.CN_EQUIPMENT, equipment.getEquipoId());
						db.insert(DbEmployeeEquipment.TABLE_NAME, null, vEqui);
					}

				}
			}

			if(data.getSecciones() != null){
				for(SectionDTO section : data.getSecciones()){
					ContentValues values = new ContentValues();
					values.put(DbSection.CN_DESCRIPTION, section.getDescripcion());

					count = db.update(DbSection.TABLE_NAME, values, 
							DbSection._ID + "=?", 
							new String[]{String.valueOf(section.getPaseRevistaSeccionId())});
					if(count==0){
						values.put(DbSection._ID, section.getPaseRevistaSeccionId());
						db.insert(DbSection.TABLE_NAME, null, values);
					}
				}
			}
			
			if(data.getTiposServicio() != null){
				for(ServiceTypeDTO serviceType : data.getTiposServicio()){
					ContentValues values = new ContentValues();
					values.put(DbServiceType.CN_TITLE, serviceType.getNombre());

					count = db.update(DbServiceType.TABLE_NAME, values, 
							DbServiceType._ID + "=?", 
							new String[]{String.valueOf(serviceType.getTipoServicioId())});
					if(count==0){
						values.put(DbServiceType._ID, serviceType.getTipoServicioId());
						db.insert(DbServiceType.TABLE_NAME, null, values);
					}
				}
			}

			if(data.getServicios() != null){
				for(ServiceDTO service : data.getServicios()){
					String[] whereServiceId = new String[]{String.valueOf(service.getServicioId())};
					
					db.delete(DbServiceEmployee.TABLE_NAME, 
							DbServiceEmployee.CN_SERVICE +"=?", 
							whereServiceId);
					
					db.delete(DbReviewQuestion.TABLE_NAME, 
							DbReviewQuestion.CN_SERVICE +"=?", 
							whereServiceId);

					db.delete(DbSurveyQuestion.TABLE_NAME, 
							DbSurveyQuestion.CN_SERVICE +"=?", 
							whereServiceId);

					db.delete(DbServiceEquipment.TABLE_NAME, 
							DbServiceEquipment.CN_SERVICE +"=?", 
							whereServiceId);

					db.delete(DbServiceConfiguration.TABLE_NAME, 
							DbServiceConfiguration.CN_SERVICE +"=?", 
							whereServiceId);


					if(service.getDomicilio() != null){
						addUpdateAddress(service.getDomicilio(), db);
					}

					ContentValues values = service.getContentValues();
					 
					count = db.update(DbService.TABLE_NAME, values, 
							DbService._ID + "=?", 
							whereServiceId);
					if(count==0){
						values.put(DbService._ID, service.getServicioId());
						db.insert(DbService.TABLE_NAME, null, values);
					}


					for(ServiceEmployeeDTO serviceEmp : service.getServicioElementos()){					
						serviceEmp.setServicioId(service.getServicioId());
						ContentValues sVals = serviceEmp.getContentValues();

						db.insert(DbServiceEmployee.TABLE_NAME, null, sVals);														
					}

					if(service.getConfiguraciones() != null){
						for(ServiceConfigurationDTO config : service.getConfiguraciones()){
							ContentValues scVal = new ContentValues();
							scVal.put(DbServiceConfiguration._ID, config.getServicioConfiguracionId());
							scVal.put(DbServiceConfiguration.CN_SERVICE, service.getServicioId());
							scVal.put(DbServiceConfiguration.CN_TITLE, config.getTitulo());
							scVal.put(DbServiceConfiguration.CN_ACTIVITY_TYPE, config.getTipo());
							db.insert(DbServiceConfiguration.TABLE_NAME, null, scVal);
							
							if(config.getExamen() != null){
								for(QuestionDTO question : config.getExamen().getPreguntas()){
									addUpdateQuestion(question, db);
									ContentValues qVal = new ContentValues();
									qVal.put(DbSurveyQuestion.CN_ACTIVITY_TYPE, config.getTipo());
									qVal.put(DbSurveyQuestion.CN_SERVICE, service.getServicioId());
									qVal.put(DbSurveyQuestion.CN_QUESTION, question.getPreguntaId());
									db.insert(DbSurveyQuestion.TABLE_NAME, null, qVal);
								}							
							}
							if(config.getPaseRevistaElemento() != null){
								for(ReviewDTO review : config.getPaseRevistaElemento()){
									addUpdateReview (review, db, DbReviewQuestion.Types.EMPLOYEE, service.getServicioId(), config.getTipo());
								}
							}
							if(config.getPaseRevistaServicio() != null){
								for(ReviewDTO review : config.getPaseRevistaServicio()){
									addUpdateReview (review, db, DbReviewQuestion.Types.SERVICE, service.getServicioId(), config.getTipo());
								}
							}
							if(config.getServicioEquipo() != null){
								for(EquipmentDTO equipment : config.getServicioEquipo()){
									addUpdateEquipment(equipment, db);
									ContentValues vEqui = new ContentValues();
									vEqui.put(DbServiceEquipment.CN_ACTIVITY_TYPE, config.getTipo());
									vEqui.put(DbServiceEquipment.CN_SERVICE, service.getServicioId());
									vEqui.put(DbServiceEquipment.CN_EQUIPMENT, equipment.getEquipoId());
									db.insert(DbServiceEquipment.TABLE_NAME, null, vEqui);
								}
							}
						}
					}
				}			
			}

			db.setTransactionSuccessful();
			sendBroadCastResult(Constants.PULL_PUSH_DATA_REFRESH, "", 100);
		}catch(Exception ex){
			Toast.makeText(context, context.getString(R.string.msg_error_recived_data), Toast.LENGTH_LONG).show();
		}finally{
			db.endTransaction();
		}

	}

	private void addUpdateReview(ReviewDTO review, SQLiteDatabase db, int reviewType, int serviceId, int activityType){
		for(QuestionDTO question : review.getPreguntas()){
			addUpdateQuestion(question, db);
			ContentValues qVal = new ContentValues();
			qVal.put(DbReviewQuestion.CN_ACTIVITY_TYPE, activityType);
			qVal.put(DbReviewQuestion.CN_SERVICE, serviceId);
			qVal.put(DbReviewQuestion.CN_QUESTION, question.getPreguntaId());
			qVal.put(DbReviewQuestion.CN_SECTION, review.getPaseRevistaSeccionId());
			qVal.put(DbReviewQuestion.CN_TYPE, reviewType);
			db.insert(DbReviewQuestion.TABLE_NAME, null, qVal);
		}															
	}

	private void addUpdateEquipment(EquipmentDTO equipment, SQLiteDatabase db){
		ContentValues values = equipment.getContentValues();
		int count = db.update(DbEquipment.TABLE_NAME, values, 
				DbEquipment._ID + "=?", 
				new String[]{String.valueOf(equipment.getEquipoId())});
		if(count == 0){
			values.put(DbEquipment._ID, equipment.getEquipoId());
			db.insert(DbEquipment.TABLE_NAME, null, values);
		}
	}

	private void addUpdateQuestion(QuestionDTO question, SQLiteDatabase db){
		ContentValues qVal = new ContentValues();
		qVal.put(DbQuestion.CN_DESCRIPTION, question.getDescripcion());
		qVal.put(DbQuestion.CN_VALUE, question.getValor());

		int count = db.update(DbQuestion.TABLE_NAME, qVal, 
				DbQuestion._ID + "=?", 
				new String[]{String.valueOf(question.getPreguntaId())});
		if(count==0){
			qVal.put(DbQuestion._ID, question.getPreguntaId());
			db.insert(DbQuestion.TABLE_NAME, null, qVal);
		}						
	}

	private void addUpdateAddress(AddressDTO address, SQLiteDatabase db){
		ContentValues values = address.getContentValues();		

		int count = db.update(DbAddress.TABLE_NAME, values , 
				DbAddress._ID + "=?", 
				new String[]{String.valueOf(address.getDomicilioId())});
		if(count==0){
			values.put(DbAddress._ID, address.getDomicilioId());
			db.insert(DbAddress.TABLE_NAME, null, values);
		}						
	}

	private void sendBroadCastResult(String statusDescription, String description, int progress){
		Intent status = new Intent();
		status.setAction(Constants.PULL_PUSH_DATA_ACTION);
		status.putExtra(Constants.PULL_PUSH_DATA_STATUS, statusDescription);
		status.putExtra(Constants.PULL_PUSH_DATA_DESCRIPTION, description);
		status.putExtra(Constants.PULL_PUSH_DATA_PROGRESS, progress);
		status.putExtra(Constants.PULL_PUSH_DATA_DATA, 0);
		context.sendBroadcast(status);

	}

}

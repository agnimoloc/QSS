package com.churpi.qualityss.client.dto;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;

import android.content.ContentValues;
import android.database.Cursor;

public class ServiceDTO {
	
	int ServicioId;
	String Code;
	String Descripcion;
	AddressDTO Domicilio;
	int ClienteId;
	int SectorId;
	ServiceConfigurationDTO[] Configuraciones;
	ServiceEmployeeDTO[] ServicioElementos;
	
	//SurveyDTO Examen;
		
	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();		
		values.put(DbService.CN_CODE, Code);
		values.put(DbService.CN_DESCRIPTION, Descripcion);
		values.put(DbService.CN_CUSTOMER, ClienteId);
		values.put(DbService.CN_SECTOR, SectorId);
		if(Domicilio != null){
			values.put(DbService.CN_ADDRESS, Domicilio.getDomicilioId());
		}
		return values;
	}
	public void fillFromCursor(Cursor c) {
		int index = c.getColumnIndex(DbService._ID);
		if(index != -1){
			ServicioId = c.getInt(index);
		}
		index = c.getColumnIndex(DbService.CN_CODE);
		if(index != -1){
			Code = c.getString(index);
		}
		index = c.getColumnIndex(DbService.CN_DESCRIPTION);
		if(index != -1){
			Descripcion = c.getString(index);
		}
		index = c.getColumnIndex(DbService.CN_ADDRESS);
		if(index != -1){
			Domicilio = new AddressDTO();
			Domicilio.setDomicilioId(c.getInt(index));
		}
		index = c.getColumnIndex(DbService.CN_CUSTOMER);
		if(index != -1){
			ClienteId = c.getInt(index);
		}
		index = c.getColumnIndex(DbService.CN_SECTOR);
		if(index != -1){
			SectorId = c.getInt(index);
		}		
	}
	
	public int getServicioId() {
		return ServicioId;
	}
	public void setServicioId(int servicioId) {
		ServicioId = servicioId;
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	
	public AddressDTO getDomicilio() {
		return Domicilio;
	}
	public void setDomicilio(AddressDTO domicilio) {
		Domicilio = domicilio;
	}
	/*public SurveyDTO getExamen() {
		return Examen;
	}
	public void setExamen(SurveyDTO examen) {
		Examen = examen;
	}*/
	public ServiceEmployeeDTO[] getServicioElementos() {
		return ServicioElementos;
	}
	public void setServicioElementos(ServiceEmployeeDTO[] servicioElementos) {
		ServicioElementos = servicioElementos;
	}

	public int getClienteId() {
		return ClienteId;
	}

	public void setClienteId(int clienteId) {
		ClienteId = clienteId;
	}

	public int getSectorId() {
		return SectorId;
	}

	public void setSectorId(int sectorId) {
		SectorId = sectorId;
	}
	
	public ServiceConfigurationDTO[] getConfiguraciones() {
		return Configuraciones;
	}
	
	public void setConfiguraciones(ServiceConfigurationDTO[] configuraciones) {
		Configuraciones = configuraciones;
	}	
}

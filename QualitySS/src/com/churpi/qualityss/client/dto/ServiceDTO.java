package com.churpi.qualityss.client.dto;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;

import android.content.ContentValues;
import android.database.Cursor;

public class ServiceDTO {
	int ServicioId;
	String Code;
	String Descripcion;
	String Domicilio;
	int ClienteId;
	SurveyDTO Examen;
	int SectorId;
	ServiceEmployeeDTO[] ServicioElementos;
	
	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();		
		values.put(DbService.CN_CODE, Code);
		values.put(DbService.CN_DESCRIPTION, Descripcion);
		values.put(DbService.CN_ADDRESS, Domicilio);
		values.put(DbService.CN_CUSTOMER, ClienteId);
		values.put(DbService.CN_SECTOR, SectorId);
		return values;
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
	public String getDomicilio() {
		return Domicilio;
	}
	public void setDomicilio(String domicilio) {
		Domicilio = domicilio;
	}
	public SurveyDTO getExamen() {
		return Examen;
	}
	public void setExamen(SurveyDTO examen) {
		Examen = examen;
	}
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
			Domicilio = c.getString(index);
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
	
	
}

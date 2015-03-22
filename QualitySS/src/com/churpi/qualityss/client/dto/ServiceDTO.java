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
	SurveyDTO Examen;
	ReviewDTO[] PaseRevista;
	int SectorId;
	ServiceEmployeeDTO[] ServicioElementos;
	int ElementoRevisionId;
	String FechaRevision;
	String UltimaRevision;
	String Status;
	EquipmentDTO[] ServicioEquipo;
	
	
	
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
		index = c.getColumnIndex(DbService.CN_EMPLOYEEREVIEW);
		if(index != -1){
			ElementoRevisionId = c.getInt(index);
		}
		index = c.getColumnIndex(DbService.CN_DATETIME);
		if(index != -1){
			FechaRevision = c.getString(index);
		}
		index = c.getColumnIndex(DbService.CN_LASTREVIEW);
		if(index != -1){
			UltimaRevision = c.getString(index);
		}	
		index = c.getColumnIndex(DbService.CN_STATUS);
		if(index != -1){
			Status = c.getString(index);
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
	

	public ReviewDTO[] getPaseRevista() {
		return PaseRevista;
	}

	public void setPaseRevista(ReviewDTO[] paseRevista) {
		PaseRevista = paseRevista;
	}

	public int getElementoRevisionId() {
		return ElementoRevisionId;
	}

	public void setElementoRevisionId(int elementoRevisionId) {
		ElementoRevisionId = elementoRevisionId;
	}

	public String getFechaRevision() {
		return FechaRevision;
	}

	public void setFechaRevision(String fechaRevision) {
		FechaRevision = fechaRevision;
	}

	public String getUltimaRevision() {
		return UltimaRevision;
	}

	public void setUltimaRevision(String ultimaRevision) {
		UltimaRevision = ultimaRevision;
	}
	
	public EquipmentDTO[] getServicioEquipo() {
		return ServicioEquipo;
	}
	public void setServicioEquipo(EquipmentDTO[] servicioEquipo) {
		ServicioEquipo = servicioEquipo;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public boolean canOpen() {
		
		return true;
	}
	public boolean canStart() {
		if(Status == null)
			return true;
		return false;
	}
	
}

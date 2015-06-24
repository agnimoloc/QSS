package com.churpi.qualityss.client.dto;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbRequisition;

import android.database.Cursor;

public class RequisitionDTO {
	int id;
	int ResponsableId;
	String Acuerdo_Compromiso;
	int ElementoId;
	String Avance;
	String Status;
	String FechaInicio;
	String FechaTerminacion;
	int ServicioId;
	int Sent;
	String FechaCreacion;
	int Creador;
	
	public String getFechaCreacion() {
		return FechaCreacion;
	}
	public void setFechaCreacion(String fechaCreacion) {
		FechaCreacion = fechaCreacion;
	}
	public int getCreador() {
		return Creador;
	}
	public void setCreador(int creador) {
		Creador = creador;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSent() {
		return Sent;
	}
	public void setSent(int sent) {
		Sent = sent;
	}
	public int getResponsableId() {
		return ResponsableId;
	}
	public void setResponsableId(int responsableId) {
		ResponsableId = responsableId;
	}
	public String getAcuerdo_Compromiso() {
		return Acuerdo_Compromiso;
	}
	public void setAcuerdo_Compromiso(String acuerdo_Compromiso) {
		Acuerdo_Compromiso = acuerdo_Compromiso;
	}
	public int getElementoId() {
		return ElementoId;
	}
	public void setElementoId(int elementoId) {
		ElementoId = elementoId;
	}
	public String getAvance() {
		return Avance;
	}
	public void setAvance(String avance) {
		Avance = avance;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getFechaInicio() {
		return FechaInicio;
	}
	public void setFechaInicio(String fechaInicio) {
		FechaInicio = fechaInicio;
	}
	public String getFechaTerminacion() {
		return FechaTerminacion;
	}
	public void setFechaTerminacion(String fechaTerminacion) {
		FechaTerminacion = fechaTerminacion;
	}
	public int getServicioId() {
		return ServicioId;
	}
	public void setServicioId(int servicioId) {
		ServicioId = servicioId;
	}
	
	
	public void fillFromCursor(Cursor c){
		int index = c.getColumnIndex(DbRequisition._ID);
		if(index != -1){
			id = c.getInt(index);
		}
		index = c.getColumnIndex(DbRequisition.CN_AGREEMENT);
		if(index != -1){
			Acuerdo_Compromiso = c.getString(index);
		}
		index = c.getColumnIndex(DbRequisition.CN_ASSIGN_EMPLOYEE);
		if(index != -1){
			ResponsableId = c.getInt(index);
		}
		index = c.getColumnIndex(DbRequisition.CN_END_DATE);
		if(index != -1){
			FechaTerminacion = c.getString(index);
		}
		index = c.getColumnIndex(DbRequisition.CN_EMPLOYEE);
		if(index != -1){
			ElementoId = c.getInt(index);
		}
		index = c.getColumnIndex(DbRequisition.CN_PROGRESS);
		if(index != -1){
			Avance = c.getString(index);
		}
		index = c.getColumnIndex(DbRequisition.CN_SERVICE);
		if(index != -1){
			ServicioId = c.getInt(index);
		}
		index = c.getColumnIndex(DbRequisition.CN_START_DATE);
		if(index != -1){
			FechaInicio = c.getString(index);
		}
		index = c.getColumnIndex(DbRequisition.CN_STATUS);
		if(index != -1){
			Status = c.getString(index);
		}
		index = c.getColumnIndex(DbRequisition.CN_CREATION_DATE);
		if(index != -1){
			FechaCreacion = c.getString(index);
		}
		index = c.getColumnIndex(DbRequisition.CN_CREATOR);
		if(index != -1){
			Creador = c.getInt(index);
		}
	}
	
	public String getUniqueKey(){
		return "REQ_" + String.valueOf(Creador) + "_" + FechaCreacion;
	}
}

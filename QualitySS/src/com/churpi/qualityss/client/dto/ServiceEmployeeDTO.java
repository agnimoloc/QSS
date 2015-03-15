package com.churpi.qualityss.client.dto;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceEmployee;

import android.content.ContentValues;

public class ServiceEmployeeDTO {
	int ElementoId;
	int Tipo;
	int ServicioId;
	EmployeeDTO Elemento;
	
	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		
		values.put(DbServiceEmployee.CN_EMPLOYEE, Elemento.getElementoId());
		values.put(DbServiceEmployee.CN_SERVICE, ServicioId);
		values.put(DbServiceEmployee.CN_TYPE, Tipo);		
		
		return values ;
	}
	
	public int getTipo() {
		return Tipo;
	}
	public void setTipo(int tipo) {
		Tipo = tipo;
	}
	public int getServicioId() {
		return ServicioId;
	}
	public void setServicioId(int servicioId) {
		ServicioId = servicioId;
	}

	public int getElementoId() {
		return ElementoId;
	}

	public void setElementoId(int elementoId) {
		ElementoId = elementoId;
	}
	
}

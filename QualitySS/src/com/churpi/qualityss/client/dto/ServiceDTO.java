package com.churpi.qualityss.client.dto;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;

import android.content.ContentValues;

public class ServiceDTO {
	int ServicioId;
	int Tipo;
	String Code;
	String Descripcion;
	String Domicilio;
	String Cliente;
	SurveyDTO Examen;
	EmployeeDTO Supervisor;
	ServiceEmployeeDTO[] ServicioElementos;
	
	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		values.put(DbService._ID, ServicioId);
		values.put(DbService.CN_TYPE, Tipo);
		values.put(DbService.CN_CODE, Code);
		values.put(DbService.CN_DESCRIPTION, Descripcion);
		values.put(DbService.CN_ADDRESS, Domicilio);
		values.put(DbService.CN_CUSTOMER, Cliente);
		return values;
	}
	
	public int getServicioId() {
		return ServicioId;
	}
	public void setServicioId(int servicioId) {
		ServicioId = servicioId;
	}
	public int getTipo() {
		return Tipo;
	}
	public void setTipo(int tipo) {
		Tipo = tipo;
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
	public String getCliente() {
		return Cliente;
	}
	public void setCliente(String cliente) {
		Cliente = cliente;
	}
	public SurveyDTO getExamen() {
		return Examen;
	}
	public void setExamen(SurveyDTO examen) {
		Examen = examen;
	}
	public EmployeeDTO getSupervisor() {
		return Supervisor;
	}
	public void setSupervisor(EmployeeDTO supervisor) {
		Supervisor = supervisor;
	}
	public ServiceEmployeeDTO[] getServicioElementos() {
		return ServicioElementos;
	}
	public void setServicioElementos(ServiceEmployeeDTO[] servicioElementos) {
		ServicioElementos = servicioElementos;
	}
	
}

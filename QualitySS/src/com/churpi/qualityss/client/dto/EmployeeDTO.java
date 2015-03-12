package com.churpi.qualityss.client.dto;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;

import android.content.ContentValues;

public class EmployeeDTO {
	int ElementoId;
	String Code;
	String Nombre;
	String Matricula;
	EquipmentDTO[] Equipo;
	int ServicioId;
	
	
	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		
		values.put(DbEmployee._ID, ElementoId);
		values.put(DbEmployee.CN_CODE, Code);
		values.put(DbEmployee.CN_NAME, Nombre);
		values.put(DbEmployee.CN_PLATE, Matricula);
		values.put(DbEmployee.CN_SERVICE, ServicioId);
		
		return values;
	}
	
	public int getElementoId() {
		return ElementoId;
	}
	public void setElementoId(int elementoId) {
		ElementoId = elementoId;
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public String getNombre() {
		return Nombre;
	}
	public void setNombre(String nombre) {
		Nombre = nombre;
	}
	public String getMatricula() {
		return Matricula;
	}
	public void setMatricula(String matricula) {
		Matricula = matricula;
	}
	public EquipmentDTO[] getEquipo() {
		return Equipo;
	}
	public void setEquipo(EquipmentDTO[] equipo) {
		Equipo = equipo;
	}

	public int getServicioId() {
		return ServicioId;
	}

	public void setServicioId(int servicioId) {
		ServicioId = servicioId;
	}
	
}

package com.churpi.qualityss.client.dto;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbEmployee;

import android.content.ContentValues;
import android.database.Cursor;

public class EmployeeDTO {
	int ElementoId;
	String Code;
	String Nombre;
	String Matricula;
	String Status;
	int BarcodeChecked;
	EquipmentDTO[] Equipo;
	
	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
	
		values.put(DbEmployee.CN_CODE, Code);
		values.put(DbEmployee.CN_NAME, Nombre);
		values.put(DbEmployee.CN_PLATE, Matricula);
		
		return values;
	}
	
	public void fillFromCursor(Cursor c) {
		int index = c.getColumnIndex(DbEmployee._ID);
		if(index != -1){
			ElementoId = c.getInt(index);
		}
		index = c.getColumnIndex(DbEmployee.CN_CODE);
		if(index != -1){
			Code = c.getString(index);
		}
		index = c.getColumnIndex(DbEmployee.CN_NAME);
		if(index != -1){
			Nombre = c.getString(index);
		}
		index = c.getColumnIndex(DbEmployee.CN_PLATE);
		if(index != -1){
			Matricula = c.getString(index);
		}
		index = c.getColumnIndex(DbEmployee.CN_STATUS);
		if(index != -1){
			Status = c.getString(index);
		}
		index = c.getColumnIndex(DbEmployee.CN_BARCODECHECK);
		if(index != -1){
			BarcodeChecked = c.getInt(index);
		}
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

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public int getBarcodeChecked() {
		return BarcodeChecked;
	}

	public void setBarcodeChecked(int barcodeChecked) {
		BarcodeChecked = barcodeChecked;
	}		
	
}

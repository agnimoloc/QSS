package com.churpi.qualityss.client.dto;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbEquipment;

import android.content.ContentValues;

public class EquipmentDTO {
	int EquipoId;
	String Descripcion;
	int Tipo;
	
	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		
		values.put(DbEquipment.CN_DESCRIPTION, Descripcion);
		values.put(DbEquipment.CN_TYPE, Tipo);
		
		return values;
	}
	
	public int getEquipoId() {
		return EquipoId;
	}
	public void setEquipoId(int equipoId) {
		EquipoId = equipoId;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	public int getTipo() {
		return Tipo;
	}
	public void setTipo(int tipo) {
		Tipo = tipo;
	}
	
}

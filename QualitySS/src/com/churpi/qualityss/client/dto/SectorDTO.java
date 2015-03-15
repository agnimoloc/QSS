package com.churpi.qualityss.client.dto;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbSector;

import android.content.ContentValues;

public class SectorDTO {
	int SectorId;
	String Nombre;
	
	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		
		values.put(DbSector.CN_NAME, Nombre);
		
		return values;
	}
	
	public int getSectorId() {
		return SectorId;
	}
	public void setSectorId(int sectorId) {
		SectorId = sectorId;
	}
	public String getNombre() {
		return Nombre;
	}
	public void setNombre(String nombre) {
		Nombre = nombre;
	}
	
}

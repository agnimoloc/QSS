package com.churpi.qualityss.client.dto;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbCustomer;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;

import android.content.ContentValues;
import android.database.Cursor;

public class CustomerDTO {
	int ClienteId;
	String Code;
	String Descripcion;
	String Domicilio;
	
	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		values.put(DbCustomer.CN_CODE, Code);
		values.put(DbCustomer.CN_DESCRIPTION, Descripcion);
		values.put(DbCustomer.CN_ADDRESS, Domicilio);
		return values;
	}
	
	public int getClienteId() {
		return ClienteId;
	}
	public void setClienteId(int clienteId) {
		ClienteId = clienteId;
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

	public void fillFromCursor(Cursor c) {
		int index = c.getColumnIndex(DbCustomer._ID);
		if(index != -1){
			ClienteId = c.getInt(index);
		}
		index = c.getColumnIndex(DbCustomer.CN_CODE);
		if(index != -1){
			Code = c.getString(index);
		}
		index = c.getColumnIndex(DbCustomer.CN_DESCRIPTION);
		if(index != -1){
			Descripcion = c.getString(index);
		}
		index = c.getColumnIndex(DbCustomer.CN_ADDRESS);
		if(index != -1){
			Domicilio = c.getString(index);
		}
	}
	
}

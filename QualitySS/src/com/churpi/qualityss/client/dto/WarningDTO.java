package com.churpi.qualityss.client.dto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbWarning;

public class WarningDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5567292589526294783L;
	private int id;
	private int ElementoId;
	private int ServiceId;
	private String Observaciones;
	private int IsSent;
	private String FechaCreacion;
	private int CreadorId;
	private List<WarningDetailDTO> Details;
	
	public WarningDTO(){
		Details = new ArrayList<WarningDetailDTO>();
	}
	
	public int getServiceId() {
		return ServiceId;
	}

	public void setServiceId(int serviceId) {
		ServiceId = serviceId;
	}

	public List<WarningDetailDTO> getDetails() {
		return Details;
	}
	public void setDetails(List<WarningDetailDTO> details) {
		Details = details;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getElementoId() {
		return ElementoId;
	}
	public void setElementoId(int elementoId) {
		ElementoId = elementoId;
	}
	public String getObservaciones() {
		return Observaciones;
	}
	public void setObservaciones(String observaciones) {
		Observaciones = observaciones;
	}
	public int getIsSent() {
		return IsSent;
	}
	public void setIsSent(int isSent) {
		IsSent = isSent;
	}
	public String getFechaCreacion() {
		return FechaCreacion;
	}
	public void setFechaCreacion(String fechaCreacion) {
		FechaCreacion = fechaCreacion;
	}
	public int getCreadorId() {
		return CreadorId;
	}
	public void setCreadorId(int creadorId) {
		CreadorId = creadorId;
	}
	
	public void fillFromCursor(Cursor c){
		int index = c.getColumnIndex(DbWarning._ID);
		if(index != -1){
			id = c.getInt(index);
		}
		index = c.getColumnIndex(DbWarning.CN_CREATION_DATE);
		if(index != -1){
			FechaCreacion = c.getString(index);
		}
		index = c.getColumnIndex(DbWarning.CN_CREATOR);
		if(index != -1){
			CreadorId = c.getInt(index);
		}
		index = c.getColumnIndex(DbWarning.CN_EMPLOYEE);
		if(index != -1){
			ElementoId = c.getInt(index);
		}
		index = c.getColumnIndex(DbWarning.CN_SERVICE);
		if(index != -1 && !c.isNull(index)){
			ServiceId = c.getInt(index);
		}

		index = c.getColumnIndex(DbWarning.CN_NOTE);
		if(index != -1){
			Observaciones = c.getString(index);
		}
		index = c.getColumnIndex(DbWarning.CN_SENT);
		if(index != -1){
			IsSent = c.getInt(index);
		}
	}
	
}

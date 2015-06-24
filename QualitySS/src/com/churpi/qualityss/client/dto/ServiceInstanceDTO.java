package com.churpi.qualityss.client.dto;


import android.content.ContentValues;
import android.database.Cursor;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbServiceInstance;

public class ServiceInstanceDTO {
	
	private int ServicioInstanciaId;
	private int Tipo;
	private int ServicioConfiguracionId;
	private int ServicioId;
	private String Key;
	private int EmpleadoRevision;
	private String fechaInicio;
	private String fechaFin;
	private String Status;
	private String ComentariosInventario;
	private String ComentariosCheckList;
	private String ComentariosElementos;
	
	
	public int getServicioConfiguracionId() {
		return ServicioConfiguracionId;
	}
	public void setServicioConfiguracionId(int servicioConfiguracionId) {
		ServicioConfiguracionId = servicioConfiguracionId;
	}
	public String getComentariosInventario() {
		return ComentariosInventario;
	}
	public void setComentariosInventario(String comentariosInventario) {
		ComentariosInventario = comentariosInventario;
	}
	public String getComentariosCheckList() {
		return ComentariosCheckList;
	}
	public void setComentariosCheckList(String comentariosCheckList) {
		ComentariosCheckList = comentariosCheckList;
	}
	public String getComentariosElementos() {
		return ComentariosElementos;
	}
	public void setComentariosElementos(String comentariosElementos) {
		ComentariosElementos = comentariosElementos;
	}
	public int getServicioInstanciaId() {
		return ServicioInstanciaId;
	}
	public void setServicioInstanciaId(int servicioInstanciaId) {
		ServicioInstanciaId = servicioInstanciaId;
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
	public String getKey() {
		if(Key == null){
			Key = fechaInicio + "a" + 
					String.valueOf(EmpleadoRevision) + "a" + 
					String.valueOf(Tipo) + "a" +
					String.valueOf(ServicioId) + "a";
		}
		return Key;
	}
	public void setKey(String key) {
		Key = key;
	}
	public int getEmpleadoRevision() {
		return EmpleadoRevision;
	}
	public void setEmpleadoRevision(int empleadoRevision) {
		EmpleadoRevision = empleadoRevision;
	}
	public String getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(String fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public String getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	
	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();

		values.put(DbServiceInstance.CN_SERVICE_CONFIGURATION, ServicioConfiguracionId);
		values.put(DbServiceInstance.CN_ACTIVITY_TYPE, Tipo);
		values.put(DbServiceInstance.CN_SERVICE, ServicioId);
		values.put(DbServiceInstance.CN_KEY, this.getKey());
		values.put(DbServiceInstance.CN_EMPLOYEEREVIEW, EmpleadoRevision);
		if(fechaInicio != null){
			values.put(DbServiceInstance.CN_START_DATETIME, fechaInicio);
		}else{
			values.putNull(DbServiceInstance.CN_START_DATETIME);
		}			
		if(fechaFin != null){
			values.put(DbServiceInstance.CN_FINISH_DATETIME, fechaFin);
		}else{
			values.putNull(DbServiceInstance.CN_FINISH_DATETIME);
		}
		if(Status != null){
			values.put(DbServiceInstance.CN_STATUS, Status);
		}else{
			values.putNull(DbServiceInstance.CN_STATUS);
		}
		if(ComentariosCheckList != null){
			values.put(DbServiceInstance.CN_REVIEW_COMMENT, ComentariosCheckList);
		}else{
			values.putNull(DbServiceInstance.CN_REVIEW_COMMENT);
		}
		if(ComentariosElementos != null){
			values.put(DbServiceInstance.CN_EMPLOYEE_COMMENT, ComentariosElementos);
		}else{
			values.putNull(DbServiceInstance.CN_EMPLOYEE_COMMENT);
		}
		if(ComentariosInventario != null){
			values.put(DbServiceInstance.CN_INVENTORY_COMMENT, ComentariosInventario);
		}else{
			values.putNull(DbServiceInstance.CN_INVENTORY_COMMENT);
		}
		
		return values ;
	}
	
	public void fillFromCursor(Cursor c) {
		int index = c.getColumnIndex(DbServiceInstance._ID);
		if(index != -1){
			ServicioInstanciaId = c.getInt(index);
		}
		index = c.getColumnIndex(DbServiceInstance.CN_ACTIVITY_TYPE);
		if(index != -1){
			Tipo = c.getInt(index);
		}
		index = c.getColumnIndex(DbServiceInstance.CN_SERVICE_CONFIGURATION);
		if(index != -1){
			ServicioConfiguracionId = c.getInt(index);
		}
		index = c.getColumnIndex(DbServiceInstance.CN_SERVICE);
		if(index != -1){
			ServicioId = c.getInt(index);
		}
		index = c.getColumnIndex(DbServiceInstance.CN_KEY);
		if(index != -1){
			Key = c.getString(index);
		}
		index = c.getColumnIndex(DbServiceInstance.CN_EMPLOYEEREVIEW);
		if(index != -1){
			EmpleadoRevision = c.getInt(index);
		}
		index = c.getColumnIndex(DbServiceInstance.CN_START_DATETIME);
		if(index != -1){
			fechaInicio = c.getString(index);
		}
		index = c.getColumnIndex(DbServiceInstance.CN_FINISH_DATETIME);
		if(index != -1){
			fechaFin = c.getString(index);
		}
		index = c.getColumnIndex(DbServiceInstance.CN_STATUS);
		if(index != -1){
			Status = c.getString(index);
		}		
		index = c.getColumnIndex(DbServiceInstance.CN_INVENTORY_COMMENT);
		if(index != -1){
			ComentariosInventario = c.getString(index);
		}		
		index = c.getColumnIndex(DbServiceInstance.CN_REVIEW_COMMENT);
		if(index != -1){
			ComentariosCheckList = c.getString(index);
		}		
		index = c.getColumnIndex(DbServiceInstance.CN_EMPLOYEE_COMMENT);
		if(index != -1){
			ComentariosElementos = c.getString(index);
		}		
	}
}

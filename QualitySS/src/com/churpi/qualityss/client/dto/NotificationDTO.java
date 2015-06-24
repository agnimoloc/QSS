package com.churpi.qualityss.client.dto;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbNotification;

import android.content.ContentValues;

public class NotificationDTO {
	int NotificacionId;
	String Titulo;
	int ServicioId;
	int ElementoId;
	String Mensaje;
	int Estatus;
	int Importancia;
	int Tipo;
	public int getNotificacionId() {
		return NotificacionId;
	}
	public void setNotificacionId(int notificacionId) {
		NotificacionId = notificacionId;
	}
	public String getTitulo() {
		return Titulo;
	}
	public void setTitulo(String titulo) {
		Titulo = titulo;
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
	public String getMensaje() {
		return Mensaje;
	}
	public void setMensaje(String mensaje) {
		Mensaje = mensaje;
	}
	public int getEstatus() {
		return Estatus;
	}
	public void setEstatus(int estatus) {
		Estatus = estatus;
	}
	public int getImportancia() {
		return Importancia;
	}
	public void setImportancia(int importancia) {
		Importancia = importancia;
	}
	public int getTipo() {
		return Tipo;
	}
	public void setTipo(int tipo) {
		Tipo = tipo;
	}
	
	
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(DbNotification.CN_TITLE, Titulo);
		values.put(DbNotification.CN_SERVICE, ServicioId);
		if(ElementoId != 0){
			values.put(DbNotification.CN_EMPLOYEE, ElementoId);
		}
		values.put(DbNotification.CN_MESSAGE, Mensaje);
		values.put(DbNotification.CN_STATUS, Estatus);
		values.put(DbNotification.CN_PRIORITY, Importancia);
		values.put(DbNotification.CN_TYPE, Tipo);
		return values;
	}
}

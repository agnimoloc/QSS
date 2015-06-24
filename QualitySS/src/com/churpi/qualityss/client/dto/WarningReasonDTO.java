package com.churpi.qualityss.client.dto;

public class WarningReasonDTO {
	private int MotivoAmonestacionId;
	private String Descripcion;
	public int getMotivoAmonestacionId() {
		return MotivoAmonestacionId;
	}
	public void setMotivoAmonestacionId(int motivoAmonestacionId) {
		MotivoAmonestacionId = motivoAmonestacionId;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	
}

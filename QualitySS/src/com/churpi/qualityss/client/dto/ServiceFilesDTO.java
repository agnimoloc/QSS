package com.churpi.qualityss.client.dto;

import java.util.Map;

public class ServiceFilesDTO {
	private int ServicioId;
	private String Code;
	private Map<String, String> ArchivosReferencia;
	public int getServicioId() {
		return ServicioId;
	}
	public void setServicioId(int servicioId) {
		ServicioId = servicioId;
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public Map<String, String> getArchivosReferencia() {
		return ArchivosReferencia;
	}
	public void setArchivosReferencia(Map<String, String> archivosReferencia) {
		ArchivosReferencia = archivosReferencia;
	}
	
	
	
	
}

package com.churpi.qualityss.client.dto;

public class SectionDTO {
	int PaseRevistaSeccionId;
	String Descripcion;
	Float Valor;

	public int getPaseRevistaSeccionId() {
		return PaseRevistaSeccionId;
	}
	public void setPaseRevistaSeccionId(int paseRevistaSeccionId) {
		PaseRevistaSeccionId = paseRevistaSeccionId;
	}
	public Float getValor() {
		return Valor;
	}
	public void setValor(Float valor) {
		Valor = valor;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}	
}

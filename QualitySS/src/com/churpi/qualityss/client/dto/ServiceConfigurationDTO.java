package com.churpi.qualityss.client.dto;

public class ServiceConfigurationDTO {
	int ServicioConfiguracionId;
	String Titulo;
	int Tipo;
	SurveyDTO Examen;
	EquipmentDTO[] ServicioEquipo;
	ReviewDTO[] PaseRevistaServicio;
	ReviewDTO[] PaseRevistaElemento;
	
	public int getServicioConfiguracionId() {
		return ServicioConfiguracionId;
	}
	public void setServicioConfiguracionId(int servicioConfiguracionId) {
		ServicioConfiguracionId = servicioConfiguracionId;
	}
	public String getTitulo() {
		return Titulo;
	}
	public void setTitulo(String titulo) {
		Titulo = titulo;
	}
	public int getTipo() {
		return Tipo;
	}
	public void setTipo(int tipo) {
		Tipo = tipo;
	}
	public SurveyDTO getExamen() {
		return Examen;
	}
	public void setExamen(SurveyDTO examen) {
		Examen = examen;
	}
	public EquipmentDTO[] getServicioEquipo() {
		return ServicioEquipo;
	}
	public void setServicioEquipo(EquipmentDTO[] servicioEquipo) {
		ServicioEquipo = servicioEquipo;
	}
	public ReviewDTO[] getPaseRevistaServicio() {
		return PaseRevistaServicio;
	}
	public void setPaseRevistaServicio(ReviewDTO[] paseRevistaServicio) {
		PaseRevistaServicio = paseRevistaServicio;
	}
	public ReviewDTO[] getPaseRevistaElemento() {
		return PaseRevistaElemento;
	}
	public void setPaseRevistaElemento(ReviewDTO[] paseRevistaElemento) {
		PaseRevistaElemento = paseRevistaElemento;
	}
	
	
}

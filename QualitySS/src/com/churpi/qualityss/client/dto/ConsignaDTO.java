package com.churpi.qualityss.client.dto;

public class ConsignaDTO {
	int Id;
	String Name;
	ConsignaDetalleDTO[] Detalle;
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public ConsignaDetalleDTO[] getDetalle() {
		return Detalle;
	}
	public void setDetalle(ConsignaDetalleDTO[] detalle) {
		Detalle = detalle;
	}

}

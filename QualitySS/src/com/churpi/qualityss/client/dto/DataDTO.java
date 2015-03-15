package com.churpi.qualityss.client.dto;

public class DataDTO {
	String changeset;
	CustomerDTO[] Clientes;
	SectorDTO[] Sectores;
	EmployeeDTO[] Elementos;
	ServiceDTO[] Servicios;

	public ServiceDTO[] getServicios() {
		return Servicios;
	}

	public void setServicios(ServiceDTO[] servicios) {
		Servicios = servicios;
	}

	public String getChangeset() {
		return changeset;
	}

	public void setChangeset(String changeset) {
		this.changeset = changeset;
	}

	public CustomerDTO[] getClientes() {
		return Clientes;
	}

	public void setClientes(CustomerDTO[] clientes) {
		Clientes = clientes;
	}

	public EmployeeDTO[] getElementos() {
		return Elementos;
	}

	public void setElementos(EmployeeDTO[] elementos) {
		Elementos = elementos;
	}

	public SectorDTO[] getSectores() {
		return Sectores;
	}

	public void setSectores(SectorDTO[] sectores) {
		Sectores = sectores;
	}	
	
	
	
}

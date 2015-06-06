package com.churpi.qualityss.client.dto;

public class DataDTO {
	String changeset;
	String ImageBaseUrl;
	StateDTO[] Estados;
	TownDTO[] Municipios;
	CustomerDTO[] Clientes;
	SectorDTO[] Sectores;
	EmployeeDTO[] Elementos;
	ServiceDTO[] Servicios;
	SectionDTO[] Secciones;
	ServiceTypeDTO[] TiposServicio;
	NotificationDTO[] Notificaciones;
	WarningReasonDTO[] MotivoAmonestacion;
	HREmployeeDTO[] RepresentantesRH;
		
	public HREmployeeDTO[] getRepresentantesRH() {
		return RepresentantesRH;
	}

	public void setRepresentantesRH(HREmployeeDTO[] representantesRH) {
		RepresentantesRH = representantesRH;
	}

	public WarningReasonDTO[] getMotivoAmonestacion() {
		return MotivoAmonestacion;
	}

	public void setMotivoAmonestacion(WarningReasonDTO[] motivoAmonestacion) {
		MotivoAmonestacion = motivoAmonestacion;
	}

	public NotificationDTO[] getNotificaciones() {
		return Notificaciones;
	}

	public void setNotificaciones(NotificationDTO[] notificaciones) {
		Notificaciones = notificaciones;
	}

	public ServiceTypeDTO[] getTiposServicio() {
		return TiposServicio;
	}

	public void setTiposServicio(ServiceTypeDTO[] tiposServicio) {
		TiposServicio = tiposServicio;
	}

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

	public SectionDTO[] getSecciones() {
		return Secciones;
	}

	public void setSecciones(SectionDTO[] secciones) {
		Secciones = secciones;
	}

	public String getImageBaseUrl() {
		return ImageBaseUrl;
	}

	public void setImageBaseUrl(String imageBaseUrl) {
		ImageBaseUrl = imageBaseUrl;
	}

	public StateDTO[] getEstados() {
		return Estados;
	}

	public void setEstados(StateDTO[] estados) {
		Estados = estados;
	}

	public TownDTO[] getMunicipios() {
		return Municipios;
	}

	public void setMunicipios(TownDTO[] municipios) {
		Municipios = municipios;
	}	
	
	
	
}

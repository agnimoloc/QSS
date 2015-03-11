package com.churpi.qualityss.client.dto;

public class DataDTO {
	UserDTO[] users;
	ConsignaDTO[] consignas;
	ServiceDTO[] services;
	GeneralCheckpointDTO[] generalchecklist;
	
	public UserDTO[] getUsers() {
		return users;
	}
	public void setUsers(UserDTO[] users) {
		this.users = users;
	}
	public ConsignaDTO[] getConsignas() {
		return consignas;
	}
	public void setConsignas(ConsignaDTO[] consignas) {
		this.consignas = consignas;
	}
	public ServiceDTO[] getServices() {
		return services;
	}
	public void setServices(ServiceDTO[] services) {
		this.services = services;
	}
	public GeneralCheckpointDTO[] getGeneralchecklist() {
		return generalchecklist;
	}
	public void setGeneralchecklist(GeneralCheckpointDTO[] generalchecklist) {
		this.generalchecklist = generalchecklist;
	}
	

}

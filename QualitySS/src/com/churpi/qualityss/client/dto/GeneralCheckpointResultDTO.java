package com.churpi.qualityss.client.dto;

public class GeneralCheckpointResultDTO {
	int Id;
	int ServiceId;
	int GeneralCheckpointId;
	String Comment;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getServiceId() {
		return ServiceId;
	}
	public void setServiceId(int serviceId) {
		ServiceId = serviceId;
	}
	public int getGeneralCheckpointId() {
		return GeneralCheckpointId;
	}
	public void setGeneralCheckpointId(int generalCheckpointId) {
		GeneralCheckpointId = generalCheckpointId;
	}
	public String getComment() {
		return Comment;
	}
	public void setComment(String comment) {
		Comment = comment;
	}
	
	
}

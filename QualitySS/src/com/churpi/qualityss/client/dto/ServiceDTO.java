package com.churpi.qualityss.client.dto;

public class ServiceDTO {
	int Id;
	String Type;
	String Address;
	String Contact;
	String Phone;
	String Description;
	EmployeeDTO[] Staff;
	public int getId() {
		return Id;
	}
	public void setId(int Id) {
		this.Id = Id;
	}
	public String getType() {
		return Type;
	}
	public void setType(String Type) {
		this.Type = Type;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String Address) {
		this.Address = Address;
	}
	public String getContact() {
		return Contact;
	}
	public void setContact(String Contact) {
		this.Contact = Contact;
	}
	public String getPhone() {
		return Phone;
	}
	public void setPhone(String Phone) {
		this.Phone = Phone;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String Description) {
		this.Description = Description;
	}
	public EmployeeDTO[] getStaff() {
		return Staff;
	}
	public void setStaff(EmployeeDTO[] staff) {
		Staff = staff;
	}
	
}

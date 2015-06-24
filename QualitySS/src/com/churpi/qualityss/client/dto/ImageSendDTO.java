package com.churpi.qualityss.client.dto;

import java.io.File;

public class ImageSendDTO implements IFileSend {
	File file;
	String response;
	String Message;
	
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
}

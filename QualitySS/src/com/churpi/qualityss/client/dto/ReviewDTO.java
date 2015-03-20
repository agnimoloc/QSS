package com.churpi.qualityss.client.dto;

public class ReviewDTO {
	int PaseRevistaId;
	float Value;
	int PaseRevistaSeccionId;
	QuestionDTO[] Preguntas;
	public int getPaseRevisteId() {
		return PaseRevistaId;
	}
	public void setPaseRevisteId(int paseRevisteId) {
		PaseRevistaId = paseRevisteId;
	}
	public float getValue() {
		return Value;
	}
	public void setValue(float value) {
		Value = value;
	}
	
	public int getPaseRevistaSeccionId() {
		return PaseRevistaSeccionId;
	}
	public void setPaseRevistaSeccionId(int paseRevistaSeccionId) {
		PaseRevistaSeccionId = paseRevistaSeccionId;
	}
	public int getPaseRevistaId() {
		return PaseRevistaId;
	}
	public void setPaseRevistaId(int paseRevistaId) {
		PaseRevistaId = paseRevistaId;
	}
	public QuestionDTO[] getPreguntas() {
		return Preguntas;
	}
	public void setPreguntas(QuestionDTO[] preguntas) {
		Preguntas = preguntas;
	}
	
}

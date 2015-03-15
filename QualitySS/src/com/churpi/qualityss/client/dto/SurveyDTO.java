package com.churpi.qualityss.client.dto;

public class SurveyDTO {
	int ExamenId;
	float Valor;
	QuestionDTO[] Preguntas;
	public int getExamenId() {
		return ExamenId;
	}
	public void setExamenId(int examenId) {
		ExamenId = examenId;
	}
	public float getValor() {
		return Valor;
	}
	public void setValor(float valor) {
		Valor = valor;
	}
	public QuestionDTO[] getPreguntas() {
		return Preguntas;
	}
	public void setPreguntas(QuestionDTO[] preguntas) {
		Preguntas = preguntas;
	}
	
}

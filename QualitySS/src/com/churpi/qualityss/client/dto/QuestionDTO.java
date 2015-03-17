package com.churpi.qualityss.client.dto;

import android.database.Cursor;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswer;

public class QuestionDTO {
	int PreguntaId;
	String Descripcion;
	float Valor;
	String NombreSeccion;
	String Resultado;
	
	public void fillFromCursor(Cursor c) {
		int index = c.getColumnIndex(DbQuestion._ID);
		if(index != -1){
			PreguntaId = c.getInt(index);
		}
		index = c.getColumnIndex(DbQuestion.CN_DESCRIPTION);
		if(index != -1){
			Descripcion = c.getString(index);
		}
		index = c.getColumnIndex(DbQuestion.CN_VALUE);
		if(index != -1){
			Valor = c.getFloat(index);
		}
		index = c.getColumnIndex(DbReviewQuestion.CN_SECTION_NAME);
		if(index != -1){
			NombreSeccion = c.getString(index);
		}
		index = c.getColumnIndex(DbReviewQuestionAnswer.CN_RESULT);
		if(index != -1){
			Resultado = c.getString(index);
		}
	}
	
	public int getPreguntaId() {
		return PreguntaId;
	}
	public void setPreguntaId(int preguntaId) {
		PreguntaId = preguntaId;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	public float getValor() {
		return Valor;
	}
	public void setValor(float valor) {
		Valor = valor;
	}
	public String getNombreSeccion() {
		return NombreSeccion;
	}

	public void setNombreSeccion(String nombreSeccion) {
		NombreSeccion = nombreSeccion;
	}

	public String getResultado() {
		return Resultado;
	}

	public void setResultado(String resultado) {
		Resultado = resultado;
	}
	
	
}

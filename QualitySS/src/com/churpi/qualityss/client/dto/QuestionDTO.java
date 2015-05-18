package com.churpi.qualityss.client.dto;

import android.database.Cursor;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestion;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerEmployee;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbReviewQuestionAnswerService;

public class QuestionDTO {
	int PreguntaId;
	String Descripcion;
	float Valor;
	String NombreSeccion;
	String Resultado;
	String Comentarios;
	
	boolean isResultado = false;
	boolean isComentario = false;
	
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
		index = c.getColumnIndex(DbReviewQuestionAnswerEmployee.CN_RESULT);
		if(index != -1){
			Resultado = c.getString(index);
		}else{
			index = c.getColumnIndex(DbReviewQuestionAnswerService.CN_RESULT);
			if(index != -1){
				Resultado = c.getString(index);
			}
		}
		index = c.getColumnIndex(DbReviewQuestionAnswerEmployee.CN_COMMENT);
		if(index != -1){
			Comentarios = c.getString(index);
		}else{
			index = c.getColumnIndex(DbReviewQuestionAnswerService.CN_COMMENT);
			if(index != -1){
				Comentarios = c.getString(index);
			}
		}
	}

	public boolean isComentario(){
		return isComentario;
	}
	public String getComentarios() {
		return Comentarios;
	}
	public void setComentarios(String comentarios) {
		isComentario = true;
		isResultado = false;
		Comentarios = comentarios;
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
	public boolean isResultado(){
		return isResultado;
	}
	public String getResultado() {
		return Resultado;
	}
	public void setResultado(String resultado) {
		isResultado = true;
		isComentario = false;
		Resultado = resultado;
	}
	
	
}

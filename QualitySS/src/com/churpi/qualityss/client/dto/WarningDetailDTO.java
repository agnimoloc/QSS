package com.churpi.qualityss.client.dto;

import java.io.Serializable;

import android.database.Cursor;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbWarningDetail;

public class WarningDetailDTO  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4558697720454381654L;
	private int id;
	private int WarningId;
	private int WarningReasonId;
	private String WarningReasonDesc;
	private String Descripcion;	
	
	public String getWarningReasonDesc() {
		return WarningReasonDesc;
	}
	public void setWarningReasonDesc(String warningReasonDesc) {
		WarningReasonDesc = warningReasonDesc;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getWarningId() {
		return WarningId;
	}
	public void setWarningId(int warningId) {
		WarningId = warningId;
	}
	public int getWarningReasonId() {
		return WarningReasonId;
	}
	public void setWarningReasonId(int warningReasonId) {
		WarningReasonId = warningReasonId;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	
	public void fillFromCursor(Cursor c){
		int index = c.getColumnIndex(DbWarningDetail._ID);
		if(index != -1){
			id = c.getInt(index);
		}
		index = c.getColumnIndex(DbWarningDetail.CN_NOTE);
		if(index != -1){
			Descripcion = c.getString(index);
		}
		index = c.getColumnIndex(DbWarningDetail.CN_WARNING);
		if(index != -1){
			WarningId = c.getInt(index);
		}
		index = c.getColumnIndex(DbWarningDetail.CN_WARNING_REASON);
		if(index != -1){
			WarningReasonId = c.getInt(index);
		}
		
	}
}

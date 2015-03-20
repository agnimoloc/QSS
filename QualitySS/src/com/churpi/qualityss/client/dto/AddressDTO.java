package com.churpi.qualityss.client.dto;

import com.churpi.qualityss.client.db.QualitySSDbContract.DbAddress;
import com.churpi.qualityss.client.db.QualitySSDbContract.DbService;

import android.content.ContentValues;
import android.database.Cursor;

public class AddressDTO {
	int DomicilioId;
	String Calle;
	String NoExt;
	String NoInt;
	String Colonia;
	int CodigoPostal;
	int EstadoId;
	String NombreEstado;
	int MunicipioId;
	String NombreMunicipio;
	String EntreCalle1;
	String EntreCalle2;
	String Telefono1;
	String Telefono2;
	
	public int getDomicilioId() {
		return DomicilioId;
	}
	public void setDomicilioId(int domicilioId) {
		DomicilioId = domicilioId;
	}
	public String getCalle() {
		return Calle;
	}
	public void setCalle(String calle) {
		Calle = calle;
	}
	public String getNoExt() {
		return NoExt;
	}
	public void setNoExt(String noExt) {
		NoExt = noExt;
	}
	public String getNoInt() {
		return NoInt;
	}
	public void setNoInt(String noInt) {
		NoInt = noInt;
	}
	public String getColonia() {
		return Colonia;
	}
	public void setColonia(String colonia) {
		Colonia = colonia;
	}
	public int getCodigoPostal() {
		return CodigoPostal;
	}
	public void setCodigoPostal(int codigoPostal) {
		CodigoPostal = codigoPostal;
	}
	public int getEstadoId() {
		return EstadoId;
	}
	public void setEstadoId(int estadoId) {
		EstadoId = estadoId;
	}
	public int getMunicipioId() {
		return MunicipioId;
	}
	public void setMunicipioId(int municipioId) {
		MunicipioId = municipioId;
	}
	public String getEntreCalle1() {
		return EntreCalle1;
	}
	public void setEntreCalle1(String entreCalle1) {
		EntreCalle1 = entreCalle1;
	}
	public String getEntreCalle2() {
		return EntreCalle2;
	}
	public void setEntreCalle2(String entreCalle2) {
		EntreCalle2 = entreCalle2;
	}
	public String getTelefono1() {
		return Telefono1;
	}
	public void setTelefono1(String telefono1) {
		Telefono1 = telefono1;
	}
	public String getTelefono2() {
		return Telefono2;
	}
	public void setTelefono2(String telefono2) {
		Telefono2 = telefono2;
	}
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(DbAddress.CN_STREET, Calle);
		values.put(DbAddress.CN_NO_EXT, NoExt);
		values.put(DbAddress.CN_NO_INT, NoInt);
		values.put(DbAddress.CN_COLONY, Colonia);
		values.put(DbAddress.CN_POSTAL, CodigoPostal);
		values.put(DbAddress.CN_STATE, EstadoId);
		values.put(DbAddress.CN_TOWN, MunicipioId);
		values.put(DbAddress.CN_REF_1, EntreCalle1);
		values.put(DbAddress.CN_REF_2, EntreCalle2);
		values.put(DbAddress.CN_PHONE_1, Telefono1);
		values.put(DbAddress.CN_PHONE_2, Telefono2);
		return values;
	}
	public void fillFromCursor(Cursor c) {
		int index = c.getColumnIndex(DbAddress._ID);
		if(index != -1){
			DomicilioId = c.getInt(index);
		}
		index = c.getColumnIndex(DbAddress.CN_STREET);
		if(index != -1){
			Calle = c.getString(index);
		}
		index = c.getColumnIndex(DbAddress.CN_NO_EXT);
		if(index != -1){
			NoExt = c.getString(index);
		}
		index = c.getColumnIndex(DbAddress.CN_NO_INT);
		if(index != -1){
			NoInt = c.getString(index);
		}
		index = c.getColumnIndex(DbAddress.CN_COLONY);
		if(index != -1){
			Colonia = c.getString(index);
		}
		index = c.getColumnIndex(DbAddress.CN_POSTAL);
		if(index != -1){
			CodigoPostal = c.getInt(index);
		}
		index = c.getColumnIndex(DbAddress.CN_STATE);
		if(index != -1){
			EstadoId = c.getInt(index);
		}
		index = c.getColumnIndex(DbAddress.CN_STATE_NAME);
		if(index != -1){
			NombreEstado = c.getString(index);
		}
		index = c.getColumnIndex(DbAddress.CN_TOWN);
		if(index != -1){
			MunicipioId = c.getInt(index);
		}
		index = c.getColumnIndex(DbAddress.CN_TOWN_NAME);
		if(index != -1){
			NombreMunicipio = c.getString(index);
		}
		index = c.getColumnIndex(DbAddress.CN_REF_1);
		if(index != -1){
			EntreCalle1 = c.getString(index);
		}
		index = c.getColumnIndex(DbAddress.CN_REF_2);
		if(index != -1){
			EntreCalle2 = c.getString(index);
		}
		index = c.getColumnIndex(DbAddress.CN_PHONE_1);
		if(index != -1){
			Telefono1 = c.getString(index);
		}
		index = c.getColumnIndex(DbAddress.CN_PHONE_1);
		if(index != -1){
			Telefono2 = c.getString(index);
		}
		
		
	}
	public String getDomicilioString() {
		StringBuilder sb = new StringBuilder(Calle);
		if(NoExt != null  && NoExt.length() > 0){
			sb.append(" #");
			sb.append(NoExt);
		}
		if(NoInt != null  && NoInt.length() > 0){
			sb.append(" int ");
			sb.append(NoInt);
		}		
		if(Colonia != null  && Colonia.length() > 0){
			sb.append(" col. ");
			sb.append(Colonia);
		}
		if(NombreMunicipio != null  && NombreMunicipio.length() > 0){
			sb.append(", ");
			sb.append(NombreMunicipio);
		}
		if(NombreEstado != null  && NombreEstado.length() > 0){
			sb.append(", ");
			sb.append(NombreEstado);
		}
		sb.append("\n");
		if(EntreCalle1 != null && EntreCalle1.length() > 0){
			sb.append("entre: ");
			sb.append(EntreCalle1);
		}

		if(EntreCalle2 != null && EntreCalle2.length() > 0){
			sb.append(" y ");
			sb.append(EntreCalle2);
		}
		sb.append("\n");

		if(Telefono1 != null && Telefono1.length() > 0){
			sb.append("tel: ");
			sb.append(Telefono1);
		}
		if(Telefono2 != null && Telefono2.length() > 0){
			sb.append(", ");
			sb.append(Telefono2);
		}

		
		return sb.toString();
	}
	public String getMapsAddress() {
		StringBuilder sb = new StringBuilder(Calle);
		if(NoExt != null  && NoExt.length() > 0){
			sb.append(" #");
			sb.append(NoExt);
		}		
		if(Colonia != null  && Colonia.length() > 0){
			sb.append(", ");
			sb.append(Colonia);
		}
		if(NombreMunicipio != null  && NombreMunicipio.length() > 0){
			sb.append(", ");
			sb.append(String.valueOf(CodigoPostal));
			sb.append(" ");			
			sb.append(NombreMunicipio);
		}
		if(NombreEstado != null  && NombreEstado.length() > 0){
			sb.append(",");
			sb.append(NombreEstado);
		}
		sb.append(",México");
		return sb.toString();
	}
	
	
}

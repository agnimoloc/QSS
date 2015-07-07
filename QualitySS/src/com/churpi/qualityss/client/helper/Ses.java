package com.churpi.qualityss.client.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Ses {
	
	private static final String PREFERENCES = "QSSPREF";
	public static final String PREF_FILLED_DB = "FILLEDDB";
	public static final String PREF_ACCOUNT = "CURRENT_ACCOUNT";
	public static final String PREF_PASSHASH = "CURRENT_PASSHASH";
	public static final String PREF_EMPLOYEE = "CURRENT_EMPLOYEE";
	public static final String PREF_CHANGESET = "CURRENT_CHANGESET";
	public static final String PREF_IMAGEURL = "IMAGE_URL";
	public static final String PREF_ACTIVITY = "CURRENT_ACTIVITY";
	public static final String PREF_ACTIVITY_TYPE = "ACTIVITY_ID";
	public static final String PREF_SECTOR_ID = "SECTOR_ID";
	public static final String PREF_SERVICE_ID = "SERVICE_ID";
	public static final String PREF_SERVICE_DESC = "SERVICE_DESC";
	public static final String PREF_EMPLOYEE_ID = "EMPLOYEE_ID";
	public static final String PREF_EMPLOYEE_INSTANCE_ID = "EMPLOYEE_INSTANCE_ID";
	public static final String PREF_EMPLOYEE_NAME = "EMPLOYEE_NAME";
	public static final String PREF_SERVICE_INSTANCE_ID = "SERVICE_INSTANCE_ID";
	public static final String PREF_SERVICE_INSTANCE_KEY = "SERVICE_INSTANCE_KEY";
	public static final String PREF_PERMISSIONS = "PERMISSIONS";
	
	private String account;
	private int passHashcode;
	private int employee;
	private int activityType;
	private int sectorId;
	private int serviceId;
	private int serviceInstanceId;
	private int employeeId;
	private int employeeInstanceId;
	private String changeset;
	private String imgURL;
	private String serviceDescription;
	private String employeeName;
	private String serviceInstanceKey;
	private String permissions;
	private boolean filledDb;	
	
	
	public String getPermissions() {
		return permissions;
	}
	public Ses setPermissions(String permissions) {
		putString(PREF_PERMISSIONS, permissions);
		this.permissions = permissions;
		return this;
	}
	public int getEmployeeInstanceId() {
		return employeeInstanceId;
	}
	public Ses setEmployeeInstanceId(int employeeInstanceId) {
		putInt(PREF_EMPLOYEE_INSTANCE_ID, employeeInstanceId);
		this.employeeInstanceId = employeeInstanceId;
		return this;
	}
	public String getServiceInstanceKey() {
		return serviceInstanceKey;
	}
	public Ses setServiceInstanceKey(String serviceInstanceKey) {
		putString(PREF_SERVICE_INSTANCE_KEY, serviceInstanceKey);
		this.serviceInstanceKey = serviceInstanceKey;
		return this;
	}
	public int getServiceInstanceId() {
		return serviceInstanceId;
	}
	public Ses setServiceInstanceId(int serviceInstanceId) {
		putInt(PREF_SERVICE_INSTANCE_ID, serviceInstanceId);
		this.serviceInstanceId = serviceInstanceId;
		return this;
	}
	public int getEmployeeId() {
		return employeeId;
	}
	public Ses setEmployeeId(int employeeId) {
		putInt(PREF_EMPLOYEE_ID, employeeId);
		this.employeeId = employeeId;
		return this;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public Ses setEmployeeName(String employeeName) {
		putString(PREF_EMPLOYEE_NAME, employeeName);
		this.employeeName = employeeName;
		return this;
	}
	public String getServiceDescription() {
		return serviceDescription;
	}
	public Ses setServiceDescription(String serviceDescription) {
		putString(PREF_SERVICE_DESC, serviceDescription);
		this.serviceDescription = serviceDescription;
		return this;
	}
	public int getServiceId() {
		return serviceId;
	}
	public Ses setServiceId(int serviceId) {
		putInt(PREF_SERVICE_ID, sectorId);
		this.serviceId = serviceId;
		return this;
	}
	public int getSectorId() {
		return sectorId;
	}
	public Ses setSectorId(int sectorId) {
		putInt(PREF_SECTOR_ID, sectorId);
		this.sectorId = sectorId;
		return this;
	}
	public boolean isFilledDb() {
		return filledDb;
	}
	public Ses setFilledDb(boolean filledDb) {
		putBool(PREF_FILLED_DB, filledDb);
		this.filledDb = filledDb;
		return this;
	}
	public String getChangeset() {
		return changeset;
	}
	public Ses setChangeset(String changeset) {
		putString(PREF_CHANGESET, changeset);
		this.changeset = changeset;
		return this;
	}
	public String getImgURL() {
		return imgURL;
	}	
	public Ses setImgURL(String imgURL) {
		putString(PREF_IMAGEURL, imgURL);
		this.imgURL = imgURL;
		return this;
	}
	public int getActivityType() {
		return activityType;
	}
	public Ses setActivityType(int activityType) {
		putInt(PREF_ACTIVITY_TYPE,activityType);
		this.activityType = activityType;
		return this;
	}
	public String getAccount() {
		return account;
	}
	public Ses setAccount(String account) {		
		putString(PREF_ACCOUNT,account);
		this.account = account;
		return this;
	}

	public int getPassHashcode() {
		return passHashcode;
	}

	public Ses setPassHashcode(int passHashcode) {
		putInt(PREF_PASSHASH, passHashcode);
		this.passHashcode = passHashcode;
		return this;
	}

	public int getEmployee() {
		return employee;
	}

	public Ses setEmployee(int employee) {
		putInt(PREF_EMPLOYEE, employee);
		this.employee = employee;
		return this;
	}
	
	
	private static Ses instance = null;
	
	private Context context = null;
	private Editor editor = null;
	
	public Ses(Context context){
		this.context = context;
		initValues();
	}
	
	private void initValues(){
		SharedPreferences pref = getPref();
		account = pref.getString(PREF_ACCOUNT, null);
		passHashcode = pref.getInt(PREF_PASSHASH, -1);
		employee = pref.getInt(PREF_EMPLOYEE, -1);
		activityType = pref.getInt(PREF_ACTIVITY_TYPE, -1);
		changeset = pref.getString(PREF_CHANGESET, null);
		imgURL = pref.getString(PREF_IMAGEURL, null);
		filledDb = pref.getBoolean(PREF_FILLED_DB, false);
		sectorId = pref.getInt(PREF_SECTOR_ID, -1);
		serviceId = pref.getInt(PREF_SERVICE_ID, -1);
		serviceDescription = pref.getString(PREF_SERVICE_DESC, null);
		employeeId = pref.getInt(PREF_EMPLOYEE_ID, -1);
		employeeName = pref.getString(PREF_EMPLOYEE_NAME, null);
		serviceInstanceId = pref.getInt(PREF_SERVICE_INSTANCE_ID, -1);
		serviceDescription = pref.getString(PREF_SERVICE_DESC, null);
		permissions = pref.getString(PREF_PERMISSIONS, null);
	}
	
	private SharedPreferences getPref(){
		return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
	}
	
	public Ses edit(){
		if(editor != null){
			editor.clear();
			editor = null;
		}		
		SharedPreferences pref = getPref();
		editor = pref.edit();
		return this;
	}
	
	private Editor getEditor(){
		SharedPreferences pref = getPref();
		return pref.edit();
	}
	
	
	private void putString(String key, String value){
		if(editor != null){
			editor.putString(key, value);
		}else{
			getEditor().putString(key, value).commit();
		}
	}
	private void putInt(String key, int value){
		if(editor != null){
			editor.putInt(key, value);
		}else{
			getEditor().putInt(key, value).commit();
		}
	}
	private void putBool(String key, boolean value){
		if(editor != null){
			editor.putBoolean(key, value);
		}else{
			getEditor().putBoolean(key, value).commit();
		}
	}
	
	public boolean commit(){
		if(editor != null){
			boolean value = editor.commit();
			editor = null;
			return value;
		}
		return false;
	}
		
	public static synchronized Ses getInstance(Context context){
		if(instance == null){
			instance = new Ses(context);
		}		
		return instance;
	}
	
}

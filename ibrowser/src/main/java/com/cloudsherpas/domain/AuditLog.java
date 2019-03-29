package com.cloudsherpas.domain;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import com.cloudsherpas.dao.BaseEntity;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class AuditLog extends BaseEntity {
	
	@Persistent
	private String email;
	
	@Persistent
	private String title;
	
	@Persistent
	private String code;
	
	@Persistent
	private String groupCode;
	
	@Persistent
	private String heading; 
	
	@Persistent
	private String type; 
	
	@Persistent
	private String deletion; 
	
	@Persistent
	private String frequency; 
	
	@Persistent
	private Date reportDate;
	
	@Persistent
	private Date viewDate;

	@Persistent
	private String deviceType;
	
	@Persistent
	private  String status;
	
	@Persistent
	private  String fileName;
	
	@Persistent
	private  String fileSize;
	
	@Persistent
	private  String userBranch;
	
	@Persistent
	private  Text userGroups;
	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTitle() {	
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDeletion() {
		return deletion;
	}

	public void setDeletion(String deletion) {
		this.deletion = deletion;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Date getViewDate() {
		return viewDate;
	}

	public void setViewDate(Date viewDate) {
		this.viewDate = viewDate;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUserBranch() {
		return userBranch;
	}

	public void setUserBranch(String userBranch) {
		this.userBranch = userBranch;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getUserGroups() {
		return userGroups.getValue();
	}

	public void setUserGroups(String userGroups) {
		this.userGroups = new Text(userGroups);
	}   

}

package com.cloudsherpas.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.cloudsherpas.dao.BaseEntity;
import com.cloudsherpas.enums.ReportPeriodEnum;

@PersistenceCapable
public class PatchInstance extends BaseEntity{
	
	@Persistent
	private String reportCode;
	
	@Persistent
	private String groupCode;
	
	@Persistent
	private Date date;
	
	@Persistent
	private ReportPeriodEnum period;
	
	@Persistent
	private String fileName;
	
	@Persistent
	private String fileSize;
	
	public String getReportCode() {
		return reportCode;
	}

	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ReportPeriodEnum getPeriod() {
		return period;
	}
	public void setPeriod(ReportPeriodEnum period) {
		this.period = period;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	
}

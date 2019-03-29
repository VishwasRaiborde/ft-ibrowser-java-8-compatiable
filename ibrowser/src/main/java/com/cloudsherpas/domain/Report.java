package com.cloudsherpas.domain;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.cloudsherpas.dao.BaseEntity;
import com.cloudsherpas.enums.DeletionPeriodEnum;
import com.cloudsherpas.enums.FrequencyEnum;
import com.cloudsherpas.enums.ReportTypeEnum;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable
public class Report extends BaseEntity {
	
	@Persistent
	private String code;
	
	@Persistent
	private String title;

	@Persistent
	private String description;
	
	@Persistent(defaultFetchGroup = "true")
	@Unowned
	private Type type;	//for many to one relation with Type entity
	
	@Persistent
	private ReportTypeEnum reportType; //this is for only filter

	@NotPersistent
	private String typeAsString;
	
	@Persistent(defaultFetchGroup = "true")
	@Unowned
	private Heading heading;	//for many to one relation with Heading entity
	
	@Persistent
	private FrequencyEnum frequency;
	
	@Persistent
	private DeletionPeriodEnum deletionPeriod;
	
	@Persistent(defaultFetchGroup = "true")
	@Unowned
	public List<GoogleGroup> allowedGroups;

	@Persistent
	private List<Key> allowedGroupsKey;

	@Persistent(defaultFetchGroup = "true")
	@Unowned
	public List<GoogleGroup> deniedGroups;

	@Persistent
	private List<Key> deniedGroupsKey;

	@Persistent
	private String headingAsString;
	
	@Persistent
	private Boolean hasInstance = false;
	
	@NotPersistent
	private List<FrequencyEnum> frequencyList;
	
	@Persistent
	private String deletionPeriodAsString;
	
	@NotPersistent
	private List<ReportTypeEnum> typeList;
	
	@NotPersistent
	private List<GoogleGroup> groupList;
	
	@NotPersistent
	private List<DeletionPeriodEnum> deletionPeriods;
	
	@NotPersistent
	private List<Heading> headingList;
	
	@NotPersistent
	private String lastReportDateAsString;
	
	@NotPersistent
	private String popupString;
	
	@NotPersistent
	private String groupCode;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
		this.reportType = type != null ? type.getType() : null;
	}

	public ReportTypeEnum getReportType() {
		return reportType;
	}

	public void setReportType(ReportTypeEnum reportType) {
		this.reportType = reportType;
	}

	public String getTypeAsString() {
		return typeAsString;
	}

	public void setTypeAsString(String typeAsString) {
		this.typeAsString = typeAsString;
	}

	public Heading getHeading() {
		return heading;
	}

	public void setHeading(Heading heading) {
		this.heading = heading;
	}

	public FrequencyEnum getFrequency() {
		return frequency;
	}

	public void setFrequency(FrequencyEnum frequency) {
		this.frequency = frequency;
	}

	public DeletionPeriodEnum getDeletionPeriod() {
		return deletionPeriod;
	}

	public void setDeletionPeriod(DeletionPeriodEnum deletionPeriod) {
		this.deletionPeriod = deletionPeriod;
	}

	public List<GoogleGroup> getAllowedGroups() {
		return allowedGroups;
	}

	public void setAllowedGroups(List<GoogleGroup> allowedGroups) {
		this.allowedGroups = allowedGroups;
		
		if (allowedGroups != null && !allowedGroups.isEmpty()) {
			ArrayList<Key> keys = new ArrayList<Key>();
			for (GoogleGroup ag : allowedGroups) {
				keys.add(ag.getKey());
			}
			this.allowedGroupsKey = keys;
		} else {
			this.allowedGroupsKey = null;
		}
	}

	public List<Key> getAllowedGroupsKey() {
		return allowedGroupsKey;
	}

	public List<GoogleGroup> getDeniedGroups() {
		return deniedGroups;
	}

	public void setDeniedGroups(List<GoogleGroup> deniedGroups) {
		this.deniedGroups = deniedGroups;

		if (deniedGroups != null && !deniedGroups.isEmpty()) {
			ArrayList<Key> keys = new ArrayList<Key>();
			for (GoogleGroup ag : deniedGroups) {
				keys.add(ag.getKey());
			}
			this.deniedGroupsKey = keys;
		} else {
			this.deniedGroupsKey = null;
		}
	}
	

	public List<Key> getDeniedGroupsKey() {
		return deniedGroupsKey;
	}

	public List<FrequencyEnum> getFrequencyList() {
		return frequencyList;
	}

	public void setFrequencyList(List<FrequencyEnum> frequencyList) {
		this.frequencyList = frequencyList;
	}

	public List<ReportTypeEnum> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<ReportTypeEnum> typeList) {
		this.typeList = typeList;
	}

	public List<GoogleGroup> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<GoogleGroup> groupList) {
		this.groupList = groupList;
	}

	public List<DeletionPeriodEnum> getDeletionPeriods() {
		return deletionPeriods;
	}

	public void setDeletionPeriods(List<DeletionPeriodEnum> deletionPeriods) {
		this.deletionPeriods = deletionPeriods;
	}

	public List<Heading> getHeadingList() {
		return headingList;
	}

	public void setHeadingList(List<Heading> headingList) {
		this.headingList = headingList;
	}
	public String getLastReportDateAsString(){
		return lastReportDateAsString;
	}
	public void setLastReportDateAsString(String lastReportDateAsString){
		this.lastReportDateAsString = lastReportDateAsString;
	}
	
	/*public String getCreatedDateAsString() {
		return createdDate != null ? DateUtils.dateFormat1.format(createdDate) : "";
	}
	public String getDateToString() {
		return createdDate != null ? DateUtils.getDateToString(createdDate) : "";
	}*/

	public String getDeletionPeriodAsString() {
		return deletionPeriodAsString;
	}
	public void setDeletionPeriodAsString(String deletionPeriodAsString) {
		this.deletionPeriodAsString = deletionPeriodAsString;
	}

	public String getHeadingAsString() {
		return headingAsString;
	}
	public void setHeadingAsString(String headingAsString) {
		this.headingAsString = headingAsString;
	}

	public String getPopupString() {
		return popupString;
	}

	public void setPopupString(String popupString) {
		this.popupString = popupString;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
  	
}

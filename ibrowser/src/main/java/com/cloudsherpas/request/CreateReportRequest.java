package com.cloudsherpas.request;

import java.util.Date;

public class CreateReportRequest {

  public String key, code, title, description, type, heading, frequency, deletionPeriod,
      allowedGroups, deniedGroups, headingAsString;
  public String sortField, order, step, headingKey, groupName, groupCode;
  public Date createdDate, lastUpdatedDate;
  public Boolean isFavourite;

  public CreateReportRequest() {
    super();
  }

  public CreateReportRequest(String key, String code, String title, String description, String type,
      String heading, String frequency, String deletionPeriod, String allowedGroups,
      String deniedGroups, String sortField, String order, String step, String headingKey,
      String groupName, String groupCode, Boolean isFavourite, String headingAsString) {
    super();
    this.key = key;
    this.code = code;
    this.title = title;
    this.description = description;
    this.type = type;
    this.heading = heading;
    this.frequency = frequency;
    this.deletionPeriod = deletionPeriod;
    this.allowedGroups = allowedGroups;
    this.deniedGroups = deniedGroups;
    this.sortField = sortField;
    this.order = order;
    this.step = step;
    this.headingKey = headingKey;
    this.groupName = groupName;
    this.groupCode = groupCode;
    this.isFavourite = isFavourite;
    this.headingAsString = headingAsString;
  }
}

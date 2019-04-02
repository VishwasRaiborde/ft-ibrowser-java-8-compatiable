package com.cloudsherpas.request;

public class CreateTypeRequest {
  public String key;
  public String name;
  public String group;
  public String groupCode;
  public String sortField;
  public String order;
  public String step;

  public CreateTypeRequest() {
    super();
  }

  public CreateTypeRequest(String key, String name, String group, String groupCode,
      String sortField, String order, String step) {
    super();
    this.key = key;
    this.name = name;
    this.group = group;
    this.groupCode = groupCode;
    this.sortField = sortField;
    this.order = order;
    this.step = step;
  }
}

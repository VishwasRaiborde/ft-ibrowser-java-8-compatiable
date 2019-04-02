package com.cloudsherpas.request;

public class SearchRequest {
  public String code;
  public String title;
  public String groupCode;
  public String reportType;

  public SearchRequest() {
    super();
  }

  public SearchRequest(String code, String title, String groupCode, String reportType) {
    super();
    this.code = code;
    this.title = title;
    this.groupCode = groupCode;
    this.reportType = reportType;
  }

}

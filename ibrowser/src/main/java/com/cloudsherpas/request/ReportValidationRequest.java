package com.cloudsherpas.request;

public class ReportValidationRequest {
  public String key;
  public String code;

  public ReportValidationRequest() {
    super();
  }

  public ReportValidationRequest(String key, String code) {
    this.key = key;
    this.code = code;
  }
}

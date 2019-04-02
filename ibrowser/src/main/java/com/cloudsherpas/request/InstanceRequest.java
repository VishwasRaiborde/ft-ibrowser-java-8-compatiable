package com.cloudsherpas.request;

public class InstanceRequest {
  public String code;
  public String groupCode;
  public String url;

  public InstanceRequest() {
    super();
  }

  public InstanceRequest(String code, String groupCode, String url) {
    super();
    this.code = code;
    this.groupCode = groupCode;
    this.url = url;
  }

}

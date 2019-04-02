package com.cloudsherpas.request;

public class EntityDeleteRequest {
  public String key;
  public String entity;

  public EntityDeleteRequest() {
    super();
  }

  public EntityDeleteRequest(String key, String entity) {
    super();
    this.key = key;
    this.entity = entity;
  }
}

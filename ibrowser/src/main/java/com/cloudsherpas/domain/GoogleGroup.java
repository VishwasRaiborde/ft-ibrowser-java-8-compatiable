package com.cloudsherpas.domain;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.cloudsherpas.dao.BaseEntity;

@PersistenceCapable
public class GoogleGroup extends BaseEntity {

  @Persistent
  private String name;

  @Persistent
  private String code;

  @Persistent
  private String email;

  @Persistent
  private String description;

  @Persistent
  private Boolean isSystem = Boolean.FALSE;

  @NotPersistent
  private Boolean allowedGroup = Boolean.FALSE;

  @NotPersistent
  private Boolean deniedGroup = Boolean.FALSE;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getIsSystem() {
    return isSystem;
  }

  public void setIsSystem(Boolean isSystem) {
    this.isSystem = isSystem;
  }

  public Boolean getAllowedGroup() {
    return allowedGroup;
  }

  public void setAllowedGroup(Boolean allowedGroup) {
    this.allowedGroup = allowedGroup;
  }

  public Boolean getDeniedGroup() {
    return deniedGroup;
  }

  public void setDeniedGroup(Boolean deniedGroup) {
    this.deniedGroup = deniedGroup;
  }
}

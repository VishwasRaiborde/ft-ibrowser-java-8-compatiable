package com.cloudsherpas.domain;

import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.cloudsherpas.dao.BaseEntity;
import com.cloudsherpas.enums.ReportTypeEnum;

@PersistenceCapable
public class Type extends BaseEntity {

  @Persistent
  private ReportTypeEnum type;

  @Persistent
  private String groupName;

  @Persistent
  private String groupCode;

  @NotPersistent
  private List<ReportTypeEnum> typeList;

  @NotPersistent
  private String nameAsString;

  public ReportTypeEnum getType() {
    return type;
  }

  public void setType(ReportTypeEnum type) {
    this.type = type;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getGroupCode() {
    return groupCode;
  }

  public void setGroupCode(String groupCode) {
    this.groupCode = groupCode;
  }

  public List<ReportTypeEnum> getTypeList() {
    return typeList;
  }

  public void setTypeList(List<ReportTypeEnum> typeList) {
    this.typeList = typeList;
  }

  public String getNameAsString() {
    if (nameAsString == null && type != null) {
      nameAsString = type.getName();
    }
    return nameAsString;
  }

  public void setNameAsString(String nameAsString) {
    this.nameAsString = nameAsString;
  }

}

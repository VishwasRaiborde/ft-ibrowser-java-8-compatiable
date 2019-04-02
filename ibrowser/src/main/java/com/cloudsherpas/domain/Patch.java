package com.cloudsherpas.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.cloudsherpas.dao.BaseEntity;

@SuppressWarnings("serial")
@PersistenceCapable
public class Patch extends BaseEntity {

  @Persistent
  private Date lastAfterDate;

  @Persistent
  private long lastStart;

  public Date getLastAfterDate() {
    return lastAfterDate;
  }

  public void setLastAfterDate(Date lastAfterDate) {
    this.lastAfterDate = lastAfterDate;
  }

  public long getLastStart() {
    return lastStart;
  }

  public void setLastStart(long lastStart) {
    this.lastStart = lastStart;
  }

}

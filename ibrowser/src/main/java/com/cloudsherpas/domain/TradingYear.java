package com.cloudsherpas.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.cloudsherpas.dao.BaseEntity;

@PersistenceCapable
public class TradingYear extends BaseEntity {

  @Persistent
  private Integer tradingYear;

  @Persistent
  private Integer numberOfWeek;

  @Persistent
  private Date startDate;

  @Persistent
  private Date endDate;

  @NotPersistent
  private List<Integer> weekList;

  @NotPersistent
  private String dateAsString;

  public Integer getTradingYear() {
    return tradingYear;
  }

  public void setTradingYear(Integer tradingYear) {
    this.tradingYear = tradingYear;
  }

  public Integer getNumberOfWeek() {
    return numberOfWeek;
  }

  public void setNumberOfWeek(Integer numberOfWeek) {
    this.numberOfWeek = numberOfWeek;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public List<Integer> getWeekList() {
    return weekList;
  }

  public void setWeekList(List<Integer> weekList) {
    this.weekList = weekList;
  }

  public String getDateAsString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    if (dateAsString == null && startDate != null) {
      dateAsString = dateFormat.format(startDate);
    }
    return dateAsString;
  }

  public void setDateAsString(String dateAsString) {
    this.dateAsString = dateAsString;
  }

}

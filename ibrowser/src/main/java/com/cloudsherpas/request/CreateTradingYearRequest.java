package com.cloudsherpas.request;

import java.util.Date;

public class CreateTradingYearRequest {
  
  public String key;
  public Integer tradingYear;
  public Integer numberOfWeek;
  public Date startDate;
  public String startDateAsString;
  
  public String sortField;
  public String order;
  public String step;
  
  public CreateTradingYearRequest(){
	  super();
  }
  
	public CreateTradingYearRequest(String key,Integer tradingYear,Integer numberOfWeek,Date startDate,String sortField,String order,String step){
		super();
		this.key = key;
		this.tradingYear = tradingYear;
		this.numberOfWeek = numberOfWeek;
		this.startDate = startDate;
		this.sortField = sortField;
		this.order = order;
		this.step = step;
	}
	
	public CreateTradingYearRequest(String key,Integer tradingYear,Integer numberOfWeek,String startDateAsString){
		super();
		this.key = key;
		this.tradingYear = tradingYear;
		this.numberOfWeek = numberOfWeek;
		this.startDateAsString = startDateAsString;
	}
 
}

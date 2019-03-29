package com.cloudsherpas.http.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.TradingYearDao;
import com.cloudsherpas.domain.TradingYear;
import com.cloudsherpas.request.CreateTradingYearRequest;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.cloudsherpas.utils.DateUtils;
import com.cloudsherpas.utils.StrUtils;
import com.google.inject.Inject;

public class TradingYearActionHandler {
	private final TradingYearDao tradingYearDao;
	private final ApiHttpUtils utils;
	
	@Inject
	public TradingYearActionHandler(TradingYearDao tradingYearDao, ApiHttpUtils utils){
		this.tradingYearDao = tradingYearDao;
		this.utils = utils;
	}
	public String get(String key ,HttpServletRequest req, HttpServletResponse resp) throws IOException {
		TradingYear tradingYear = null;
		
		if (!StrUtils.isEmpty(key)) {
			tradingYear = tradingYearDao.get(key);
			String startDate = DateUtils.dateFormat1.format(tradingYear.getStartDate());
			tradingYear.setDateAsString("Sunday,"+startDate);			
		} else {
			tradingYear = new TradingYear();
		}
		List<Integer> weekList = new ArrayList<Integer>();
		weekList.add(52);
		weekList.add(53);
		tradingYear.setWeekList(weekList);
		
		req.setAttribute("tradingYear", tradingYear);
		return "OK";
	}
	public String post(String key, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			CreateTradingYearRequest hr = (CreateTradingYearRequest)utils.readPost(req, CreateTradingYearRequest.class);
			TradingYear tradingYear = null;
			if (!StrUtils.isEmpty(key)) {
				tradingYear = tradingYearDao.get(key);
			}
			if (tradingYear == null) {
				tradingYear = new TradingYear();
			}
			Date dateFromString = DateUtils.getDateFromString(hr.startDateAsString);
			TradingYear previousYear = tradingYearDao.getTradingYear(hr.tradingYear - 1);
			if (previousYear != null) {
				Date date = (Date) previousYear.getStartDate().clone();
				date = DateUtils.addDaysToDate(date, previousYear.getNumberOfWeek() * 7);
				
				if (!date.equals(dateFromString)) {
					utils.sendResponse(resp, "NOT_EQUAL_DATES");
					return null;
				}
			}
			tradingYear.setTradingYear(hr.tradingYear);
			tradingYear.setNumberOfWeek(hr.numberOfWeek);
			tradingYear.setStartDate(dateFromString);
			tradingYear.setDateAsString(hr.startDateAsString);
			
			tradingYearDao.persist(tradingYear);
			return "SUCCESS";
		} catch(Exception e){
			e.printStackTrace();
		  utils.sendError(resp, 500, "An unexpected exception occurred: " + e.getClass().getSimpleName() + ": " + e.getMessage());
		  return null;	
		}
	}
}

package com.cloudsherpas.http.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.TradingYearDao;
import com.cloudsherpas.domain.TradingYear;
import com.cloudsherpas.request.CreateTradingYearRequest;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.google.inject.Inject;

public class TradingYearHandler {
  
	private final TradingYearDao tradingYearDao;
	private final ApiHttpUtils utils;
	
	@Inject
	public TradingYearHandler(TradingYearDao tradingYearDao ,ApiHttpUtils utils){
		this.tradingYearDao = tradingYearDao;
	    this.utils = utils;	
	}
	
	public String get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		List<TradingYear> list = tradingYearDao.getEntityList();
		Integer listCount = tradingYearDao.getEntityCount();
		req.setAttribute("content_holder", "admin/tradingYear.html");
		req.setAttribute("tradingYears", list);
		req.setAttribute("count", listCount);
		return "OK";
	}
	public void post(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		CreateTradingYearRequest cr = (CreateTradingYearRequest)utils.readPost(req, CreateTradingYearRequest.class);
		List<TradingYear> list = tradingYearDao.getEntitiesByParam(cr.sortField, cr.order,cr.step);
		//List<TradingYear> list = tradingYearDao.getTradingYearsByParam(cr.sortField, cr.order,cr.step);
		if (list != null && !list.isEmpty()) {
			for (TradingYear trading : list) {//TODO
				trading.setKeyAsString();
			}
		}
		utils.sendResponse(resp, list);
	}
}

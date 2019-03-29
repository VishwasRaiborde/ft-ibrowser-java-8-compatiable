package com.cloudsherpas.http.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.ReportDao;
import com.cloudsherpas.domain.Report;
import com.cloudsherpas.request.CreateReportRequest;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.google.inject.Inject;

public class ReportHandler {

	private final ReportDao reportDao;
	private final ApiHttpUtils utils;
	
	@Inject
	public ReportHandler(ReportDao reportDao, ApiHttpUtils utils) {
		this.reportDao = reportDao;
		this.utils = utils;
	}
	
	public String get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		List<Report> list = reportDao.getEntityList();
		if (list != null && !list.isEmpty()) {
			for (Report report : list) {
				report.setKeyAsString();
				report.setTypeAsString(report.getReportType().getName());
			}
		}
		Integer listCount = reportDao.getEntityCount();
		req.setAttribute("content_holder", "admin/report.html");
		req.setAttribute("reports", list);
		req.setAttribute("count", listCount);
		return "OK";
	}

	public void post(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		CreateReportRequest hr = (CreateReportRequest)utils.readPost(req, CreateReportRequest.class);
		List<Report> list = reportDao.getEntitiesByParam(hr.sortField, hr.order,hr.step);
		if (list != null && !list.isEmpty()) {
			for (Report report : list) {
				report.setKeyAsString();
				report.setTypeAsString(report.getReportType().getName());
			}
		}
		utils.sendResponse(resp, list);
	}
	
}
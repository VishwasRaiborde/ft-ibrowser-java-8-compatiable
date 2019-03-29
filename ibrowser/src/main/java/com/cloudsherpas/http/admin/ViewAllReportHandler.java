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

public class ViewAllReportHandler {

	private final ReportDao reportDao;
	private final ApiHttpUtils utils;
	
	@Inject
	public ViewAllReportHandler(ReportDao reportDao, ApiHttpUtils utils) {
		this.reportDao = reportDao;
		this.utils = utils;
	}
	
	public String get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		List<Report> list = reportDao.getAllEntityList();
		if (list != null && !list.isEmpty()) {
			for (Report report : list) {
				report.setKeyAsString();
				report.setTypeAsString(report.getReportType().getName());
			}
		}
		req.setAttribute("content_holder", "admin/viewAllReport.html");
		req.setAttribute("reports", list);
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
package com.cloudsherpas.http.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.cloudsherpas.dao.ReportDao;
import com.cloudsherpas.domain.Report;
import com.cloudsherpas.request.ReportValidationRequest;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.inject.Inject;

public class ReportValidationHandler {

	private final ReportDao reportDao;
    private final ApiHttpUtils utils;
	
    @Inject
	public ReportValidationHandler(ReportDao reportDao,ApiHttpUtils utils) {
		this.reportDao = reportDao;		
		this.utils = utils;
	}
	
	public void get(String key,String code, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
	}
	
	public void post(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ReportValidationRequest vr = (ReportValidationRequest) utils.readPost(
				req, ReportValidationRequest.class);
		String key = vr.key;
		String code = vr.code;
		if ("".equals(key) && code != null) {
			Report report = reportDao.getReportsByCode(code);
			if (report == null) {
				utils.sendResponse(resp, true);		
			}else{
				utils.sendResponse(resp, false);		
			}
		} else if(!"".equals(key) && code!=null) {
			Report editableReport = reportDao.getReportsByCode(code);
			if (editableReport != null) {
				if (KeyFactory.keyToString(editableReport.getKey()).equals(key)) {
					utils.sendResponse(resp, true);
				} else {
					utils.sendResponse(resp, false);
				}
			} else {
				utils.sendResponse(resp, true);
			}
		}
		
	}
}

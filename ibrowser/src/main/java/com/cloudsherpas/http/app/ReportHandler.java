package com.cloudsherpas.http.app;

import java.io.IOException;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.HeadingDao;
import com.cloudsherpas.dao.ReportDao;
import com.cloudsherpas.dao.UserDao;
import com.cloudsherpas.dao.UserFavouriteReportDao;
import com.cloudsherpas.domain.AppUser;
import com.cloudsherpas.domain.Heading;
import com.cloudsherpas.domain.Report;
import com.cloudsherpas.domain.UserFavouriteReport;
import com.cloudsherpas.enums.ReportTypeEnum;
import com.cloudsherpas.request.CreateReportRequest;
import com.cloudsherpas.responses.SelectItem;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.google.inject.Inject;

public class ReportHandler{
	
	
	private final ReportDao reportDao;
	private final UserDao userDao;
	private final HeadingDao headingDao;
	private final UserFavouriteReportDao userFavouriteReportDao;
	private final ApiHttpUtils utils;
	private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
	
	@Inject
	public ReportHandler(ReportDao reportDao,UserDao userDao,HeadingDao headingDao,UserFavouriteReportDao userFavouriteReportDao,ApiHttpUtils utils) {
		this.reportDao = reportDao;
		this.userDao = userDao;
		this.headingDao = headingDao;
		this.userFavouriteReportDao = userFavouriteReportDao;
		this.utils = utils;
	}
	
	public String get(String headingKey, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		logger.log(Level.INFO,"ReportHandler Start:  Time: "+ new Date());
		AppUser currentUser = userDao.getCurrentUser();
		List<Report> divisionalReports = reportDao.getDivisionalReport(headingKey);
		logger.log(Level.INFO,"divisionalReports :  Time: "+ new Date());
		
		List<SelectItem> userBranches = reportDao.getUserBranches();
	    logger.log(Level.INFO,"branchGroups :  Time: "+ new Date());
	    
	    List<SelectItem> userBuyingOffices = reportDao.getUserBuyingOffices();
	    logger.log(Level.INFO,"buyingGroups :  Time: "+ new Date());
	    
	    Heading heading = headingDao.get(headingKey);
	    List<UserFavouriteReport> userFavouriteReports = userFavouriteReportDao.getUserFavouriteReports(currentUser.getKeyAsString());
	    req.setAttribute("content_holder", "app/report.html");
		req.setAttribute("divisionalReports", divisionalReports);
		req.setAttribute("branchGroups", userBranches);	
		req.setAttribute("buyingGroups", userBuyingOffices);	
		req.setAttribute("userFavouriteReports", userFavouriteReports);	
			
		req.setAttribute("heading", heading);	
		
	    logger.log(Level.INFO,"userBranch :  Time: "+ new Date());

	    // a branch user
	    if (userBranches != null && !userBranches.isEmpty() && userBranches.size()==1) {
	    	req.setAttribute("userBranch", userBranches.get(0));
	    	req.setAttribute("branchReport", reportDao.getReports(headingKey,ReportTypeEnum.BRANCH, userBranches.get(0).getKey()));
	    } 
	    if (currentUser.getBuyingOfficeCode() != null && !"".equals(currentUser.getBuyingOfficeCode())) {
	    	req.setAttribute("userSavedBO", new SelectItem(currentUser.getBuyingOfficeCode(), currentUser.getBuyingOfficeCode()));
	    	req.setAttribute("buyingReport", reportDao.getReports(headingKey, ReportTypeEnum.BUYING, currentUser.getBuyingOfficeCode()));
	    }
		return "OK";
	}
	
	public void post(String headingKey, HttpServletRequest req,HttpServletResponse resp) throws IOException {
//		logger.log(Level.INFO,"On ReportHandler Post");
		CreateReportRequest reportReq = (CreateReportRequest)utils.readPost(req, CreateReportRequest.class);
		List<Report> reports = reportDao.getReports(headingKey, ReportTypeEnum.buildByCode(reportReq.type), reportReq.key);
		
		int retries = 0; 
		while(reports.size() < 1 && retries<10) {
			reports = reportDao.getReports(headingKey, ReportTypeEnum.buildByCode(reportReq.type), reportReq.key);
			retries++;
		}
		
		try {
			if (ReportTypeEnum.BUYING.getCode().equals(reportReq.type)) {
				AppUser user = userDao.getCurrentUser();
				user.setBuyingOfficeCode(reportReq.key);
				userDao.persist(user);
			}
		} catch(Exception e) {
			logger.log(Level.INFO,"Failed to get user ");
		}
		
		
		utils.sendResponse(resp, reports);
	}	
}

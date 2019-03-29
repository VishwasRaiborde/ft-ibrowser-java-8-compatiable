package com.cloudsherpas.http.app;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.dao.AuditDao;
import com.cloudsherpas.dao.GroupDao;
import com.cloudsherpas.dao.InstanceDao;
import com.cloudsherpas.dao.ReportDao;
import com.cloudsherpas.dao.TradingYearDao;
import com.cloudsherpas.dao.UserDao;
import com.cloudsherpas.domain.AppUser;
import com.cloudsherpas.domain.AuditLog;
import com.cloudsherpas.domain.GoogleGroup;
import com.cloudsherpas.domain.Instance;
import com.cloudsherpas.domain.Report;
import com.cloudsherpas.domain.TradingYear;
import com.cloudsherpas.enums.FrequencyEnum;
import com.cloudsherpas.enums.ReportPeriodEnum;
import com.cloudsherpas.google.api.CloudStorageSignedUrl;
import com.cloudsherpas.google.api.GoogleCloudStorageApi;
import com.cloudsherpas.http.UserAgentInfo;
import com.cloudsherpas.model.ReportHistory;
import com.cloudsherpas.request.InstanceRequest;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.cloudsherpas.utils.DateUtils;
import com.cloudsherpas.utils.StrUtils;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;

public class ReportDatesHandler {

	private final TradingYearDao tradingYearDao;
	private final InstanceDao instanceDao;
	private final ReportDao reportDao;
	private final UserDao userDao;
	private final AuditDao auditDao;
	private final GroupDao groupDao;
	private ApiHttpUtils utils;
	private final MemcacheService cache;
	private final DateUtils dateUtils;
	private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

	@Inject
	public ReportDatesHandler(InstanceDao instanceDao, ReportDao reportDao,
			UserDao userDao, TradingYearDao tradingYearDao,AuditDao auditDao,GroupDao groupDao, ApiHttpUtils utils,
			MemcacheService cache, DateUtils dateUtils) {
		this.tradingYearDao = tradingYearDao;
		this.instanceDao = instanceDao;
		this.reportDao = reportDao;
		this.userDao = userDao;
		this.auditDao = auditDao;
		this.groupDao = groupDao;
		this.utils = utils;
		this.cache = cache;
		this.dateUtils = dateUtils;
	}

	public void get(String id, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		if (id == null || id.isEmpty()) {
			logger.log(Level.INFO,"Report id is NULL");
			resp.sendRedirect("/error.html");
		}
		Key instanceKey = KeyFactory.createKey("Instance", Long.parseLong(id));
		Instance instance = instanceDao.get(instanceKey);
        AppUser currentUser = userDao.getCurrentUser();
		//Audit log
		Report report  = reportDao.getReportsByCode(instance.getReportCode());
		if(report!=null){
			logger.log(Level.INFO,"Report : "+ report.getCode());
			UserAgentInfo userAgent = new UserAgentInfo(req);
			String deviceType =  userAgent.getDeviceType();
			AuditLog auditLog = new AuditLog();
			auditLog.setTitle(report.getTitle());
			auditLog.setCode(report.getCode());
			auditLog.setGroupCode(instance.getGroupCode());
			auditLog.setHeading(report.getHeadingAsString());
			auditLog.setType(report.getReportType().getName());
			auditLog.setDeletion(report.getDeletionPeriodAsString());
			auditLog.setFrequency(report.getFrequency().toString());
			auditLog.setEmail(userDao.getCurrentUser().getEmail());
			auditLog.setViewDate(new Date());
			auditLog.setReportDate(instance.getDate());
			auditLog.setDeviceType(deviceType);
			auditLog.setFileName(instance.getFileName());
			auditLog.setFileSize(instance.getFileSize());
			auditLog.setUserBranch((String)cache.get(currentUser.getEmail()));
			if(!currentUser.getGroups().isEmpty()){
				StringBuffer groups = new StringBuffer();
				for(String group : currentUser.getGroups()){
					groups.append(group).append(GlobalConstants.DELIMITER);
				}
				auditLog.setUserGroups(groups.toString());
			}
			
			auditDao.persist(auditLog);
		}
		if (report != null && isAllowReportToUser(report, currentUser)) {
			
			GoogleCloudStorageApi gcsApi = new GoogleCloudStorageApi();
			gcsApi.init();
			String fileFullPath = "/" + instance.getGroupCode() + "/"+ instance.getReportCode() + "/"+ instance.getFileName();
			String filePath  = StrUtils.removeStringFirstChar(fileFullPath);
			Boolean  hasFile =  gcsApi.hasFileInBucket(GlobalConstants.REPORTS_BUCKET,filePath);
			if(hasFile){
				CloudStorageSignedUrl storageSignedUrl = new CloudStorageSignedUrl(fileFullPath);
				resp.setHeader("Content-Disposition: inline application", "filename="+fileFullPath);
				resp.sendRedirect(storageSignedUrl.getSignedUrl());	
			}else{
				resp.sendRedirect("/noFile.html");			
			}				
		}else {
			notAllowedReport(resp);
		}
				
	}

	public boolean isAllowReportToUser(Report report, AppUser user) {
		logger.log(Level.INFO," Start checking isAllowReportToUser: USER: " +user.getEmail());
		if(user.getGroups()==null){
			logger.log(Level.INFO," user.getGroups: NULL");
		}else {
			logger.log(Level.INFO," user.getGroups: "   +user.getGroups());
		} 
		for (GoogleGroup group : report.getAllowedGroups()) {
			   group = groupDao.get(group.getKey());
			logger.log(Level.INFO,"Report Group: "+group.getName());
			if (user.getGroups() != null && user.getGroups().contains(group.getName())) {
				logger.log(Level.INFO," isAllowReportToUser: TRUE");
				return true;
			}
		}
		logger.log(Level.INFO," isAllowReportToUser: FALSE" );
	    return false;
	}
	
	public static void notAllowedReport(HttpServletResponse resp) throws IOException {
		resp.sendRedirect("/noAccess.html");		
	}

	

	@SuppressWarnings("static-access")
	public void post(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		ReportHistory result = new ReportHistory();
		
		// fill instance request parameters from http request
		InstanceRequest instanceRequest = (InstanceRequest) utils.readPost(req,InstanceRequest.class);
		
		// get current trading year
		TradingYear currentYear = tradingYearDao.getTradingYearByDate(DateUtils.convertDateToCalendar(new Date()).getTime());
//		DateUtils dateUtils = new DateUtils(currentYear.getStartDate());
		
		// get report data by given report code
		Report report = reportDao.getReportByCode(instanceRequest.code);
		result.setFrequency(report.getFrequency());
		
		if (instanceRequest != null && "simple".equals(instanceRequest.url)) {
			//initialize report histories
			List<Instance> currentWeekPeriodDate = instanceDao.getLastInstances(instanceRequest.code, instanceRequest.groupCode, FrequencyEnum.DAILY.equals(report.getFrequency()) ? ReportPeriodEnum.DAILY : ReportPeriodEnum.WEEKLY);
			
			if (currentWeekPeriodDate != null && !currentWeekPeriodDate.isEmpty()) {
				result.getHistories().addAll(currentWeekPeriodDate);
				Instance item = currentWeekPeriodDate.get(0);
				
				if (FrequencyEnum.WEEKLY.equals(result.getFrequency()) && item.getDate().compareTo(dateUtils.getLast7DaysPeriod()[0]) != 1 && item.getDate().compareTo(dateUtils.getLast7DaysPeriod()[1]) != -1) {
					result.setLastWeek(item);
					result.getHistories().remove(item);
				} else if(FrequencyEnum.DAILY.equals(result.getFrequency()) && item.getDate().compareTo(dateUtils.convertDateToCalendar(new Date()).getTime()) == 0){
					result.setToday(item);
					result.getHistories().remove(item);
				}
			}
			
			List<Instance> lastTradingPeriodDates = instanceDao.getLastTradingPeriodInstances(instanceRequest.code, instanceRequest.groupCode, ReportPeriodEnum.END_OF_TRADING_PERIOD, dateUtils.getLastTradingPeriodDate());
			
			if (lastTradingPeriodDates != null && !lastTradingPeriodDates.isEmpty()) {
				result.setLastTradingPeriod(lastTradingPeriodDates.get(0));
			}
			
			List<Instance> lastHalfDates = instanceDao.getLastTradingPeriodInstances(instanceRequest.code, instanceRequest.groupCode, ReportPeriodEnum.END_OF_HALF, dateUtils.getLastHalfDate());
			
			if (lastHalfDates != null && !lastHalfDates.isEmpty()) {
				result.setLastHalf(lastHalfDates.get(0));
			}
			
			List<Instance> lastYearDates = instanceDao.getLastTradingPeriodInstances(instanceRequest.code, instanceRequest.groupCode, ReportPeriodEnum.END_OF_YEAR, dateUtils.getLastYearDate());
			
			if (lastYearDates != null && !lastYearDates.isEmpty()) {
				result.setLastYear(lastYearDates.get(0));
			}
			
		} else {
			List<Instance> reportHistories = instanceDao.getReportDates(instanceRequest.code, instanceRequest.groupCode, FrequencyEnum.DAILY.equals(report.getFrequency()) ? ReportPeriodEnum.DAILY : ReportPeriodEnum.WEEKLY);
			result.setHistories(reportHistories);

			//last week report logic initialize, this is for only weekly reports
			if (reportHistories != null && !reportHistories.isEmpty()) {
				Instance item = reportHistories.get(0);
				
				if (FrequencyEnum.WEEKLY.equals(result.getFrequency()) && item.getDate().compareTo(dateUtils.getLast7DaysPeriod()[0]) != 1 && item.getDate().compareTo(dateUtils.getLast7DaysPeriod()[1]) != -1) {
					result.setLastWeek(item);
					result.getHistories().remove(item);
				} else if(FrequencyEnum.DAILY.equals(result.getFrequency()) && item.getDate().compareTo(dateUtils.convertDateToCalendar(new Date()).getTime()) == 0){
					result.setToday(item);
					result.getHistories().remove(item);
				}
			}

			List<Instance> reportTradingPeriodHistories = instanceDao.getReportDates(instanceRequest.code, instanceRequest.groupCode, ReportPeriodEnum.END_OF_TRADING_PERIOD);
			result.setTradingPeriods(reportTradingPeriodHistories);
			
			List<Instance> reportEndOfHalfHistories = instanceDao.getReportDates(instanceRequest.code, instanceRequest.groupCode, ReportPeriodEnum.END_OF_HALF);
			result.setHalfs(reportEndOfHalfHistories);

			List<Instance> reportEndOfYearHistories = instanceDao.getReportDates(instanceRequest.code, instanceRequest.groupCode, ReportPeriodEnum.END_OF_YEAR);
			result.setYears(reportEndOfYearHistories);
		}
		utils.sendResponse(resp, result);
	}
}

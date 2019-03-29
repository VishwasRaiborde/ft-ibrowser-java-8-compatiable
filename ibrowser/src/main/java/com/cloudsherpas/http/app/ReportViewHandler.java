package com.cloudsherpas.http.app;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.dao.AuditDao;
import com.cloudsherpas.dao.GroupDao;
import com.cloudsherpas.dao.InstanceDao;
import com.cloudsherpas.dao.ReportDao;
import com.cloudsherpas.dao.UserDao;
import com.cloudsherpas.domain.AppUser;
import com.cloudsherpas.domain.AuditLog;
import com.cloudsherpas.domain.GoogleGroup;
import com.cloudsherpas.domain.Instance;
import com.cloudsherpas.domain.Report;
import com.cloudsherpas.enums.EntityTransferStatus;
import com.cloudsherpas.google.api.CloudStorageSignedUrl;
import com.cloudsherpas.google.api.GoogleCloudStorageApi;
import com.cloudsherpas.http.UserAgentInfo;
import com.cloudsherpas.utils.StrUtils;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.inject.Inject;

public class ReportViewHandler {

	private final ReportDao reportDao;
	private final UserDao userDao;
	private final InstanceDao instanceDao;
	private final AuditDao auditDao;
	private final GroupDao groupDao;
	private final MemcacheService cache;
	private final Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

	
	@Inject
	public ReportViewHandler(UserDao userDao, ReportDao reportDao, InstanceDao instanceDao,AuditDao auditDao,MemcacheService cache,GroupDao groupDao) {
		this.userDao = userDao;
		this.reportDao = reportDao;
		this.instanceDao = instanceDao;
		this.auditDao = auditDao;
		this.cache = cache;
		this.groupDao = groupDao;
		
	}
	
	public void get(String key, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String groupCode = req.getParameter("group_code");
		AppUser currentUser = userDao.getCurrentUser();
		Report report = reportDao.get(key);
		logger.log(Level.INFO,"Start report pdf view. User :" +currentUser.getEmail());
		Instance reportInstance = null;
		if (report != null) {
			    logger.log(Level.INFO,"Report :" +report.getCode());
			    
			    reportInstance = instanceDao.getLastInstanceByCode(report.getCode(),groupCode);
			    
			    logger.log(Level.INFO,"Report instance :" +reportInstance==null?"NULL":reportInstance.getFileName());
			    
			    UserAgentInfo userAgent = new UserAgentInfo(req);
				String deviceType =  userAgent.getDeviceType();
				
				AuditLog auditLog = new AuditLog();
				auditLog.setTitle(report.getTitle());
				auditLog.setCode(report.getCode());
				auditLog.setGroupCode(groupCode);
				auditLog.setHeading(report.getHeadingAsString());
				auditLog.setType(report.getReportType().getName());
				auditLog.setDeletion(report.getDeletionPeriodAsString());
				auditLog.setFrequency(report.getFrequency().toString());
			    auditLog.setEmail(userDao.getCurrentUser().getEmail());
				auditLog.setViewDate(new Date());
				auditLog.setReportDate(reportInstance.getDate());
				auditLog.setDeviceType(deviceType);
				auditLog.setStatus(EntityTransferStatus.READY_TO_TRANSFER.getCode());
				auditLog.setFileName(reportInstance.getFileName());
				auditLog.setFileSize(reportInstance.getFileSize());
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
			
			String fileName = "/" + reportInstance.getGroupCode() + "/" + reportInstance.getReportCode() + "/" + reportInstance.getFileName();
			GoogleCloudStorageApi gcsApi = new GoogleCloudStorageApi();
			gcsApi.init();
			String filePath  = StrUtils.removeStringFirstChar(fileName);
			Boolean  hasFile =  gcsApi.hasFileInBucket(GlobalConstants.REPORTS_BUCKET,filePath);
			if(hasFile){
				CloudStorageSignedUrl storageSignedUrl = new CloudStorageSignedUrl(fileName);
				resp.setHeader("Content-Disposition: inline application", "filename="+fileName);
				resp.sendRedirect(storageSignedUrl.getSignedUrl());	
			}else{
				resp.sendRedirect("/noFile.html");
			}
			
		} else {
			notAllowedReport(resp);
		}
	}
	
	public  boolean isAllowReportToUser(Report report, AppUser user) {
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
}

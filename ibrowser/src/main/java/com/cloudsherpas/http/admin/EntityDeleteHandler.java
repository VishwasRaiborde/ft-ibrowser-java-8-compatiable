package com.cloudsherpas.http.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.GroupDao;
import com.cloudsherpas.dao.HeadingDao;
import com.cloudsherpas.dao.InstanceDao;
import com.cloudsherpas.dao.NewsDao;
import com.cloudsherpas.dao.ReportDao;
import com.cloudsherpas.dao.TradingYearDao;
import com.cloudsherpas.dao.TypeDao;
import com.cloudsherpas.dao.UserDao;
import com.cloudsherpas.domain.GoogleGroup;
import com.cloudsherpas.domain.Heading;
import com.cloudsherpas.domain.Instance;
import com.cloudsherpas.domain.News;
import com.cloudsherpas.domain.Report;
import com.cloudsherpas.domain.TradingYear;
import com.cloudsherpas.domain.Type;
import com.cloudsherpas.google.api.DirectoryApi;
import com.cloudsherpas.request.EntityDeleteRequest;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.cloudsherpas.utils.StrUtils;
import com.google.inject.Inject;

public class EntityDeleteHandler  {
	private HeadingDao headingDao;
	private TypeDao typeDao;
	private ReportDao reportDao;
	private TradingYearDao tradingYearDao;
	private NewsDao newsDao;
	private GroupDao groupDao;
	private UserDao userDao;
	private InstanceDao instanceDao;
	private final ApiHttpUtils utils;
	
	@Inject
	public EntityDeleteHandler(HeadingDao headingDao,TypeDao typeDao,ReportDao reportDao,TradingYearDao tradingYearDao,
			NewsDao newsDao,GroupDao groupDao,UserDao userDao,InstanceDao instanceDao, ApiHttpUtils utils) {
		this.headingDao = headingDao;
		this.typeDao = typeDao;
		this.reportDao = reportDao;
		this.tradingYearDao = tradingYearDao;
		this.newsDao = newsDao;
		this.groupDao = groupDao;
		this.userDao = userDao;
		this.instanceDao = instanceDao;
		this.utils = utils;
	}
	public void get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
	}
	public void post(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	try{
		EntityDeleteRequest ed = (EntityDeleteRequest)utils.readPost(req, EntityDeleteRequest.class);
			String key = ed.key != null ? ed.key : null;
			String entity = ed.entity != null ? ed.entity : null;
			if (!StrUtils.isEmpty(key)) {
			if("Heading".equals(entity)){
				Heading heading = headingDao.get(ed.key);
				if (heading != null) {
					List<Report> headingReports = headingDao.getHeadingReports(heading);
					if (headingReports == null || headingReports.isEmpty()) {
						headingDao.deleteHeading(ed.key);
						utils.sendResponse(resp, "null");
						return;
					} else {
						StringBuilder table = new StringBuilder("<table>");
						for (Report report : headingReports) {
							table.append("<tr><td>").append(report.getTitle()).append("</td></tr>");
						}
						table.append("</table>");
						utils.sendResponse(resp, table);
						return;
					}
				}
			}else if("Type".equals(entity)) {
					Type type = typeDao.get(key);
					if (type != null) {
						Instance instance = instanceDao.getInstanceByGroupCode(type.getGroupCode());
						if(instance==null){
							typeDao.delete(key);
							utils.sendResponse(resp, "null");
							return;
						}else{
							utils.sendResponse(resp, instance.getReportCode());
							return;
						}
					}
			}else if("Report".equals(entity)){
					Report report = reportDao.get(key);
					if (report != null) {
						GoogleGroup reportGroup = groupDao.getGroupByCode("iBrowser-".toUpperCase() + report.getCode().toUpperCase());
						if (reportGroup != null) {
							DirectoryApi directoryApi = new DirectoryApi();
							directoryApi.delete(reportGroup.getName(),userDao.getCurrentUser().getEmail());
							groupDao.delete(reportGroup.getKey());
						 }
						reportDao.delete(key);
					}
				utils.sendResponse(resp, "SUCCESS");
				return;
			}else if("TradingYear".equals(entity)){
                TradingYear tradingYear = tradingYearDao.get(key);
				if (tradingYear != null) {
					tradingYearDao.delete(key);
				}
				utils.sendResponse(resp, "SUCCESS");
				return;
			}else if("News".equals(entity)){
				News news =  newsDao.get(key);
				if(news!=null){
					newsDao.delete(key);
				}
				utils.sendResponse(resp, "SUCCESS");
				return;
			}
			  
		}
	} catch (Exception e) {
		e.printStackTrace();
		utils.sendError(resp, 500, "An unexpected exception occurred: " + e.getClass().getSimpleName() + ": " + e.getMessage());
		throw new RuntimeException(e.getMessage(), e);
	 }
  }
}

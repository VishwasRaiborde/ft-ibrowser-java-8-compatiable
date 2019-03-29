package com.cloudsherpas.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.InstanceDao;
import com.cloudsherpas.dao.NotificationDao;
import com.cloudsherpas.dao.ReportDao;
import com.cloudsherpas.dao.TradingYearDao;
import com.cloudsherpas.google.api.GoogleCloudStorageApi;
import com.cloudsherpas.utils.DateUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("serial")
public class GoogleCloudStorageServlet extends HttpServlet{
	
	private final InstanceDao instanceDao;
	private final TradingYearDao tradingYearDao;
	private final ReportDao reportDao;
	private final NotificationDao notificationDao;
	private final DateUtils dateUtils;
	
	@Inject
	public GoogleCloudStorageServlet(InstanceDao instanceDao, TradingYearDao tradingYearDao, ReportDao reportDao,
			NotificationDao notificationDao,DateUtils dateUtils){
		this.instanceDao = instanceDao;
		this.tradingYearDao = tradingYearDao;
		this.reportDao = reportDao;
		this.notificationDao = notificationDao;
		this.dateUtils = dateUtils;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		GoogleCloudStorageApi gcsApi = new GoogleCloudStorageApi(instanceDao, tradingYearDao, reportDao,notificationDao, dateUtils);
		gcsApi.init();
		gcsApi.run();
	
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
}

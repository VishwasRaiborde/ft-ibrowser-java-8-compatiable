package com.cloudsherpas.http.app;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.UserDao;
import com.cloudsherpas.dao.UserFavouriteReportDao;
import com.cloudsherpas.domain.UserFavouriteReport;
import com.cloudsherpas.request.CreateReportRequest;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.google.inject.Inject;

public class FavouriteReportHandler {
	private final UserDao userDao;
	private final UserFavouriteReportDao favouriteReportDao;
	private final ApiHttpUtils utils;
	
	@Inject
	public FavouriteReportHandler(UserDao userDao,UserFavouriteReportDao favouriteReportDao,ApiHttpUtils utils) {
		this.userDao = userDao;
		this.favouriteReportDao = favouriteReportDao;
		this.utils = utils;
	}
	public void get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    List<UserFavouriteReport> userFavouriteReports = favouriteReportDao.getUserFavouriteReports(userDao.getCurrentUser().getKeyAsString());
	    utils.sendResponse(resp, userFavouriteReports);		
	}
	public void post(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		CreateReportRequest rq = (CreateReportRequest) utils.readPost(req, CreateReportRequest.class);
		Boolean isFavourite = (Boolean) rq.isFavourite;
		favouriteReportDao.saveRemoveUserFavouriteReport(userDao.getCurrentUser().getKeyAsString(),rq.key,rq.groupCode,rq.title,isFavourite);
		//utils.sendResponse(resp, userDao.getCurrentUser().getFavouriteReportsKey());
	}
}

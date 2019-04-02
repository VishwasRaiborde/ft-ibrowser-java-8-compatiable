package com.cloudsherpas.http.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.ReportDao;
import com.cloudsherpas.domain.Report;
import com.cloudsherpas.enums.ReportTypeEnum;
import com.cloudsherpas.request.SearchRequest;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.cloudsherpas.utils.StrUtils;
import com.google.inject.Inject;

public class SearchReportHandler {
  private ReportDao reportDao;
  private final ApiHttpUtils utils;
  private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

  @Inject
  public SearchReportHandler(ReportDao reportDao, ApiHttpUtils utils) {
    this.reportDao = reportDao;
    this.utils = utils;
  }

  public String get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    req.setAttribute("content_holder", "app/search.html");
    req.setAttribute("branchGroups", reportDao.getUserBranches());
    req.setAttribute("buyingGroups", reportDao.getUserBuyingOffices());
    return "OK";
  }

  public void post(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      logger.log(Level.INFO, "Search begin...");
      List<Report> reports = new ArrayList<Report>();
      SearchRequest searchReq = (SearchRequest) utils.readPost(req, SearchRequest.class);
      String code = !StrUtils.isEmpty(searchReq.code, true) ? searchReq.code.toUpperCase() : "#";
      String title = !StrUtils.isEmpty(searchReq.title, true) ? searchReq.title.toUpperCase() : "#";
      if ("#".equals(code) && "#".equals(title)) {
        logger.log(Level.INFO, "Search key is null");
        utils.sendResponse(resp, reports);
      } else {
        if (StrUtils.isEmpty(searchReq.reportType)) {
          logger.log(Level.INFO, "Search Divisional reports");
          reports = reportDao.getDivisionalReports(code, title);
          utils.sendResponse(resp, reports);
        } else {
          logger.log(Level.INFO, "Search Branch or Buying Office reports");
          reports = reportDao.getSearchReports(ReportTypeEnum.buildByCode(searchReq.reportType),
              searchReq.groupCode, code, title);
          utils.sendResponse(resp, reports);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      utils.sendError(resp, 500, "An unexpected exception occurred: " + e.getClass().getSimpleName()
          + ": " + e.getMessage());
    }
  }

}

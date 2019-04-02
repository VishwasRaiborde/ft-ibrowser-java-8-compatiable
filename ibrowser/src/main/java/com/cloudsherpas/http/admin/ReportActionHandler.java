package com.cloudsherpas.http.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.dao.GroupDao;
import com.cloudsherpas.dao.HeadingDao;
import com.cloudsherpas.dao.ReportDao;
import com.cloudsherpas.domain.GoogleGroup;
import com.cloudsherpas.domain.Heading;
import com.cloudsherpas.domain.Report;
import com.cloudsherpas.enums.DeletionPeriodEnum;
import com.cloudsherpas.enums.FrequencyEnum;
import com.cloudsherpas.enums.ReportTypeEnum;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.inject.Inject;

public class ReportActionHandler {

  private final ReportDao reportDao;
  private final HeadingDao headingDao;
  private final GroupDao groupDao;
  private final ApiHttpUtils utils;

  @Inject
  public ReportActionHandler(ReportDao reportDao, HeadingDao headingDao, GroupDao groupDao,
      ApiHttpUtils utils) {
    this.reportDao = reportDao;
    this.headingDao = headingDao;
    this.groupDao = groupDao;
    this.utils = utils;
  }

  public String get(String key, HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    Report report = null;
    if (key != null && !key.isEmpty()) {
      report = reportDao.get(key);
    } else {
      report = new Report();
    }
    ArrayList<ReportTypeEnum> types = new ArrayList<ReportTypeEnum>();
    types.add(ReportTypeEnum.DIVISIONAL);
    types.add(ReportTypeEnum.BRANCH);
    types.add(ReportTypeEnum.BUYING);
    report.setTypeList(types);

    List<Heading> headings = headingDao.getHeadingByOrder();
    report.setHeadingList(headings);

    ArrayList<FrequencyEnum> frequencies = new ArrayList<FrequencyEnum>();
    frequencies.add(FrequencyEnum.DAILY);
    frequencies.add(FrequencyEnum.WEEKLY);
    report.setFrequencyList(frequencies);

    ArrayList<DeletionPeriodEnum> deletingPeriods = new ArrayList<DeletionPeriodEnum>();
    deletingPeriods.add(DeletionPeriodEnum.MONTH_1);
    deletingPeriods.add(DeletionPeriodEnum.MONTH_2);
    deletingPeriods.add(DeletionPeriodEnum.MONTH_3);
    report.setDeletionPeriods(deletingPeriods);

    List<GoogleGroup> groups = groupDao.getReportGroup();
    List<GoogleGroup> groupList = new ArrayList<GoogleGroup>();
    if (groups != null) {
      groupList.addAll(groups);
    }

    if (report.getKey() != null) {
      if (FrequencyEnum.DAILY.equals(report.getFrequency())) {
        deletingPeriods.add(DeletionPeriodEnum.DAY_1);
        deletingPeriods.add(DeletionPeriodEnum.WEEK_1);
        deletingPeriods.add(DeletionPeriodEnum.WEEK_2);
        deletingPeriods.add(DeletionPeriodEnum.WEEK_3);
      }

      GoogleGroup systemGroup = groupDao
          .getGroupByCode("iBrowser-".toUpperCase() + report.getCode().toUpperCase());
      if (systemGroup != null) {
        groupList.add(systemGroup);
      }
      List<GoogleGroup> googleGroups = new ArrayList<GoogleGroup>();
      List<Key> allowedGroups = report.getAllowedGroupsKey();
      List<Key> deniedGroups = report.getDeniedGroupsKey();
      for (GoogleGroup group : groupList) {
        if (allowedGroups != null && allowedGroups.contains(group.getKey())) {
          group.setAllowedGroup(true);
          googleGroups.add(group);
        }
        if (deniedGroups != null && deniedGroups.contains(group.getKey())) {
          googleGroups.add(group);
          group.setDeniedGroup(true);
        }
      }
      groupDao.persistAll(googleGroups);
    }
    report.setGroupList(groupList);
    req.setAttribute("report", report);
    return "OK";
  }

  public String post(String key, HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    try {
      Report report = null;
      if (key != null && !key.isEmpty()) {
        report = reportDao.get(key);
      } else {
        report = new Report();
      }
      report.setCode(req.getParameter("reportCode"));
      report.setTitle(req.getParameter("title"));
      report.setDescription(req.getParameter("description"));
      report.setReportType(ReportTypeEnum.buildByCode(req.getParameter("reportType")));
      report.setHeading(headingDao.get(req.getParameter("heading")));
      if (req.getParameter("frequency") != null) {
        report.setFrequency(FrequencyEnum.buildByCode(req.getParameter("frequency")));
      }
      report.setDeletionPeriod(DeletionPeriodEnum.buildByCode(req.getParameter("deletionPeriod")));
      report.setHeadingAsString(report.getHeading().getName());

      if (FrequencyEnum.DAILY.equals(report.getFrequency())) {
        if (report.getDeletionPeriod().equals(DeletionPeriodEnum.DAY_1)) {
          report.setDeletionPeriodAsString("1 day");
        } else if (report.getDeletionPeriod().equals(DeletionPeriodEnum.WEEK_1)) {
          report.setDeletionPeriodAsString("1 week");
        } else if (report.getDeletionPeriod().equals(DeletionPeriodEnum.WEEK_2)) {
          report.setDeletionPeriodAsString("2 weeks");
        } else if (report.getDeletionPeriod().equals(DeletionPeriodEnum.WEEK_3)) {
          report.setDeletionPeriodAsString("3 weeks");
        } else if (report.getDeletionPeriod().equals(DeletionPeriodEnum.MONTH_1)) {
          report.setDeletionPeriodAsString("30 days");
        } else if (report.getDeletionPeriod().equals(DeletionPeriodEnum.MONTH_2)) {
          report.setDeletionPeriodAsString("60 days");
        } else if (report.getDeletionPeriod().equals(DeletionPeriodEnum.MONTH_3)) {
          report.setDeletionPeriodAsString("90 days");
        }
      } else {
        report.setDeletionPeriodAsString(report.getDeletionPeriod().getName());
      }

      String deniedGroups = req.getParameter("deniedGroupsValue");
      if (deniedGroups != null && !"".equals(deniedGroups)) {
        String[] deniedGroupsArray = deniedGroups.split(",");
        List<GoogleGroup> deniedGoogleGroups = new ArrayList<GoogleGroup>();
        for (String value : deniedGroupsArray) {
          GoogleGroup groupByCode = groupDao.get(value.trim());
          deniedGoogleGroups.add(groupByCode);
        }
        report.setDeniedGroups(deniedGoogleGroups);
      } else {
        report.setDeniedGroups(null);
      }

      String allowedGroups = req.getParameter("allowedGroupsValue");
      String[] allowedGroupsArray = allowedGroups.split(",");
      List<GoogleGroup> allowedGoogleGroups = new ArrayList<GoogleGroup>();
      for (String value : allowedGroupsArray) {
        GoogleGroup groupByCode = groupDao.get(value.trim());
        allowedGoogleGroups.add(groupByCode);
      }

      GoogleGroup ourGroup = groupDao
          .getGroupByCode("iBrowser-".toUpperCase() + report.getCode().toUpperCase());
      if (ourGroup == null) {
        GoogleGroup newGGroup = null;
        if (ourGroup == null) {
          newGGroup = new GoogleGroup();
          newGGroup.setCode("iBrowser-".toUpperCase() + report.getCode().toUpperCase());
          newGGroup.setName("iBrowser-" + report.getCode());
          newGGroup.setDescription("iBrowser users for " + report.getCode());
          newGGroup.setIsSystem(true);
          groupDao.persist(newGGroup);
          ourGroup = newGGroup;
          allowedGoogleGroups.add(ourGroup);

          // There is creating task queue for google group
          Queue queue = QueueFactory.getQueue("AppQueue");
          TaskOptions taskOptions = TaskOptions.Builder
              .withUrl(GlobalConstants.TASK_QUEUE + "/group")
              .param("key", KeyFactory.keyToString(ourGroup.getKey())).method(Method.POST);
          queue.add(taskOptions);
        }
      } else {
        ArrayList<String> list = new ArrayList<String>();
        for (GoogleGroup value : allowedGoogleGroups) {
          list.add(value.getCode());
        }
        if (!list.contains(ourGroup.getCode())) {
          allowedGoogleGroups.add(ourGroup);
        }
      }
      report.setAllowedGroups(allowedGoogleGroups);
      reportDao.persist(report);
      return "SUCCESS";

    } catch (Exception e) {
      e.printStackTrace();
      utils.sendError(resp, 500, "An unexpected exception occurred: " + e.getClass().getSimpleName()
          + ": " + e.getMessage());
      return null;
    }
  }
}

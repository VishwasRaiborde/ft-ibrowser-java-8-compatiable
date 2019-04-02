package com.cloudsherpas.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.cloudsherpas.domain.PatchInstance;
import com.cloudsherpas.enums.ReportPeriodEnum;
import com.cloudsherpas.utils.StrUtils;

public class PatchInstanceDao extends BaseDao<PatchInstance> {

  public PatchInstanceDao() {
    super(PatchInstance.class);
  }

  public List<PatchInstance> getReportDates(String reportKey, String groupCode,
      ReportPeriodEnum reportPeriodEnum) {
    return getReportDates(reportKey, groupCode, reportPeriodEnum, new Date());
  }

  public List<PatchInstance> getReportDates(String reportKey, String groupCode,
      ReportPeriodEnum reportPeriodEnum, Date date) {
    PersistenceManager pm = getPersistenceManager();

    Query instanceQuery = pm.newQuery(PatchInstance.class);
    instanceQuery.setFilter(
        "period == :periodParam && reportCode == :reportCodeParam && groupCode == :groupCode && date <= :dateParam");
    instanceQuery.setOrdering("date DESC");
    try {
      List<PatchInstance> instanceList = (List<PatchInstance>) instanceQuery
          .executeWithArray(reportPeriodEnum, reportKey, groupCode, date);
      return instanceList;
    } finally {
//			instanceQuery.closeAll();
      pm.close();
    }
  }

  public List<PatchInstance> getReportDates(String reportKey, ReportPeriodEnum reportPeriodEnum,
      Date date) {
    PersistenceManager pm = getPersistenceManager();

    Query instanceQuery = pm.newQuery(PatchInstance.class);
    instanceQuery.setFilter(
        "period == :periodParam && reportCode == :reportCodeParam && date <= :dateParam");
    instanceQuery.setOrdering("date DESC");
    try {
      List<PatchInstance> instanceList = (List<PatchInstance>) instanceQuery
          .executeWithArray(reportPeriodEnum, reportKey, date);
      return instanceList;
    } finally {
//			instanceQuery.closeAll();
      pm.close();
    }
  }

  public List<PatchInstance> getReportPatchInstances(String code) {
    PersistenceManager pm = getPersistenceManager();

    Query q = pm.newQuery(PatchInstance.class);
    q.setFilter("reportCode == :codeParam");
    try {
      List<PatchInstance> instanceList = (List<PatchInstance>) q.execute(code);
      return instanceList;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public PatchInstance getLastPatchInstanceByCode(String reportCode, String typeGroupCode) {
    PersistenceManager pm = getPersistenceManager();
    Boolean isNullTypeGroupCode = StrUtils.isEmpty(typeGroupCode);
    List<PatchInstance> list = null;
    Query q = pm.newQuery(PatchInstance.class);
    if (isNullTypeGroupCode) {
      q.setFilter("reportCode == :codeParam");
    } else {
      q.setFilter("reportCode == :codeParam && groupCode == :typeGroupCodeParam");
    }
    q.setOrdering("date DESC");
    q.setRange(0, 1);
    try {
      list = isNullTypeGroupCode ? (List<PatchInstance>) q.execute(reportCode)
          : (List<PatchInstance>) q.execute(reportCode, typeGroupCode);
      if (list.isEmpty()) {
        return null;
      }
      return list.get(0);
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public Set<String> getPatchInstanceGroupCodes(String reportCode) {
    Set<String> instanceGroupCodes = new HashSet<String>();
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(PatchInstance.class);
    q.setFilter("reportCode == :codeParam");// TODO

    try {
      List<PatchInstance> list = (List<PatchInstance>) q.execute(reportCode);
      for (PatchInstance instance : list) {
        instanceGroupCodes.add(instance.getGroupCode());
      }
      return instanceGroupCodes;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<PatchInstance> getPatchInstanceByCodeAndDate(String code) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(PatchInstance.class);
    q.setFilter("reportCode == :codeParam");
    q.setOrdering("date DESC");
    try {
      List<PatchInstance> list = (List<PatchInstance>) q.execute(code);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<PatchInstance> getPatchInstancesByRangeForDelete(String reportCode, Date dateBefore,
      int startIndex, int endIndex) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(PatchInstance.class);
    q.setFilter("reportCode == :codeParam && (period == '" + ReportPeriodEnum.WEEKLY
        + "' || period == '" + ReportPeriodEnum.DAILY + "') && date <= :dateBeforeParam");
    q.setRange(startIndex, endIndex);
    q.setOrdering("date ASC");
    try {
      List<PatchInstance> list = (List<PatchInstance>) q.execute(reportCode, dateBefore);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public boolean hasEndOfPeriodPatchInstances(String fileName) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(PatchInstance.class);
    q.setFilter(
        "fileName == :fileNameParam && period == '" + ReportPeriodEnum.END_OF_TRADING_PERIOD + "'");
    q.setRange(0, 1);
    try {
      List<PatchInstance> list = (List<PatchInstance>) q.execute(fileName);
      return !list.isEmpty();
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<PatchInstance> getPatchInstanceByCodes(List<String> codes) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(PatchInstance.class);
    q.setFilter("reportCode == :codeParam");
    try {
      List<PatchInstance> list = (List<PatchInstance>) q.execute(codes);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<PatchInstance> getLastPatchInstances(String code, String groupCode,
      ReportPeriodEnum reportPeriodEnum) {
    PersistenceManager pm = getPersistenceManager();

    Query instanceQuery = pm.newQuery(PatchInstance.class);
    instanceQuery.setFilter(
        "period == :periodParam && reportCode == :reportCodeParam && groupCode == :groupCodeParam && date <= :dateParam");
    instanceQuery.setOrdering("date DESC");

    // set rage for short history of the report's instances
    instanceQuery.setRange(0, 7);

    try {
      List<PatchInstance> instanceList = (List<PatchInstance>) instanceQuery
          .executeWithArray(reportPeriodEnum, code, groupCode, new Date());
      return instanceList;
    } finally {
//			instanceQuery.closeAll();
      pm.close();
    }
  }

  public List<PatchInstance> getLastTradingPeriodPatchInstances(String code, String groupCode,
      ReportPeriodEnum reportPeriodEnum, Date[] range) {
    PersistenceManager pm = getPersistenceManager();

    Query instanceQuery = pm.newQuery(PatchInstance.class);
    instanceQuery.setFilter(
        "period == :periodParam && reportCode == :reportCodeParam && groupCode == :groupCodeParam && date >= :dateParam1 && date <= :dateParam2");
    instanceQuery.setOrdering("date DESC");

    // set rage for short history of the report's instances
    instanceQuery.setRange(0, 1);

    try {
      List<PatchInstance> instanceList = (List<PatchInstance>) instanceQuery
          .executeWithArray(reportPeriodEnum, code, groupCode, range[0], range[1]);
      return instanceList;
    } finally {
//			instanceQuery.closeAll();
      pm.close();
    }
  }

  public PatchInstance getPatchInstanceByFileName(String fileName, ReportPeriodEnum period) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(PatchInstance.class);
    q.setFilter("fileName == :fileNameParam && period == :periodParam");
    q.setOrdering("date DESC");
    try {
      List<PatchInstance> list = (List<PatchInstance>) q.execute(fileName, period);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list.get(0);
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public PatchInstance getPatchInstanceByGroupCode(String groupCode) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(PatchInstance.class);
    q.setFilter("groupCode == :groupCodeParam");
    q.setRange(0, 1);
    try {
      List<PatchInstance> list = (List<PatchInstance>) q.execute(groupCode);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list.get(0);
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<PatchInstance> getPatchInstancesByPeriod(ReportPeriodEnum periodEnum) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(PatchInstance.class);
    q.setFilter("period == :periodParam");
    try {
      List<PatchInstance> list = (List<PatchInstance>) q.execute(periodEnum);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<PatchInstance> getPatchInstancesByAfterTheDate(ReportPeriodEnum reportPeriodEnum,
      long start, long end) {
    PersistenceManager pm = getPersistenceManager();

    Query instanceQuery = pm.newQuery(PatchInstance.class);
//		instanceQuery.setFilter("period == :periodParam");
    instanceQuery.setOrdering("date DESC");
    instanceQuery.setRange(start, end);
    try {
      List<PatchInstance> instanceList = (List<PatchInstance>) instanceQuery
          .executeWithArray(reportPeriodEnum);
      return instanceList;
    } finally {
//			instanceQuery.closeAll();
      pm.close();
    }
  }
}

package com.cloudsherpas.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.cloudsherpas.domain.Instance;
import com.cloudsherpas.enums.ReportPeriodEnum;
import com.cloudsherpas.utils.StrUtils;

public class InstanceDao extends BaseDao<Instance> {

  public InstanceDao() {
    super(Instance.class);
  }

  public List<Instance> getReportDates(String reportKey, String groupCode,
      ReportPeriodEnum reportPeriodEnum) {
    return getReportDates(reportKey, groupCode, reportPeriodEnum, new Date());
  }

  public List<Instance> getReportDates(String reportKey, String groupCode,
      ReportPeriodEnum reportPeriodEnum, Date date) {
    PersistenceManager pm = getPersistenceManager();

    Query instanceQuery = pm.newQuery(Instance.class);
    instanceQuery.setFilter(
        "period == :periodParam && reportCode == :reportCodeParam && groupCode == :groupCode && date <= :dateParam");
    instanceQuery.setOrdering("date DESC");
    try {
      List<Instance> instanceList = (List<Instance>) instanceQuery
          .executeWithArray(reportPeriodEnum, reportKey, groupCode, date);
      return instanceList;
    } finally {
//			instanceQuery.closeAll();
      pm.close();
    }
  }

  public List<Instance> getReportDates(String reportKey, ReportPeriodEnum reportPeriodEnum,
      Date date) {
    PersistenceManager pm = getPersistenceManager();

    Query instanceQuery = pm.newQuery(Instance.class);
    instanceQuery.setFilter(
        "period == :periodParam && reportCode == :reportCodeParam && date <= :dateParam");
    instanceQuery.setOrdering("date DESC");
    try {
      List<Instance> instanceList = (List<Instance>) instanceQuery
          .executeWithArray(reportPeriodEnum, reportKey, date);
      return instanceList;
    } finally {
//			instanceQuery.closeAll();
      pm.close();
    }
  }

  public List<Instance> getReportInstances(String code) {
    PersistenceManager pm = getPersistenceManager();

    Query q = pm.newQuery(Instance.class);
    q.setFilter("reportCode == :codeParam");
    try {
      List<Instance> instanceList = (List<Instance>) q.execute(code);
      return instanceList;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public Instance getLastInstanceByCode(String reportCode, String typeGroupCode) {
    PersistenceManager pm = getPersistenceManager();
    Boolean isNullTypeGroupCode = StrUtils.isEmpty(typeGroupCode);
    List<Instance> list = null;
    Query q = pm.newQuery(Instance.class);
    if (isNullTypeGroupCode) {
      q.setFilter("reportCode == :codeParam");
    } else {
      q.setFilter("reportCode == :codeParam && groupCode == :typeGroupCodeParam");
    }
    q.setOrdering("date DESC");
    q.setRange(0, 1);
    try {
      list = isNullTypeGroupCode ? (List<Instance>) q.execute(reportCode)
          : (List<Instance>) q.execute(reportCode, typeGroupCode);
      if (list.isEmpty()) {
        return null;
      }
      return list.get(0);
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public Set<String> getInstanceGroupCodes(String reportCode) {
    Set<String> instanceGroupCodes = new HashSet<String>();
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Instance.class);
    q.setFilter("reportCode == :codeParam");// TODO

    try {
      List<Instance> list = (List<Instance>) q.execute(reportCode);
      for (Instance instance : list) {
        instanceGroupCodes.add(instance.getGroupCode());
      }
      return instanceGroupCodes;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<Instance> getInstanceByCodeAndDate(String code) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Instance.class);
    q.setFilter("reportCode == :codeParam");
    q.setOrdering("date DESC");
    try {
      List<Instance> list = (List<Instance>) q.execute(code);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<Instance> getInstancesByRangeForDelete(String reportCode, Date dateBefore,
      int startIndex, int endIndex) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Instance.class);
    q.setFilter("reportCode == :codeParam && (period == '" + ReportPeriodEnum.WEEKLY
        + "' || period == '" + ReportPeriodEnum.DAILY + "') && date <= :dateBeforeParam");
    q.setRange(startIndex, endIndex);
    q.setOrdering("date ASC");
    try {
      List<Instance> list = (List<Instance>) q.execute(reportCode, dateBefore);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public boolean hasEndOfPeriodInstances(String fileName) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Instance.class);
    q.setFilter(
        "fileName == :fileNameParam && period == '" + ReportPeriodEnum.END_OF_TRADING_PERIOD + "'");
    q.setRange(0, 1);
    try {
      List<Instance> list = (List<Instance>) q.execute(fileName);
      return !list.isEmpty();
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<Instance> getInstanceByCodes(List<String> codes) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Instance.class);
    q.setFilter("reportCode == :codeParam");
    try {
      List<Instance> list = (List<Instance>) q.execute(codes);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<Instance> getLastInstances(String code, String groupCode,
      ReportPeriodEnum reportPeriodEnum) {
    PersistenceManager pm = getPersistenceManager();

    Query instanceQuery = pm.newQuery(Instance.class);
    instanceQuery.setFilter(
        "period == :periodParam && reportCode == :reportCodeParam && groupCode == :groupCodeParam && date <= :dateParam");
    instanceQuery.setOrdering("date DESC");

    // set rage for short history of the report's instances
    instanceQuery.setRange(0, 7);

    try {
      List<Instance> instanceList = (List<Instance>) instanceQuery
          .executeWithArray(reportPeriodEnum, code, groupCode, new Date());
      return instanceList;
    } finally {
//			instanceQuery.closeAll();
      pm.close();
    }
  }

  public List<Instance> getLastTradingPeriodInstances(String code, String groupCode,
      ReportPeriodEnum reportPeriodEnum, Date[] range) {
    PersistenceManager pm = getPersistenceManager();

    Query instanceQuery = pm.newQuery(Instance.class);
    instanceQuery.setFilter(
        "period == :periodParam && reportCode == :reportCodeParam && groupCode == :groupCodeParam && date >= :dateParam1 && date <= :dateParam2");
    instanceQuery.setOrdering("date DESC");

    // set rage for short history of the report's instances
    instanceQuery.setRange(0, 1);

    try {
      List<Instance> instanceList = (List<Instance>) instanceQuery
          .executeWithArray(reportPeriodEnum, code, groupCode, range[0], range[1]);
      return instanceList;
    } finally {
//			instanceQuery.closeAll();
      pm.close();
    }
  }

  public Instance getInstanceByFileName(String fileName, ReportPeriodEnum period) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Instance.class);
    q.setFilter("fileName == :fileNameParam && period == :periodParam");
    q.setOrdering("date DESC");
    try {
      List<Instance> list = (List<Instance>) q.execute(fileName, period);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list.get(0);
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public Instance getInstanceByGroupCode(String groupCode) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Instance.class);
    q.setFilter("groupCode == :groupCodeParam");
    q.setRange(0, 1);
    try {
      List<Instance> list = (List<Instance>) q.execute(groupCode);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list.get(0);
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<Instance> getInstancesByPeriod(ReportPeriodEnum periodEnum) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Instance.class);
    q.setFilter("period == :periodParam");
    try {
      List<Instance> list = (List<Instance>) q.execute(periodEnum);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<Instance> getInstancesByAfterTheDate(ReportPeriodEnum reportPeriodEnum,
      /* Date afterTheDate, Date beforeTheDate, */ long start, long end) {
    PersistenceManager pm = getPersistenceManager();
    Query instanceQuery = pm.newQuery(Instance.class);
    instanceQuery
        .setFilter("period ==:periodParam " /* && date >= :dateParam && date < :dateParam2 */);
    instanceQuery.setOrdering("date DESC");
    instanceQuery.setRange(start, end);
    try {
      List<Instance> instanceList = (List<Instance>) instanceQuery
          .executeWithArray(reportPeriodEnum);
      return instanceList;
    } finally {
//			instanceQuery.closeAll();
      pm.close();
    }
  }
}

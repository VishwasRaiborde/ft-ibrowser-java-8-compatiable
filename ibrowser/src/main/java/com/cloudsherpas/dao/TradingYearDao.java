package com.cloudsherpas.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.domain.TradingYear;
import com.cloudsherpas.utils.DateUtils;

public class TradingYearDao extends BaseDao<TradingYear> {
  public TradingYearDao() {
    super(TradingYear.class);
  }

  @Override
  public void persist(TradingYear entity) {
    // calculate end date of given trading year
    GregorianCalendar calendar = DateUtils.convertDateToCalendar(entity.getStartDate());
    calendar.add(Calendar.WEEK_OF_YEAR, entity.getNumberOfWeek());
    entity.setEndDate(calendar.getTime());

    PersistenceManager pm = getPersistenceManager();
    Transaction txn = pm.currentTransaction();
    try {
      txn.begin();
      if (entity.isNew()) {
        entity.setCreatedDate(new Date());
      }
      entity.setLastUpdatedDate(new Date());
      pm.makePersistent(entity);
      txn.commit();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
      pm.close();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public List<TradingYear> getTradingYears() {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(TradingYear.class);
    q.setRange(0, GlobalConstants.PAGE_SIZE);
    q.setOrdering("lastUpdatedDate DESC");
    try {
      List<TradingYear> list = (List<TradingYear>) q.execute();
      if (list.isEmpty()) {
        list = null;
      }
      return list;
    } finally {
      pm.close();
    }
  }

  public List<TradingYear> getTradingYearsByParam(String sortKey, String order, String step) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(TradingYear.class);

    if (!"".equals(sortKey)) {
      q.setOrdering(sortKey + " " + order);
    } else {
      q.setOrdering("lastUpdatedDate DESC");
    }
    if (!"".equals(step)) {
      Integer end = Integer.valueOf(step) * GlobalConstants.PAGE_SIZE;
      q.setRange(end - GlobalConstants.PAGE_SIZE, end);
    } else {
      q.setRange(0, GlobalConstants.PAGE_SIZE);
    }
    try {
      List<TradingYear> list = (List<TradingYear>) q.execute();
      if (list.isEmpty()) {
        list = null;
      }
      return list;
    } finally {
      pm.close();
    }
  }

  public TradingYear getTradingYear(Integer year) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(TradingYear.class);
    q.setFilter("tradingYear==:yearParam");

    try {
      List<TradingYear> list = (List<TradingYear>) q.execute(year);

      if (list == null || list.isEmpty()) {
        return null;
      }
      return list.get(0);
    } finally {
      pm.close();
    }
  }

  public TradingYear getTradingYearByDate(Date date) {
    PersistenceManager pm = getPersistenceManager();

    Query q = pm.newQuery(TradingYear.class);
    List<TradingYear> list = (List<TradingYear>) q.execute();

    // If we have no any trading years then creating trading year by system process will be cancel
    if (list == null || list.isEmpty()) {
      logger.getLevel();
      logger.log(Level.SEVERE,
          "No trading period found for the date:" + DateUtils.dateFormat1.format(date));
      return null;
    }

    q = pm.newQuery(TradingYear.class);
    q.setFilter("endDate > :dateParam2");
    try {
      list = (List<TradingYear>) q.execute(date);

      for (TradingYear tradingYear : list) {
        if (tradingYear.getStartDate().compareTo(date) != 1
            && tradingYear.getEndDate().compareTo(date) == 1) {
          return tradingYear;
        }
      }

      // createTradingYearByPrevNexYears(date, true); // no need to create the trading years
      // automatically,
      // Trading years must be manually added in the admin section
      logger.log(Level.SEVERE,
          "No Trading Year found for the date:" + DateUtils.dateFormat1.format(date));

      return null; // If trading year was not found in the system it should fail processing.

    } finally {
      pm.close();
    }
  }

  protected void createTradingYearByPrevNexYears(Date date, boolean greaterThan) {
    PersistenceManager pm = getPersistenceManager();

    try {
      Query q = pm.newQuery(TradingYear.class);

      if (greaterThan) {
        q.setFilter("endDate <= :dateParam");
      } else {
        q.setFilter("startDate > :dateParam");
      }
      q.setOrdering(greaterThan ? "endDate DESC" : "startDate ASC");
      List<TradingYear> list = (List<TradingYear>) q.execute(date);

      if (list == null || list.isEmpty()) {
        createTradingYearByPrevNexYears(date, false);
      }

      // in the recursive process
      if (list.isEmpty()) {
        return;
      }

      TradingYear tradingYear = list.get(0);
      do {
        GregorianCalendar calendar = new GregorianCalendar();
        if (greaterThan) {
          calendar.setTime(tradingYear.getEndDate());
        } else {
          calendar.setTime(tradingYear.getStartDate());
          calendar.add(Calendar.WEEK_OF_YEAR, -52);

          // this process will work if the prev year is Leap Year
          if (calendar.isLeapYear(calendar.get(Calendar.YEAR))) {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
          }
        }
        tradingYear = new TradingYear();
        tradingYear.setStartDate((Date) calendar.getTime().clone());
        tradingYear.setNumberOfWeek(calendar.isLeapYear(calendar.get(Calendar.YEAR)) ? 53 : 52);
        tradingYear.setTradingYear(calendar.get(Calendar.YEAR));

        // initialize end date of trading year
        calendar.add(Calendar.WEEK_OF_YEAR, tradingYear.getNumberOfWeek());
        tradingYear.setEndDate(calendar.getTime());

        persist(tradingYear);

      } while (!(tradingYear.getStartDate().compareTo(date) != 1
          && tradingYear.getEndDate().compareTo(date) == 1));

    } finally {
      pm.close();
    }
  }
}

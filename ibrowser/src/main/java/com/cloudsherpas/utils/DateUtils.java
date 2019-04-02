package com.cloudsherpas.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import com.cloudsherpas.dao.TradingYearDao;
import com.cloudsherpas.domain.TradingYear;

public class DateUtils {

  public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
  public static SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
  public static SimpleDateFormat dateFormat2 = new SimpleDateFormat("ddMMyyyy");
  public static SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
  public static SimpleDateFormat dateFormat4 = new SimpleDateFormat("ddMMyyyy hh:mm:ss");
  public static SimpleDateFormat dateFormat5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public static SimpleDateFormat dateFormat6 = new SimpleDateFormat("dd/MM/yyyy");
  public static SimpleDateFormat dateFormat7 = new SimpleDateFormat("HH:mm:ss");
  public static SimpleDateFormat dateFormat8 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  public static HashMap<Date, Boolean> dateYearCache = new HashMap<Date, Boolean>();
  public static HashMap<Date, Boolean> dateHalfCache = new HashMap<Date, Boolean>();
  public static HashMap<Date, Boolean> datePeriodCache = new HashMap<Date, Boolean>();

  // private Date currentYearStartDate;

  public static HashMap<Integer, GregorianCalendar> yearStartDates = new HashMap<Integer, GregorianCalendar>();

  private final TradingYearDao tradingYearDao;

  @Inject
  public DateUtils(TradingYearDao tradingYearDao) {
    this.tradingYearDao = tradingYearDao;
  }
//	public DateUtils(Date startDate) {
//		this.currentYearStartDate = startDate;
//	}
//
//	public void setCurrentYearStartDate(Date date) {
//		this.currentYearStartDate = date;
//	}

  public static Date getDateFromString(String stringDate) {
    Date date = null;
    try {
      String parts[] = stringDate.split(",");
      date = dateFormat1.parse(parts[1]);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  public static String getDateFormat(Date date) {
    if (date == null) {
      return null;
    }
    return dateFormat1.format(date);
  }

  public static Date getDateFormat(String dateStr) {
    Date date = null;
    try {
      if (StrUtils.isEmpty(dateStr)) {
        return date;
      }
      date = dateFormat1.parse(dateStr);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  public static String getDateFormat(Object object, SimpleDateFormat format) {
    String dateStr = null;
    if (object == null) {
      return null;
    }
    dateStr = format.format(object);
    return dateStr;
  }

  public Date getCurrentYearStartDate() {
    TradingYear tradingYear = tradingYearDao.getTradingYearByDate(new Date());
    return tradingYear.getStartDate();

  }

  public Integer getTradingWeekNumber(Date date) {// 26052012

    TradingYear tradingYear = tradingYearDao.getTradingYearByDate(date);

    int weekIndex = -1;
    GregorianCalendar weekStartDate = convertDateToCalendar(tradingYear.getStartDate());
//		GregorianCalendar weekStartDate1 = convertDateToCalendar((Date) currentYearStartDate
//				.clone());
    int weeks = tradingYear.getNumberOfWeek();
//		GregorianCalendar prevWeekStart = getWeekStartDate(weekStartDate1);
//		if (date.getTime() >= prevWeekStart.getTimeInMillis()
//				&& date.getTime() < currentYearStartDate.getTime()) {
//			weekIndex = 0;
//		} else {
    for (int i = 1; i <= weeks; i++) {
      GregorianCalendar weekEndDate = addDays((GregorianCalendar) weekStartDate.clone(), 7);
      if (date.getTime() >= weekStartDate.getTimeInMillis()
          && date.getTime() < weekEndDate.getTimeInMillis()) {
        weekIndex = i;
        break;
      }
      weekStartDate = addDays(weekStartDate, 7);
    }
//		}
    return weekIndex;
  }

  public static void main(String[] args) {
    StringBuilder sb = new StringBuilder();
    sb.append(
        "Can someone please tell me how to get a Text value out of a Google App Engine datastore using Java? I have some entities in the datastore with a Text property named longDescription. When I try this:");
    sb.append(
        "Can someone please tell me how to get a Text value out of a Google App Engine datastore using Java? I have some entities in the datastore with a Text property named longDescription. When I try this:");
    sb.append(
        "Can someone please tell me how to get a Text value out of a Google App Engine datastore using Java? I have some entities in the datastore with a Text property named longDescription. When I try this:");
    sb.append(
        "Can someone please tell me how to get a Text value out of a Google App Engine datastore using Java? I have some entities in the datastore with a Text property named longDescription. When I try this:");
    sb.append(
        "Can someone please tell me how to get a Text value out of a Google App Engine datastore using Java? I have some entities in the datastore with a Text property named longDescription. When I try this:");
    System.out.println(sb.toString().length() + "  " + sb.toString());
  }

  public int getTradingPeriod(Date date) {
    int result = 1;
    int weeksCount = getWeeksCount(convertDateToCalendar(date));
    int week = getTradingWeekNumber(date);
    if (week >= 1 && week <= 4) {
      result = 1;
    } else if (week >= 5 && week <= 8) {
      result = 2;
    } else if (week >= 9 && week <= 13) {
      result = 3;
    } else if (week >= 14 && week <= 17) {
      result = 4;
    } else if (week >= 18 && week <= 21) {
      result = 5;
    } else if (week >= 22 && week <= 26) {
      result = 6;
    } else if (week >= 27 && week <= 30) {
      result = 7;
    } else if (week >= 31 && week <= 34) {
      result = 8;
    } else if (week >= 35 && week <= 39) {
      result = 9;
    } else if (week >= 40 && week <= 43) {
      result = 10;
    } else {
      if (weeksCount == 52) {
        if (week >= 44 && week <= 47) {
          result = 11;
        } else if (week >= 48 && week <= 52) {
          result = 12;
        }
      } else {
        if (week >= 44 && week <= 48) {
          result = 11;
        } else if (week >= 49 && week <= 53) {
          result = 12;
        }
      }
    }
    return result;
  }

  public Date[] getLastYearDate() {
    GregorianCalendar prevYear = addDays(
        convertDateToCalendar((Date) getCurrentYearStartDate().clone()), -7);
    GregorianCalendar endDate = addDays((GregorianCalendar) prevYear.clone(), 6);
    setEndOfDay(endDate);
    return new Date[] { prevYear.getTime(), endDate.getTime() };
  }

  public Date[] getLastHalfDate() {
    Date date = new Date();
    Date result = null;
    int week = getTradingWeekNumber(date);

    if (week > 26) {
      ArrayList<Date> weeks = getTradingYearWeeks();
      result = weeks.get(26);
    } else {
      GregorianCalendar prevYearEndWeek = addDays(convertDateToCalendar(getCurrentYearStartDate()),
          -7);
      result = prevYearEndWeek.getTime();
    }
    GregorianCalendar endDate = addDays(convertDateToCalendar(result), 6);
    setEndOfDay(endDate);
    return new Date[] { result, endDate.getTime() };
  }

  public Date[] getLastTradingPeriodDate() {
    Date date = new Date();
    Date result = null;
    ArrayList<Date> weeks = getTradingYearWeeks();
    int week = getTradingWeekNumber(date);
    int tradingPeriodNumber = getTradingPeriod(date) - 1;
    if (tradingPeriodNumber > 0) {
      switch (tradingPeriodNumber) {
      case 1:
        result = weeks.get(3);
        break;
      case 2:
        result = weeks.get(7);
        break;
      case 3:
        result = weeks.get(12);
        break;
      case 4:
        result = weeks.get(16);
        break;
      case 5:
        result = weeks.get(20);
        break;
      case 6:
        result = weeks.get(25);
        break;
      case 7:
        result = weeks.get(29);
        break;
      case 8:
        result = weeks.get(33);
        break;
      case 9:
        result = weeks.get(38);
        break;
      case 10:
        result = weeks.get(42);
        break;
      case 11:
        result = weeks.get(46);
        break;
      case 12:
        if (week == 52) {
          result = weeks.get(51);
        } else
          result = weeks.get(52);
        break;
      }
    } else {
      GregorianCalendar startDate = convertDateToCalendar((Date) getCurrentYearStartDate().clone());
      addDays(startDate, -7);
      GregorianCalendar endDate = convertDateToCalendar((Date) getCurrentYearStartDate().clone());
      addDays(endDate, -1);
      setEndOfDay(endDate);
      return new Date[] { startDate.getTime(), endDate.getTime() };
    }
    GregorianCalendar endDate = addDays(convertDateToCalendar(result), 6);
    setEndOfDay(endDate);
    return new Date[] { result, endDate.getTime() };
  }

  public Date[] getCurrentWeekDates() {
    GregorianCalendar weekStartDate = getWeekStartDate(convertDateToCalendar(new Date()));
    GregorianCalendar weekEndDate = addDays((GregorianCalendar) weekStartDate.clone(), 6);
    setEndOfDay(weekEndDate);
    return new Date[] { weekStartDate.getTime(), weekEndDate.getTime() };
  }

  public Date[] getLast7DaysPeriod() {
    GregorianCalendar weekStartDate = convertDateToCalendar(new Date());
    GregorianCalendar prevWeekStartDate = addDays((GregorianCalendar) weekStartDate.clone(), -7);

    return new Date[] { weekStartDate.getTime(), prevWeekStartDate.getTime() };
  }

  public Date[] getLastWeekDates() {
    GregorianCalendar weekStartDate = getWeekStartDate(convertDateToCalendar(new Date()));
    GregorianCalendar prevWeekStartDate = addDays((GregorianCalendar) weekStartDate.clone(), -7);
    GregorianCalendar prevWeekEndDate = addDays((GregorianCalendar) prevWeekStartDate.clone(), 6);
    setEndOfDay(prevWeekEndDate);
    return new Date[] { prevWeekStartDate.getTime(), prevWeekEndDate.getTime() };
  }

  public boolean isEndOfTradingPeriod(Date date) {

    Boolean cacheValue = datePeriodCache.get(date);
    if (cacheValue != null) {
      return cacheValue.booleanValue();
    }

    if (!isEndOfWeek(date)) {
      cacheValue = new Boolean(false);
      datePeriodCache.put(date, cacheValue);
      return false; // always needs to be Saturday.
    }

    int weeksCount = getWeeksCount(convertDateToCalendar(date));
    int week = getTradingWeekNumber(date); // 1 <= week <= 53

    cacheValue = new Boolean(week == 0 || week == 4 || week == 8 || week == 13 || week == 17
        || week == 21 || week == 26 || week == 30 || week == 34 || week == 39 || week == 43
        || weeksCount == 52 && (week == 47 || week == 52)
        || weeksCount == 53 && (week == 48 || week == 53));
    datePeriodCache.put(date, cacheValue);
    return (week == 0 || week == 4 || week == 8 || week == 13 || week == 17 || week == 21
        || week == 26 || week == 30 || week == 34 || week == 39 || week == 43
        || weeksCount == 52 && (week == 47 || week == 52)
        || weeksCount == 53 && (week == 48 || week == 53));
  }

  public boolean isEndOfWeek(Date date) {
    GregorianCalendar cal = convertDateToCalendar(date);
    return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
  }

  public boolean isEndOfHalf(Date date) {

    Boolean cacheValue = dateHalfCache.get(date);
    if (cacheValue != null) {
      return cacheValue.booleanValue();
    }

    if (!isEndOfWeek(date)) {
      cacheValue = new Boolean(false);
      dateHalfCache.put(date, cacheValue);
      return false; // always needs to be Saturday.
    }

    int week = getTradingWeekNumber(date);
    int weeksCount = getWeeksCount(convertDateToCalendar(date));

    cacheValue = new Boolean(
        (week == 26 || (weeksCount == 52 && week == 52) || (weeksCount == 53 && week == 53)));
    dateHalfCache.put(date, cacheValue);
    return (week == 26 || (weeksCount == 52 && week == 52) || (weeksCount == 53 && week == 53));
  }

  public boolean isEndOfYear(Date date) {

    Boolean cacheValue = dateYearCache.get(date);
    if (cacheValue != null) {
      return cacheValue.booleanValue();
    }

    if (!isEndOfWeek(date)) {
      cacheValue = new Boolean(false);
      dateYearCache.put(date, cacheValue);
      return false; // always needs to be Saturday.
    }
    int week = getTradingWeekNumber(date);
    int weeksCount = getWeeksCount(convertDateToCalendar(date));

    cacheValue = new Boolean((weeksCount == 52 && week == 52 || weeksCount == 53 && week == 53));
    dateYearCache.put(date, cacheValue);
    return (weeksCount == 52 && week == 52 || weeksCount == 53 && week == 53);
  }

  public int getWeeksCount(GregorianCalendar calendar) {

    TradingYear tradingYear = tradingYearDao.getTradingYearByDate(calendar.getTime());

    int weeks = 52;
    if (tradingYear != null) {
      weeks = tradingYear.getNumberOfWeek();
    }
    return weeks;
  }

  public ArrayList<Date> getTradingYearWeeks() {
    ArrayList<Date> result = new ArrayList<Date>();
    GregorianCalendar calendar = convertDateToCalendar((Date) getCurrentYearStartDate().clone());
    int weeks = getWeeksCount(convertDateToCalendar(getCurrentYearStartDate()));
    for (int i = 1; i <= weeks; i++) {
      result.add(calendar.getTime());
      calendar = addDays(calendar, 7);
    }
    return result;
  }

  public Date getHalfOfYear() {
    ArrayList<Date> weeks = getTradingYearWeeks();
    return weeks.get(25);
  }

  public Date getEndOfYear() {
    ArrayList<Date> weeks = getTradingYearWeeks();
    Date endDate = weeks.get(51);
    return addDays(convertDateToCalendar(endDate), 7).getTime();
  }

  public static GregorianCalendar convertDateToCalendar(Date date) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    setStartOfDay(calendar);
    return calendar;
  }

  public static GregorianCalendar addDays(GregorianCalendar calendar, int days) {
    calendar.add(Calendar.DATE, days);
    return calendar;
  }

  public static GregorianCalendar addMonth(GregorianCalendar calendar, int month) {
    calendar.add(Calendar.MONTH, month);
    return calendar;
  }

  public static GregorianCalendar addYear(GregorianCalendar calendar, int year) {
    calendar.add(Calendar.YEAR, year);
    return calendar;
  }

  public void getDateRangesForPatch(List<Date> afterDates, List<Date> beforeDates) {
    try {
      Date date = dateFormat2.parse("01102014");
      GregorianCalendar dayAfter = convertDateToCalendar(date);
      setStartOfDay(dayAfter);

      for (int i = 0; i < 160; i++) {

        addDays(dayAfter, 1);

        GregorianCalendar dayBefore = (GregorianCalendar) dayAfter.clone();
        addDays(dayBefore, 1);

        if (isEndOfTradingPeriod(dayAfter.getTime()) || isEndOfHalf(dayAfter.getTime())
            || isEndOfYear(dayAfter.getTime())) {
          afterDates.add(dayAfter.getTime());
          beforeDates.add(dayBefore.getTime());

        }
      }

    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static String getDateToString(Date date) {
    String res = "";
    if (date != null) {
      GregorianCalendar today = convertDateToCalendar(new Date());
      GregorianCalendar yesterday = addDays(convertDateToCalendar(new Date()), -1);
      GregorianCalendar reportDate = convertDateToCalendar(date);
      GregorianCalendar weekStartDate = getWeekStartDate(convertDateToCalendar(date));
      GregorianCalendar weekEndDate = addDays(convertDateToCalendar(date), 6);
      setEndOfDay(weekEndDate);

      if (today.getTime().equals(reportDate.getTime())) {
        res = "Today";
      } else if (yesterday.getTime().equals(reportDate.getTime())) {
        res = "Yesterday";
      } else if (date.getTime() >= weekStartDate.getTimeInMillis()
          && date.getTime() <= weekEndDate.getTimeInMillis()) {
        res = "This week";
      } else {
        res = dateFormat1.format(date);
      }
    }
    return res;
  }

  public static Date addDaysToDate(Date date, int days) {
    return new Date(date.getYear(), date.getMonth(), date.getDate() + days, date.getHours(),
        date.getMinutes(), date.getSeconds());
  }

  public static GregorianCalendar getWeekStartDate(GregorianCalendar calendar) {
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    return addDays(calendar, (-1) * dayOfWeek);
  }

  public static void setStartOfDay(GregorianCalendar calendar) {
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
  }

  public static void setEndOfDay(GregorianCalendar calendar) {
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
  }

  public static Date addDaysToTime(Date date, int minute, int secund) {
    return new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(),
        date.getMinutes() + minute, date.getSeconds() + secund);
  }

  public static GregorianCalendar getFifteenDate() {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.add(Calendar.MINUTE, 14);
    calendar.add(Calendar.SECOND, 40);
    return calendar;
  }

}

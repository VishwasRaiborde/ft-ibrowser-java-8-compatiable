package com.cloudsherpas.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.cloudsherpas.domain.Type;
import com.cloudsherpas.domain.UserFavouriteReport;
import com.google.inject.Inject;

public class UserFavouriteReportDao extends BaseDao<UserFavouriteReport> {

  private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
  private final TypeDao typeDao;

  @Inject
  public UserFavouriteReportDao(TypeDao typeDao) {
    super(UserFavouriteReport.class);
    this.typeDao = typeDao;

  }

  public void saveRemoveUserFavouriteReport(String userKey, String reportKey, String groupCode,
      String title, Boolean isFavourite) {
    UserFavouriteReport favouriteReport = null;
    if (isFavourite) {
      Type type = typeDao.getTypeByParam(groupCode, "groupCode");
      favouriteReport = new UserFavouriteReport();
      favouriteReport.setUserKey(userKey);
      favouriteReport.setReportKey(reportKey);
      favouriteReport.setGroupCode(groupCode);
      favouriteReport.setTitle(title);
      favouriteReport.setGroupName(type != null ? type.getGroupName() : "");

      persist(favouriteReport);
      logger.log(Level.INFO, "User Favourite Report Successful Saved! Group Code: " + groupCode);

    } else {
      favouriteReport = getUserFavouriteReport(userKey, reportKey, groupCode);
      if (favouriteReport != null) {
        delete(favouriteReport.getKey());
        logger.log(Level.INFO,
            "User Favourite Report Successful Deleted! Group Code: " + groupCode);
      }
    }

  }

  public UserFavouriteReport getUserFavouriteReport(String userKey, String reportKey,
      String groupCode) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(UserFavouriteReport.class);
    q.setFilter(
        "userKey == :userKeyParam && reportKey == :reportKeyParam && groupCode == :groupCodeParam");
    try {
      List<UserFavouriteReport> list = (List<UserFavouriteReport>) q.execute(userKey, reportKey,
          groupCode);
      if (list.isEmpty()) {
        return null;
      }
      return list.get(0);
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<UserFavouriteReport> getUserFavouriteReports(String userKey) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(UserFavouriteReport.class);
    q.setFilter("userKey == :userKeyParam");
    try {
      List<UserFavouriteReport> list = (List<UserFavouriteReport>) q.execute(userKey);
      if (list.isEmpty()) {
        return null;
      }
      return list;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public UserFavouriteReport getUserFavouriteReportByReportKey(String reportKey) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(UserFavouriteReport.class);
    q.setFilter("reportKey == :reportKeyParam");
    try {
      List<UserFavouriteReport> list = (List<UserFavouriteReport>) q.execute(reportKey);
      if (list.isEmpty()) {
        return null;
      }
      return list.get(0);
    } finally {
      q.closeAll();
      pm.close();
    }
  }
}

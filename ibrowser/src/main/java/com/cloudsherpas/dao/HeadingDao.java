package com.cloudsherpas.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.cloudsherpas.domain.Heading;
import com.cloudsherpas.domain.Report;
import com.google.appengine.api.datastore.Key;

public class HeadingDao extends BaseDao<Heading> {
  private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

  public HeadingDao() {
    super(Heading.class);
  }

  public List<Heading> getHeadingByOrder() {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Heading.class);
    q.setOrdering("order ASC");
    List<Heading> list = null;
    for (int i = 0; i < 5; i++) {
      try {
        list = (List<Heading>) q.execute();
        if (list.isEmpty()) {
          list = null;
        } else {
          for (Heading heading : list) {
            heading.setKeyAsString();
          }
          break;
        }

      } catch (Exception e) {
        logger.log(Level.SEVERE, "Could not fetch headers", e);
      } finally {
        q.closeAll();
        pm.close();
      }
    }
    return list;
  }

  public void deleteHeading(String key) {
    delete(key);
    List<Heading> list = getHeadingByOrder();
    if (list != null && !list.isEmpty()) {
      for (int i = 0; i < list.size(); i++) {
        list.get(i).setOrder(i);
      }
    }
    persistAll(list);

  }

  /*
   * public void delete(Object key) { PersistenceManager pm = getPersistenceManager(); Transaction
   * txn = pm.currentTransaction(); try { txn.begin();
   * pm.deletePersistent(pm.getObjectById(Heading.class, key)); txn.commit(); } finally { if
   * (txn.isActive()) { txn.rollback(); } pm.close(); } }
   */
  public Key getHeadingKeyByName(String name) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Heading.class);
    q.setFilter("name ==: headingParam");
    try {
      List<Heading> list = (List<Heading>) q.execute(name);
      if (list.isEmpty()) {
        return null;
      }
      return list.get(0).getKey();
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  @SuppressWarnings("unchecked")
  public List<Report> getHeadingReports(Heading heading) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Report.class);
    q.setFilter("heading ==:headingParam");
    try {
      List<Report> result = (List<Report>) q.execute(heading);
      return result;
    } finally {
      pm.close();
    }

  }
}

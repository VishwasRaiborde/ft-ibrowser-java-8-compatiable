package com.cloudsherpas.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.cloudsherpas.domain.AuditLog;

public class AuditDao extends BaseDao<AuditLog> {

  public AuditDao() {
    super(AuditLog.class);
  }

  public AuditLog getAuditLogByCode(String code) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(AuditLog.class);
    q.setFilter("code == :codeParam");
    try {
      List<AuditLog> list = (List<AuditLog>) q.execute(code);
      if (list == null || list.isEmpty()) {
        return null;
      }
      return list.get(0);
    } finally {
      q.closeAll();
      pm.close();
    }
  }
}

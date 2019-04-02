package com.cloudsherpas.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.cloudsherpas.domain.Patch;

public class PatchDao extends BaseDao<Patch> {

  public PatchDao() {
    super(Patch.class);
  }

  public Patch getLastPatch() {
    PersistenceManager pm = getPersistenceManager();

    List<Patch> list = null;
    Query q = pm.newQuery(Patch.class);
    // q.setFilter("reportCode == :codeParam");
    // q.setOrdering("date DESC");
    q.setRange(0, 1);
    try {
      list = (List<Patch>) q.execute();
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

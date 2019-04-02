package com.cloudsherpas.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.cloudsherpas.domain.GoogleGroupMember;

public class GoogleGroupMemberDao extends BaseDao<GoogleGroupMember> {

  public GoogleGroupMemberDao() {
    super(GoogleGroupMember.class);
  }

  public GoogleGroupMember getGoogleGroupMemberByParentEmail(String parentEmail) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(GoogleGroupMember.class);
    q.setFilter("parentEmail == :parentEmailParam");
    q.setRange(0, 1);
    try {
      List<GoogleGroupMember> result = (List<GoogleGroupMember>) q.execute(parentEmail);
      if (result == null || result.isEmpty()) {
        return null;
      }
      return result.get(0);
    } finally {
      q.closeAll();
      pm.close();
    }
  }

}

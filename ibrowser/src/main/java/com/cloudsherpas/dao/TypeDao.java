package com.cloudsherpas.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.cloudsherpas.domain.Type;
import com.cloudsherpas.enums.ReportTypeEnum;

public class TypeDao extends BaseDao<Type> {
  public TypeDao() {
    super(Type.class);
  }

  public List<Type> getTypes(String type, String groupName) {
    PersistenceManager pm = getPersistenceManager();

    Query q = pm.newQuery("select from " + Type.class.getName());

    if (groupName == null) {
      q.setFilter("type == :typeParam");
    } else {
      q.setFilter("type == :typeParam && groupName == :groupNameParam");
    }
    try {
      List<Type> list = null;
      if (groupName == null) {
        list = (List<Type>) q.execute(type);
      } else {
        list = (List<Type>) q.execute(type, groupName);
      }
      if (list.isEmpty()) {
        return null;
      }
      return list;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public ArrayList<Type> getTypeGroupByName() {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Type.class);
    ArrayList<Type> divisional = new ArrayList<Type>();
    ArrayList<Type> branch = new ArrayList<Type>();
    ArrayList<Type> buying = new ArrayList<Type>();
    ArrayList<Type> results = new ArrayList<Type>();
    try {
      List<Type> list = (List<Type>) q.execute();
      if (list.isEmpty()) {
        return null;
      }
      for (Type type : list) {
        if (ReportTypeEnum.DIVISIONAL.equals(type.getType())) {
          divisional.add(type);
        } else if (ReportTypeEnum.BRANCH.equals(type.getType())) {
          branch.add(type);
        } else {
          buying.add(type);
        }
      }
      results.addAll(divisional);
      results.addAll(branch);
      results.addAll(buying);
      return results;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public Type getTypeByParam(String param, String field) {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Type.class);
    if ("groupCode".equals(field)) {
      q.setFilter("groupCode == :groupCodeParam");
    } else if ("groupName".equals(field)) {
      q.setFilter("groupName == :groupNameParam");
    }
    try {
      List<Type> list = (List<Type>) q.execute(param);
      if (list.isEmpty()) {
        return null;
      }
      return list.get(0);
    } finally {
      q.closeAll();
      pm.close();
    }
  }

  public List<Type> getBranchTypes() {
    PersistenceManager pm = getPersistenceManager();
    Query q = pm.newQuery(Type.class);
    q.setFilter("type==:typeParam");
    try {
      List<Type> list = (List<Type>) q.execute(ReportTypeEnum.BRANCH);
      if (list.isEmpty()) {
        return null;
      }
      return list;
    } finally {
      q.closeAll();
      pm.close();
    }
  }

}
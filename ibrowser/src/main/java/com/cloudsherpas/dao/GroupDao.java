package com.cloudsherpas.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.cloudsherpas.domain.AppUser;
import com.cloudsherpas.domain.GoogleGroup;
import com.google.appengine.api.datastore.Key;

public class GroupDao extends BaseDao<GoogleGroup> {

	public GroupDao() {
		super(GoogleGroup.class);
	}

	public GoogleGroup getGroupByCode(String code) {
		PersistenceManager pm = getPersistenceManager();
		Query q = pm.newQuery(GoogleGroup.class);
		q.setFilter("code == codeParam");
		q.declareParameters("String codeParam");
		q.setRange(0,1);
		try {
			List<GoogleGroup> results = (List<GoogleGroup>) q.execute(code);
			if (!results.isEmpty()) {
				return results.get(0);
			}
		} finally {
			q.closeAll();
			pm.close();
		}
		return null;
	}

	public GoogleGroup getGroupByEmail(String email) {
		PersistenceManager pm = getPersistenceManager();
		Query q = pm.newQuery(GoogleGroup.class);
		q.setFilter("email == emailParam");
		q.declareParameters("String emailParam");
		q.setRange(0,1);
		try {
			List<GoogleGroup> results = (List<GoogleGroup>) q.execute(email.toLowerCase());
			if (!results.isEmpty()) {
				return results.get(0);
			}
		} finally {
			q.closeAll();
			pm.close();
		}
		return null;
	}
	
	public List<Key> getUserGroupsKey(AppUser user) {
		PersistenceManager pm = getPersistenceManager();
		Query q = pm.newQuery(GoogleGroup.class);
		q.setFilter("name == :nameParam");
		try {
			if(user.getGroups().isEmpty()){
				return null;
			}
			List<GoogleGroup> list = (List<GoogleGroup>) q.execute(user.getGroups());
			ArrayList<Key> keys = new ArrayList<Key>();
			
			if (list != null && !list.isEmpty()) {
				for (GoogleGroup gg : list) {
					keys.add(gg.getKey());
				}
				return keys;
			}else {
				return null;
			}
		} finally {
			q.closeAll();
			pm.close();
		}
	}

	public List<GoogleGroup> getReportGroup() {
		PersistenceManager pm = getPersistenceManager();
		Query q = pm.newQuery(GoogleGroup.class);
		q.setFilter("isSystem == false");
		try {
			List<GoogleGroup> list = (List<GoogleGroup>) q.execute();
			if (list == null || list.isEmpty()) {
				return null;
			}
			return list;
		} finally {
			q.closeAll();
			pm.close();
		}
		
	}	
}

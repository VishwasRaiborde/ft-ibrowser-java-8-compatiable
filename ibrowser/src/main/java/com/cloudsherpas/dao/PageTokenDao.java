package com.cloudsherpas.dao;

import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.cloudsherpas.domain.PageToken;
import com.google.inject.Inject;

public class PageTokenDao extends BaseDao<PageToken> {
	
	@Inject
	public PageTokenDao() {
		super(PageToken.class);
	}

	
	public PageToken getPageToken(String bucketName) {
		PersistenceManager pm = getPersistenceManager();
		Query q = pm.newQuery(PageToken.class);
		q.setFilter("bucketName == :bucketNameParam");
		q.setRange(0,1);
		
		try {
			List<PageToken> list = (List<PageToken>)q.execute(bucketName);
			
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		} finally {
			q.closeAll();
			pm.close();
		}
		return null;
	}
}
package com.cloudsherpas.dao;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.cloudsherpas.domain.News;

public class NewsDao extends  BaseDao<News>{
   public NewsDao(){
	   super(News.class);
   }

public News getLatestNews() {
	PersistenceManager pm = getPersistenceManager();
	Query q = pm.newQuery(News.class);
	q.setFilter("date <= :dateParam");
	q.setOrdering("date DESC");
   try {
		List<News> list = (List<News>)q.execute(new Date());
		if (list.isEmpty()) {
		 return	null ;
		}
		return list.get(0);
	} finally {
		q.closeAll();
		pm.close();
	}	
}
/*public void delete(Object key) {
	PersistenceManager pm = getPersistenceManager();
	Transaction txn = pm.currentTransaction();
	try {
		txn.begin();
		pm.deletePersistent(pm.getObjectById(News.class, key));
		txn.commit();
	} finally {
		if (txn.isActive()) {
			txn.rollback();
		}
		pm.close();
	}
}*/
   
   
}

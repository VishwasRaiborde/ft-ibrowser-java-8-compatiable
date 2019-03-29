package com.cloudsherpas.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.domain.Heading;
import com.cloudsherpas.domain.Report;
import com.google.inject.Inject;

@SuppressWarnings("unchecked")
public class BaseDao<T extends BaseEntity> {
	
	private final Class<T> clazz;
	
    protected Logger logger;
    protected PersistenceManagerFactory PMF;
    
    public BaseDao(Class<T> clazz) {
		this.clazz = clazz;
	}
    
    @Inject
    public void injectMembers(PersistenceManagerFactory PMF, Logger logger) {
    	this.PMF = PMF;
    	this.logger = logger;
    }

	public void persist(T entity) {
		PersistenceManager pm = getPersistenceManager();
		Transaction txn = pm.currentTransaction();
		try {
			txn.begin();
			if(entity.isNew()){
				entity.setCreatedDate(new Date());	
			}
			entity.setLastUpdatedDate(new Date());
			pm.makePersistent(entity);
			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
			pm.close();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

    public void persistAll(Collection<T> objects) {
    	PersistenceManager pm = getPersistenceManager();
    	pm.makePersistentAll(objects);
    }
    
    public T get(Object key) {
    	PersistenceManager pm = getPersistenceManager();
    	try {
    		return pm.getObjectById(clazz, key);
    	} finally {
    		pm.close();
    	}
    }
    
    public Object get(Class clazz, Object key) {
    	PersistenceManager pm = getPersistenceManager();
    	try {
    		return pm.getObjectById(clazz, key);
    	} finally {
    		pm.close();
    	}
    }
    
    public List<T> getAll() {
    	PersistenceManager pm = getPersistenceManager();
    	Query q = pm.newQuery(clazz);
    	try {
    		List<T> list = (List<T>)q.execute();
    		if (list.isEmpty()) {
    			return null;
    		}
    		return list;
    	} finally {
    		q.closeAll();
    		pm.close();
    	}
    }
    
    public void delete(Object key) {
    	PersistenceManager pm = getPersistenceManager();
    	Transaction txn = pm.currentTransaction();
    	try {
    		txn.begin();
    		pm.deletePersistent(pm.getObjectById(clazz, key));
    		txn.commit();
    	} finally {
    		if (txn.isActive()) {
    			txn.rollback();
    		}
    		pm.close();
    		
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}

    }

    public void delete(Class clazz, Object key) {
    	PersistenceManager pm = getPersistenceManager();
    	Transaction txn = pm.currentTransaction();
    	try {
    		txn.begin();
    		pm.deletePersistent(pm.getObjectById(clazz, key));
    		txn.commit();
    	} finally {
    		if (txn.isActive()) {
    			txn.rollback();
    		}
    		pm.close();
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public void deleteAll(List<T> entities) {
    	PersistenceManager pm = getPersistenceManager();
    	Transaction txn = pm.currentTransaction();
    	try {
    		txn.begin();
    		pm.deletePersistentAll(entities);
    		txn.commit();
    	} finally {
    		if (txn.isActive()) {
    			txn.rollback();
    		}
    		pm.close();
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }

    public List<T> getEntityList() {
    	PersistenceManager pm = getPersistenceManager();
    	Query q = pm.newQuery(clazz);
        q.setRange(0, GlobalConstants.PAGE_SIZE);
     	if (clazz.getName().equals(Report.class.getName())) {
			q.setOrdering("lastUpdatedDate DESC, title ASC"); 
		} else if (clazz.getName().equals(Heading.class.getName())) {
			q.setOrdering("lastUpdatedDate DESC, name ASC"); 
		} else {
			q.setOrdering("lastUpdatedDate DESC");
		}
  		try {
    		List<T> list = (List<T>)q.execute();
    		if (list.isEmpty()) {
    			list = null;
    		}
    		return list;
    	} finally {
    		q.closeAll();
    		pm.close();
    	}
    }

    public Integer getEntityCount(){
  	  PersistenceManager pm = getPersistenceManager();
  	  Query q = pm.newQuery(clazz);
  	  q.setResult("count(this)");
  	  Long count = null;
  	  try {
  		  count = (Long)q.execute();
  		  return count.intValue();
  	  } finally {
  		  pm.close();
  	  }
    }
    
    public List<T> getEntitiesByParam(String sortKey , String order ,String step) {
  	  PersistenceManager pm = getPersistenceManager();
  	  Query q = pm.newQuery(clazz);

  	  if(!"".equals(sortKey)){
  		  q.setOrdering(sortKey +" "+ order);
  	  }else{
		q.setOrdering("lastUpdatedDate DESC");
  	  }
  	  if(!"All".equals(step)){
  		
  	   if(!"".equals(step)){
  		 Integer end = Integer.valueOf(step) * GlobalConstants.PAGE_SIZE;
  		 q.setRange(end-GlobalConstants.PAGE_SIZE, end);
  	   }else{
  		  q.setRange(0,GlobalConstants.PAGE_SIZE);
  	   }
  	  }
  	  try {
  		  List<T> list = (List<T>)q.execute();
  		  if (list.isEmpty()) {
  			  list = null;
  		  }
  		  return list;
  	  } finally {
  		  pm.close();
  	  }
    }
    public List<T> getAllEntityList() {
		 PersistenceManager pm = getPersistenceManager();
	  	 Query q = pm.newQuery(clazz);
	  	 q.setOrdering("lastUpdatedDate DESC");
	  	 try {
	  		  List<T> list = (List<T>)q.execute();
	  		  if (list.isEmpty()) {
	  			  list = null;
	  		  }
	  		  return list;
	  	  } finally {
	  		  pm.close();
	  	  } 
    }
    
    public PersistenceManager getPersistenceManager() {
    	return PMF.getPersistenceManager();
    }

	public void setPMF(PersistenceManagerFactory pMF) {
		this.PMF = pMF;
	}
    
}

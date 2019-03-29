package com.cloudsherpas.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.cloudsherpas.domain.AppUser;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.inject.Inject;

public class UserDao extends BaseDao<AppUser> {
	private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
	private final UserService userService;
	
	@Inject
	public UserDao(UserService userService) {
		super(AppUser.class);
		this.userService = userService;
	}

	public AppUser getCurrentUser() {
		User user = userService.getCurrentUser();
		AppUser appUser = null;
		logger.log(Level.INFO,"Got current user1: " + user.getEmail());
		try {
			appUser = getUser(user.getEmail());
			logger.log(Level.INFO,"Got current user2: " + appUser.getEmail());
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Exception fetching user by email", e);
		}
		
		return appUser;
	}
	
	public AppUser getUser(String email) {
		PersistenceManager pm = getPersistenceManager();
		Query q = pm.newQuery(AppUser.class);
		q.setFilter("email == emailParam");
		q.declareParameters("String emailParam");
		
		try {
			List<AppUser> list = (List<AppUser>)q.execute(email);
			
			if (list != null && !list.isEmpty()) {
				logger.log(Level.INFO,"User exists on datastore.");
				return list.get(0);
			}
		} finally {
			q.closeAll();
			pm.close();
		}
		logger.log(Level.INFO,"User does not exist on datastore.");
		return null;
	}

	/*public void saveUserFavouriteReport(Report favouriteReport, Boolean isFavourite) {
		AppUser user = getCurrentUser();
			if (isFavourite) {
				user.getFavouriteReportsKey().add(favouriteReport.getKey());
			} else {
				user.getFavouriteReportsKey().remove(favouriteReport.getKey());
			}
		persist(user);
	}
    public List<Key> getUserFavouriteReportKey() {
		return	getCurrentUser().getFavouriteReportsKey();
	} */
	
	public void persist(AppUser entity) {
		PersistenceManager pm = getPersistenceManager();
		try {
			if(entity.isNew()){
				entity.setCreatedDate(new Date());	
			}
			entity.setLastUpdatedDate(new Date());
			pm.makePersistent(entity);
		} finally {
			pm.close();
		}
	}
}

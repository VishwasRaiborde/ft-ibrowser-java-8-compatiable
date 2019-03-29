package com.cloudsherpas.http.app;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.cloudsherpas.dao.TypeDao;
import com.cloudsherpas.dao.UserDao;
import com.cloudsherpas.domain.AppUser;
import com.cloudsherpas.domain.Type;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.cloudsherpas.utils.StrUtils;
import com.google.inject.Inject;

public class UserHandler {
   private final UserDao userDao;
   private final TypeDao typeDao;
   private final MemcacheService cache;
   private final ApiHttpUtils utils;
   private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

   
   @Inject
   public  UserHandler(UserDao userDao,TypeDao typeDao, MemcacheService cache, ApiHttpUtils utils){
	   this.userDao = userDao;
	   this.typeDao = typeDao;
	   this.cache = cache;
	   this.utils = utils;
   }
   public void get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	   AppUser currentUser = userDao.getCurrentUser();
	   currentUser.setIsAdmin(currentUser.isAdmin());
	   String userBranch = (String)cache.get(currentUser.getEmail());
	   Expiration expiration = Expiration.byDeltaSeconds(60 * 5); //Every 5 minute user's branch will be updated
	   if(StrUtils.isEmpty(userBranch)){
		   List<Type> types = typeDao.getBranchTypes();
		   List<String> userGroups = userDao.getCurrentUser().getGroups();
		   if(types!=null && userGroups!=null && !userGroups.isEmpty()){
			   for(String userGroupName : userGroups){
				   for(Type type : types){
					   if(userGroupName.equals(type.getGroupName())){
						   userBranch = userGroupName;
					   }
				   }
			    }
			  cache.put(currentUser.getEmail(), userBranch,expiration);
	       }
	    }
	   currentUser.setUserBranch(userBranch);
	   logger.log(Level.INFO,"Detected Current User: "+currentUser.getEmail());
	   utils.sendResponse(resp, currentUser);
	 }
   public void post(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	   
   }
}

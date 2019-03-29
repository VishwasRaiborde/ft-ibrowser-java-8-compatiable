package com.cloudsherpas.http.app;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.GoogleGroupMemberDao;
import com.cloudsherpas.dao.GroupDao;
import com.cloudsherpas.dao.UserDao;
import com.cloudsherpas.domain.AppUser;
import com.cloudsherpas.http.CsFilter;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AppAuthFilter extends CsFilter {

	public final Logger logger;
	private final UserService userService;
	public final UserDao userDao;
	public final GroupDao groupDao;
	public final GoogleGroupMemberDao memberDao;
	
	@Inject
	public AppAuthFilter(Logger logger, UserService userService, UserDao userDao,GroupDao groupDao, GoogleGroupMemberDao memberDao) {
		super(userDao,groupDao,memberDao);
		this.logger = logger;
		this.userService = userService;
		this.userDao = userDao;
		this.groupDao = groupDao;
		this.memberDao = memberDao;
	}
		
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		super.doFilter(req, resp, chain);
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		logger.log(Level.INFO,"User Agent: "+ request.getHeader("User-Agent"));
		//check valid ip range
		if (!isValidIPRange(request)) {
			response.sendRedirect("/noAccess.html");
		    return;
		}
		
		User user = userService.getCurrentUser();
		
		if(user != null){
			String email = user.getEmail();
			logger.log(Level.INFO,"user trying to connect :" + email);
			AppUser appUser = userDao.getUser(email);
			if (appUser == null) {
				if (email.toLowerCase().matches(USER_AUTOCREATE_WHITELIST_REGEX)) {
					logger.log(Level.INFO, "To create a new object for user");
					appUser = new AppUser();
					appUser.setEmail(email);
					logger.log(Level.INFO, "To sync user groups");
					syncUsersGroups(appUser);
				}
			}
			
			if (appUser != null) {
				logger.log(Level.INFO, "User exists");
				logger.log(Level.INFO,"Got current user: " + appUser.getEmail());
				if(appUser.getLastUpdatedDate()==null || moreThanOneMinute(appUser.getLastUpdatedDate())){
					appUser.setEmail(email);
					syncUsersGroups(appUser);						
				}
				chain.doFilter(request, response);
			} else {
				response.sendRedirect("/error.html");
			}
		} else if (request.getRequestURI().startsWith(TASK_QUEUE+"/")) {
			chain.doFilter(req, resp);	
		} else {
			response.sendRedirect(userService.createLoginURL(request.getRequestURI())); // This is for Localhost
			//response.sendRedirect(LOGIN); // This is production URL
		}
	}


	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}
	
	public boolean moreThanOneMinute(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE,-10);
		return cal.getTime().after(date);
	}
}

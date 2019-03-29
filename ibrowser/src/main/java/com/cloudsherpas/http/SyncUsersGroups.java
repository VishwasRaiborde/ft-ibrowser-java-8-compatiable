package com.cloudsherpas.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.dao.UserDao;
import com.cloudsherpas.domain.AppUser;
import com.cloudsherpas.http.app.AppAuthFilter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("serial")
public class SyncUsersGroups extends HttpServlet implements GlobalConstants {
	

	private final AppAuthFilter filter;
	public final UserDao userDao;
	
	@Inject
	public SyncUsersGroups(AppAuthFilter filter, UserDao userDao){
		this.filter = filter;
		this.userDao = userDao;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String email = req.getParameter("email");
		if(email!=null){
			AppUser user = userDao.getUser(email);
			if(user == null){
				user = new AppUser();
				user.setEmail(email);
			}
			if(user !=null){
				filter.syncUsersGroups(user);
			}
		}
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}
	
}

package com.cloudsherpas.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Singleton;

@Singleton
public class LogoutServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
//		resp.sendRedirect(UserServiceFactory.getUserService()
//				.createLogoutURL(GlobalConstants.LOGIN));
    resp.sendRedirect(UserServiceFactory.getUserService().createLogoutURL(req.getRequestURI()));
  }
}

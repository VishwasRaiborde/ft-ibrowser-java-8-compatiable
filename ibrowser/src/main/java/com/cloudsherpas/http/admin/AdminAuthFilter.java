package com.cloudsherpas.http.admin;

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
public class AdminAuthFilter extends CsFilter {
  private final Logger logger;
  private final UserService userService;
  private final UserDao userDao;
  private final GroupDao groupDao;
  private final GoogleGroupMemberDao memberDao;

  @Inject
  public AdminAuthFilter(Logger logger, UserService userService, UserDao userDao, GroupDao groupDao,
      GoogleGroupMemberDao memberDao) {
    super(userDao, groupDao, memberDao);
    this.logger = logger;
    this.userService = userService;
    this.userDao = userDao;
    this.groupDao = groupDao;
    this.memberDao = memberDao;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) resp;

    // check valid ip range
    if (!isValidIPRange(request)) {

      response.sendRedirect("/noAccess.html");
      return;
    }

    User user = userService.getCurrentUser();

    if (user != null) {
      String email = user.getEmail();
      AppUser appUser = userDao.getUser(email);
      if (appUser == null) {
        if (email.toLowerCase().matches(USER_AUTOCREATE_WHITELIST_REGEX)) {
          appUser = new AppUser();
          appUser.setEmail(email);
          syncUsersGroups(appUser);
        }
      }

      if (appUser != null) {
        if (appUser.isAdmin()) {
          if (appUser.getLastUpdatedDate() == null
              || moreThanOneMinute(appUser.getLastUpdatedDate())) {
            syncUsersGroups(appUser);
          }
          logger.log(Level.INFO, "User is admin:" + user.getEmail());
          chain.doFilter(request, response);
        } else if (request.getRequestURI().startsWith("/noAccess.html")) {
          chain.doFilter(request, response);
        } else {
          // response.sendRedirect(LOGIN);
          response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
        }
      } else {
        // response.sendRedirect(LOGIN);
        response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
      }
    } else {
      response.sendRedirect(userService.createLoginURL(request.getRequestURI())); // This is for
                                                                                  // Localhost
      // response.sendRedirect(LOGIN); // This is production URL
    }
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {
  }

  @Override
  public void destroy() {
  }

  /*
   * public boolean moreThanOneDay(Date date) { Calendar cal = new GregorianCalendar();
   * cal.setTime(new Date()); cal.add(Calendar.DAY_OF_YEAR,-1); return cal.getTime().after(date); }
   */

  public boolean moreThanOneMinute(Date date) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(new Date());
    cal.add(Calendar.MINUTE, -10);
    return cal.getTime().after(date);
  }
}
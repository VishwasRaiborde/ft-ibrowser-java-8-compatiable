package com.cloudsherpas.http;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.dao.GroupDao;
import com.cloudsherpas.domain.GoogleGroup;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("serial")
public class CustomServlet extends HttpServlet implements GlobalConstants {

  private final GroupDao groupDao;
  private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

  @Inject
  public CustomServlet(GroupDao groupDao) {
    this.groupDao = groupDao;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    logger.log(Level.INFO, "Begin: Getting dublicate groups. Time:" + new Date());
    logger.log(Level.INFO, "Begin: Getting all groups. Time:" + new Date());
    List<GoogleGroup> groups = groupDao.getAll();

    logger.log(Level.INFO, "Finish: Getting all groups. Time:" + new Date());

    Map<String, Integer> map = new HashMap<String, Integer>();
    for (GoogleGroup group : groups) {
      Integer count = map.get(group.getEmail());
      if (count == null)
        count = 0;
      map.put(group.getEmail(), count + 1);
    }
    for (String key : map.keySet()) {
      Integer count = map.get(key);
      if (count > 1) {
        logger.log(Level.INFO, "" + key + " : " + count);
      }

    }
    logger.log(Level.INFO, "Finish: Getting dublicate groups. Time:" + new Date());
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    super.doPost(req, resp);
  }

}

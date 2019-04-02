package com.cloudsherpas.http.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.HeadingDao;
import com.cloudsherpas.dao.NewsDao;
import com.cloudsherpas.domain.Heading;
import com.cloudsherpas.domain.News;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.google.inject.Inject;

public class HomeHandler {
  private final HeadingDao headingDao;
  private final NewsDao newsDao;
  private final ApiHttpUtils utils;
  private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

  @Inject
  public HomeHandler(HeadingDao headingDao, NewsDao newsDao, ApiHttpUtils utils) {
    this.headingDao = headingDao;
    this.newsDao = newsDao;
    this.utils = utils;
  }

  public String get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    logger.log(Level.INFO, "User Agent: " + req.getHeader("User-Agent"));
    News news = newsDao.getLatestNews();
    req.setAttribute("content_holder", "app/news.html");
    req.setAttribute("news", news);
    return "OK";
  }

  public String post(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    List<Heading> list = headingDao.getHeadingByOrder();
    List<String> headings = new ArrayList<String>();
    for (Heading heading : list) {
      headings.add(heading.getName());
    }
    logger.log(Level.INFO, "Available Headings: " + headings.toString());
    utils.sendResponse(resp, list);
    return "OK";
  }

}

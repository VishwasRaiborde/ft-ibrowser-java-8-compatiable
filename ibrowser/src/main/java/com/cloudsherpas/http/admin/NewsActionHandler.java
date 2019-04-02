package com.cloudsherpas.http.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.NewsDao;
import com.cloudsherpas.domain.News;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.cloudsherpas.utils.DateUtils;
import com.cloudsherpas.utils.StrUtils;
import com.google.inject.Inject;

public class NewsActionHandler {
  private final NewsDao newsDao;
  private final ApiHttpUtils utils;

  @Inject
  public NewsActionHandler(NewsDao newsDao, ApiHttpUtils utils) {
    this.newsDao = newsDao;
    this.utils = utils;
  }

  public String get(String key, HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    News news = null;
    if (!StrUtils.isEmpty(key)) {
      news = newsDao.get(key);
    } else {
      news = new News();
    }
    req.setAttribute("news", news);
    return "OK";
  }

  public String post(String key, HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    try {
      News news = null;
      if (key != null && !key.isEmpty()) {
        news = newsDao.get(key);
      } else {
        news = new News();
      }
      news.setTitle(req.getParameter("title"));
      news.setDescription(req.getParameter("description"));
      news.setDate(DateUtils.getDateFormat(req.getParameter("date")));
      newsDao.persist(news);
      return "SUCCESS";
    } catch (Exception e) {
      // utils.sendError(resp, 500, "An unexpected exception occurred: " +
      // e.getClass().getSimpleName() + ": " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage(), e);
      // return null;
    }
  }
}

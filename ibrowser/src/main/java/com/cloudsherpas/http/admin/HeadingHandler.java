package com.cloudsherpas.http.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.HeadingDao;
import com.cloudsherpas.domain.Heading;
import com.google.inject.Inject;

public class HeadingHandler {
  private final HeadingDao headingDao;

  @Inject
  public HeadingHandler(HeadingDao headingDao) {
    this.headingDao = headingDao;
  }

  public String get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    List<Heading> list = headingDao.getHeadingByOrder();
    req.setAttribute("content_holder", "admin/heading.html");
    req.setAttribute("headings", list);
    return "OK";
  }
}

package com.cloudsherpas.http.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.NewsDao;
import com.cloudsherpas.domain.News;
import com.cloudsherpas.request.NewsRequest;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.google.inject.Inject;

public class NewsHandler {
   private final NewsDao newsDao;
   private final ApiHttpUtils utils;
   
   @Inject
   public NewsHandler(NewsDao newsDao,ApiHttpUtils utils){
	   this.newsDao = newsDao;
	   this.utils = utils;
   }  
   public String get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		List<News> list = newsDao.getEntityList();
		Integer listCount = newsDao.getEntityCount();
		if (list != null && !list.isEmpty()) {
			for (News news : list) {
				news.setKeyAsString();
			}
		}
		req.setAttribute("content_holder", "admin/news.html");
		req.setAttribute("news", list);
		req.setAttribute("count", listCount);
		return "OK";
	}
   public void post(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		NewsRequest nr = (NewsRequest)utils.readPost(req, NewsRequest.class);
		List<News> list = newsDao.getEntitiesByParam(nr.sortField, nr.order,nr.step);
		if (list != null && !list.isEmpty()) {
			for (News news : list) {
				news.setKeyAsString();
			}
		}
		utils.sendResponse(resp, list);
	}
}

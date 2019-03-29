package com.cloudsherpas.http.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.HeadingDao;
import com.cloudsherpas.domain.Heading;
import com.cloudsherpas.request.CreateHeadingRequest;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.google.inject.Inject;

public class HeadingActionHandler {

	private final HeadingDao headingDao;
	private final ApiHttpUtils utils;
	
	@Inject
	public HeadingActionHandler(HeadingDao headingDao, ApiHttpUtils utils) {
		this.headingDao = headingDao;
		this.utils = utils;
	} 
	
	public String get(String key,HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Heading heading = null;
		if(key!=null && !key.isEmpty()){
			heading = headingDao.get(key);
		}else{
			heading  = new Heading();
		}
		req.setAttribute("heading", heading);
		return "OK";		
	}
	
	public String post(String key, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			CreateHeadingRequest hr = (CreateHeadingRequest)utils.readPost(req, CreateHeadingRequest.class);
			ArrayList<Integer> ids = null;
			if(hr!=null) {
			 if(hr.ids !=null  &&  !hr.ids.isEmpty()){
				ids = new ArrayList<Integer>(hr.ids.size());
				ids = hr.ids;
				List<Heading> list  =  headingDao.getHeadingByOrder();
				if (list != null && !list.isEmpty()) {
					for(int i=0;i< list.size();i++){
						list.get(ids.get(i)).setOrder(i);
					}
					headingDao.persistAll(list);
				 }
			 } 
			}else {
				Integer lastOrder =  headingDao.getEntityCount();
				
				Heading heading = null;
				if (key != null && !key.isEmpty()) {
					heading = headingDao.get(key);
				}
				if (heading == null) {
					heading = new Heading();
					heading.setOrder(lastOrder == 0 ? lastOrder : (lastOrder-1) + 1);
				}
				heading.setName(req.getParameter("name"));
				headingDao.persist(heading);
		  }
			return "SUCCESS";
		} catch (Exception e) {
			utils.sendError(resp, 500, "An unexpected exception occurred: " + e.getClass().getSimpleName() + ": " + e.getMessage());
			return null;
		}
	}
}

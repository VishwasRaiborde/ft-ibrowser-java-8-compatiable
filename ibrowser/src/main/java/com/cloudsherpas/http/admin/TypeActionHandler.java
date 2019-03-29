package com.cloudsherpas.http.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.TypeDao;
import com.cloudsherpas.domain.Type;
import com.cloudsherpas.enums.ReportTypeEnum;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.google.inject.Inject;

public class TypeActionHandler {
	private final ApiHttpUtils utils;
	private final TypeDao typeDao;
	
	@Inject
	public TypeActionHandler(ApiHttpUtils utils,TypeDao typeDao) {
		this.utils = utils;
		this.typeDao = typeDao;
	}

	public String get(String key, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Type type = null;
		
		if (key != null && !key.isEmpty()) {
			type = typeDao.get(key);
		} else {
			type = new Type();
		}
		List<ReportTypeEnum> typeList = new ArrayList<ReportTypeEnum>();
		typeList.add(ReportTypeEnum.DIVISIONAL);
		typeList.add(ReportTypeEnum.BUYING);
		typeList.add(ReportTypeEnum.BRANCH);
		
		type.setTypeList(typeList);
		
		req.setAttribute("type", type);
		return "OK";
	}
	
	public String post(String key, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
		String name = req.getParameter("name");
		String groupName = req.getParameter("groupName");
		String groupCode = req.getParameter("groupCode");
		Type type = null;

		if (key != null && !key.isEmpty()) {
			type = typeDao.get(key);
		}
		
		if (type == null) {
			type = new Type();
		}
		type.setType(ReportTypeEnum.buildByCode(name));
		type.setGroupName(groupName);
		type.setGroupCode(ReportTypeEnum.DIVISIONAL.toString().equals(name) ? "000" : groupCode);
		typeDao.persist(type);
		return "SUCCESS";
		}catch(Exception e){
		  utils.sendError(resp, 500, "An unexpected exception occurred: " + e.getClass().getSimpleName() + ": " + e.getMessage());
		  return null;	
		}
	}	
}

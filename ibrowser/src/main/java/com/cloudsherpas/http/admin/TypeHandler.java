package com.cloudsherpas.http.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.TypeDao;
import com.cloudsherpas.domain.Type;
import com.cloudsherpas.request.CreateTypeRequest;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.google.inject.Inject;

public class TypeHandler {

  private final TypeDao typeDao;
  private final ApiHttpUtils utils;

  @Inject
  public TypeHandler(TypeDao typeDao, ApiHttpUtils utils) {
    this.typeDao = typeDao;
    this.utils = utils;
  }

  public String get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    List<Type> list = typeDao.getEntityList();
    Integer listCount = typeDao.getEntityCount();
    req.setAttribute("content_holder", "admin/type.html");
    req.setAttribute("types", list);
    req.setAttribute("count", listCount);
    return "OK";
  }

  public void post(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    CreateTypeRequest hr = (CreateTypeRequest) utils.readPost(req, CreateTypeRequest.class);
    List<Type> list = typeDao.getEntitiesByParam(hr.sortField, hr.order, hr.step);
    if (list != null && !list.isEmpty()) {
      for (Type type : list) {
        type.setNameAsString(type.getType().getName());
        type.setKeyAsString();
      }
    }
    utils.sendResponse(resp, list);
  }
}

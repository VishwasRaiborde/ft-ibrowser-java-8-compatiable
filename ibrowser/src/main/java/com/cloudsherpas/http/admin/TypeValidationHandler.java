package com.cloudsherpas.http.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.dao.TypeDao;
import com.cloudsherpas.domain.Type;
import com.cloudsherpas.request.TypeValidationRequest;
import com.cloudsherpas.utils.ApiHttpUtils;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.inject.Inject;

public class TypeValidationHandler {

  private final TypeDao typeDao;
  private final ApiHttpUtils utils;

  @Inject
  public TypeValidationHandler(TypeDao typeDao, ApiHttpUtils utils) {
    this.typeDao = typeDao;
    this.utils = utils;
  }

  public void get(HttpServletRequest req, HttpServletResponse resp) throws IOException {

  }

  public void post(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    TypeValidationRequest tr = (TypeValidationRequest) utils.readPost(req,
        TypeValidationRequest.class);
    String key = tr.key;
    String groupCode = tr.groupCode;
    String groupName = tr.groupName;
    if ("".equals(key) && groupCode != null && groupName != null) {
      Type reportType = typeDao.getTypeByParam(groupCode, "groupCode");
      Type reportType1 = typeDao.getTypeByParam(groupName, "groupName");
      if (reportType == null && reportType1 == null) {
        utils.sendResponse(resp, "bothNew");
      } else if (reportType == null && reportType1 != null) {
        utils.sendResponse(resp, "groupNameExist");
      } else if (reportType != null && reportType1 == null) {
        utils.sendResponse(resp, "groupCodeExist");
      } else {
        utils.sendResponse(resp, "bothExist");
      }
    } else if (!"".equals(key)) {
      Type editableType = typeDao.getTypeByParam(groupCode, "groupCode");
      Type editableType1 = typeDao.getTypeByParam(groupName, "groupName");
      if (editableType != null && editableType1 != null) {
        if (KeyFactory.keyToString(editableType.getKey()).equals(key)
            && KeyFactory.keyToString(editableType1.getKey()).equals(key)) {
          utils.sendResponse(resp, "bothNew");
        } else if (KeyFactory.keyToString(editableType.getKey()).equals(key)
            && !KeyFactory.keyToString(editableType1.getKey()).equals(key)) {
          utils.sendResponse(resp, "groupNameExist");
        } else if (!KeyFactory.keyToString(editableType.getKey()).equals(key)
            && KeyFactory.keyToString(editableType1.getKey()).equals(key)) {
          utils.sendResponse(resp, "groupCodeExist");
        } else {
          utils.sendResponse(resp, "bothExist");
        }
      } else if (editableType != null && editableType1 == null) {
        String valid = "groupCodeExist";
        if (KeyFactory.keyToString(editableType.getKey()).equals(key)) {
          valid = "bothNew";
        }
        utils.sendResponse(resp, valid);

      } else if (editableType == null && editableType1 != null) {
        String valid = "groupNameExist";
        if (KeyFactory.keyToString(editableType1.getKey()).equals(key)) {
          valid = "bothNew";
        }
        utils.sendResponse(resp, valid);
      } else {
        utils.sendResponse(resp, "bothNew");
      }
    }

  }
}

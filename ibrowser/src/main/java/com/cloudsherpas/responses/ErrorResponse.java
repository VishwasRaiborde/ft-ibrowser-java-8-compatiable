package com.cloudsherpas.responses;

import java.util.HashMap;

public class ErrorResponse {

  public HashMap<String, Object> error = new HashMap<String, Object>();

  public ErrorResponse() {
  }

  public ErrorResponse(Integer code, String message) {
    error.put("code", code);
    error.put("message", message);
  }

}

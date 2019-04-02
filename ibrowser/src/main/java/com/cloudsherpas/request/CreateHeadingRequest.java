package com.cloudsherpas.request;

import java.util.ArrayList;

public class CreateHeadingRequest {
  public String key;
  public String name;
  public ArrayList<Integer> ids;

  public CreateHeadingRequest() {
    super();
  }

  public CreateHeadingRequest(String key, String name, ArrayList<Integer> ids) {
    super();
    this.key = key;
    this.name = name;
    this.ids = ids;
  }
}

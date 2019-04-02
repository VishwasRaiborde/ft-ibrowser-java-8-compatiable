package com.cloudsherpas.enums;

public enum FrequencyEnum {
  DAILY("DAILY", "Daily"), WEEKLY("WEEKLY", "Weekly");

  private String code;
  private String name;

  private FrequencyEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public static FrequencyEnum buildByCode(String code) {
    if ("DAILY".equals(code)) {
      return DAILY;
    } else if ("WEEKLY".equals(code)) {
      return WEEKLY;
    } else {
      throw new IllegalArgumentException("Frequency not found");
    }
  }

}

package com.cloudsherpas.enums;

public enum ReportTypeEnum {
  DIVISIONAL("DIVISIONAL", "Divisional"), BUYING("BUYING", "Buying Office"),
  BRANCH("BRANCH", "Branch");

  private String code;
  private String name;

  private ReportTypeEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public static ReportTypeEnum buildByCode(String code) {
    if ("DIVISIONAL".equals(code)) {
      return DIVISIONAL;
    } else if ("BUYING".equals(code)) {
      return BUYING;
    } else if ("BRANCH".equals(code)) {
      return BRANCH;
    } else {
      throw new IllegalArgumentException("Report type not found");
    }
  }
}
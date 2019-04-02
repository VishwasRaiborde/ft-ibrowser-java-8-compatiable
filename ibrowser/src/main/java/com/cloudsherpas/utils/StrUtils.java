package com.cloudsherpas.utils;

public class StrUtils {

  public static boolean isEmpty(String value) {
    return isEmpty(value, false);
  }

  public static boolean isEmpty(String value, boolean withTrim) {
    if (value == null) {
      return true;
    }
    if (value.isEmpty()) {
      return true;
    }
    if (withTrim) {
      if (value.trim().isEmpty()) {
        return true;
      }
    }
    if (value.equals("null")) {
      return true;
    }
    return false;
  }

  public static String removeStringLastChar(String str, int countOfChar) {
    return str.substring(0, str.length() - countOfChar);
  }

  public static String removeStringFirstChar(String s) {
    return s.substring(1);
  }

}

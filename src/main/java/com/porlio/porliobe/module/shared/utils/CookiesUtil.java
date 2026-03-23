package com.porlio.porliobe.module.shared.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookiesUtil {

  public static void store(String name, String value, int maxAge, String path, HttpServletResponse response) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);
    cookie.setPath(path);
    response.addCookie(cookie);
  }

  public static String getValue(String name, Cookie[] cookies) {
    if (cookies == null) {
      return null;
    }
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(name)) {
        return cookie.getValue();
      }
    }
    return null;
  }

  public static void clear(String name, String path, HttpServletResponse response) {
    Cookie cookie = new Cookie(name, null);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0);
    cookie.setPath(path);
    response.addCookie(cookie);
  }
}

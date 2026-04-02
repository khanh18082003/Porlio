package com.porlio.porliobe.module.shared.utils;

import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.shared.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j(topic = "SECURITY_UTIL")
public class SecurityUtil {
  private SecurityUtil() {
    /* This utility class should not be instantiated */
  }


  public static String getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      log.warn("No authenticated user found in security context");
      throw new AppException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }
    String userId = auth.getName();

    if (userId == null) {
      log.error("Authenticated principal does not contain user details");
      throw new AppException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }
    return userId;
  }
}

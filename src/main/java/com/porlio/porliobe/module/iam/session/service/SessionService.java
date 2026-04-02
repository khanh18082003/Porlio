package com.porlio.porliobe.module.iam.session.service;

import com.nimbusds.jose.JOSEException;
import com.porlio.porliobe.module.iam.session.dto.response.SessionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Set;

public interface SessionService {

  SessionResponse refreshToken(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse
  ) throws ParseException, JOSEException;

  SessionResponse createSessionForUser(
      String userId,
      Set<String> authorities,
      HttpServletResponse response);

  void logout(String token, HttpServletRequest request, HttpServletResponse response)
      throws ParseException, JOSEException;
}

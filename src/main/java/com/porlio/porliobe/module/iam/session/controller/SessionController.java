package com.porlio.porliobe.module.iam.session.controller;

import com.nimbusds.jose.JOSEException;
import com.porlio.porliobe.module.iam.session.dto.response.SessionResponse;
import com.porlio.porliobe.module.iam.session.service.SessionService;
import com.porlio.porliobe.module.shared.data.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.ParseException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.prefix}/sessions")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "AUTH_CONTROLLER")
public class SessionController {

  SessionService sessionService;

  @PostMapping("/refresh")
  ApiResponse<SessionResponse> refreshToken(HttpServletRequest request,
      HttpServletResponse response) throws ParseException, JOSEException {
    log.info("Received token refresh request");
    return ApiResponse.ok(sessionService.refreshToken(request, response));
  }

  @PostMapping("/logout")
  ApiResponse<Void> logout(
      @RequestHeader("Authorization") String authHeader,
      HttpServletRequest request,
      HttpServletResponse response
  )
      throws ParseException, JOSEException {
    log.info("Logout");
    String token = authHeader.replace("Bearer ", "");
    sessionService.logout(token, request, response);
    return ApiResponse.noContent();
  }
}

package com.porlio.porliobe.module.iam.auth.controller;

import com.porlio.porliobe.module.iam.auth.dto.request.AuthenticationRequest;
import com.porlio.porliobe.module.iam.auth.service.AuthenticationService;
import com.porlio.porliobe.module.iam.session.dto.response.SessionResponse;
import com.porlio.porliobe.module.shared.data.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.prefix}/auth")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "AUTH_CONTROLLER")
public class AuthenticationController {

  AuthenticationService authenticationService;

  @PostMapping
  ApiResponse<SessionResponse> login(
      @RequestBody @Valid AuthenticationRequest request,
      HttpServletResponse response) {
    log.info("Received authentication request for email: {}", request.email());
    return ApiResponse.ok(authenticationService.authenticate(request, response));
  }
}

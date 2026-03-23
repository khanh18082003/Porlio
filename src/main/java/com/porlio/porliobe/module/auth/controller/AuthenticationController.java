package com.porlio.porliobe.module.auth.controller;

import com.nimbusds.jose.JOSEException;
import com.porlio.porliobe.module.auth.dto.request.AuthenticationRequest;
import com.porlio.porliobe.module.auth.dto.request.ResendVerificationTokenRequest;
import com.porlio.porliobe.module.auth.dto.request.VerificationCodeRequest;
import com.porlio.porliobe.module.auth.dto.response.AuthenticationResponse;
import com.porlio.porliobe.module.auth.dto.response.VerificationCodeResponse;
import com.porlio.porliobe.module.auth.service.AuthenticationService;
import com.porlio.porliobe.module.shared.data.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.text.ParseException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
  ApiResponse<AuthenticationResponse> login(
      @RequestBody @Valid AuthenticationRequest request,
      HttpServletResponse response) {
    log.info("Received authentication request for email: {}", request.email());
    return ApiResponse.ok(authenticationService.authenticate(request, response));
  }

  @PostMapping("/refresh")
  ApiResponse<AuthenticationResponse> refreshToken(HttpServletRequest request,
      HttpServletResponse response) throws ParseException, JOSEException {
    log.info("Received token refresh request");
    return ApiResponse.ok(authenticationService.refreshToken(request, response));
  }

  @PostMapping("/logout")
  ApiResponse<Void> logout(@RequestHeader("Authorization") String authHeader,
      HttpServletRequest request, HttpServletResponse response)
      throws ParseException, JOSEException {
    log.info("Logout");
    String token = authHeader.replace("Bearer ", "");
    authenticationService.logout(token, request, response);
    return ApiResponse.noContent();
  }

  @PostMapping("/verification-code")
  ApiResponse<VerificationCodeResponse> sendVerificationCode(
      @RequestBody @Valid VerificationCodeRequest request) {
    log.info("Received request to send verification code for email: {}", request.email());
    return ApiResponse.ok(authenticationService.verifyCode(request));
  }

  @PostMapping("/resend-verification-code")
  ApiResponse<Void> resendCode(
      @RequestBody @Valid ResendVerificationTokenRequest request) {
    log.info("Received request to resend verification code for email: {}", request.email());
    authenticationService.resendVerificationCode(request);
    return ApiResponse.ok();
  }
}

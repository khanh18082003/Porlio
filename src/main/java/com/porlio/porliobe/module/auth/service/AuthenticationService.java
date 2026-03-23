package com.porlio.porliobe.module.auth.service;

import com.nimbusds.jose.JOSEException;
import com.porlio.porliobe.module.auth.dto.request.AuthenticationRequest;
import com.porlio.porliobe.module.auth.dto.request.ResendVerificationTokenRequest;
import com.porlio.porliobe.module.auth.dto.request.VerificationCodeRequest;
import com.porlio.porliobe.module.auth.dto.response.AuthenticationResponse;
import com.porlio.porliobe.module.auth.dto.response.IntrospectResponse;
import com.porlio.porliobe.module.auth.dto.response.VerificationCodeResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.ParseException;

public interface AuthenticationService {

  AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response);

  AuthenticationResponse refreshToken(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse
  ) throws ParseException, JOSEException;

  void logout(String token, HttpServletRequest request, HttpServletResponse response)
      throws ParseException, JOSEException;

  VerificationCodeResponse verifyCode(VerificationCodeRequest request);

  void resendVerificationCode(ResendVerificationTokenRequest request);
}

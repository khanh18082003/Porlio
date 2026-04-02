package com.porlio.porliobe.module.iam.auth.service.impl;

import com.porlio.porliobe.module.iam.auth.dto.request.AuthenticationRequest;
import com.porlio.porliobe.module.iam.auth.service.AuthenticationService;
import com.porlio.porliobe.module.iam.session.dto.response.SessionResponse;
import com.porlio.porliobe.module.iam.session.service.SessionService;
import com.porlio.porliobe.module.iam.user.entity.CustomUserDetail;
import com.porlio.porliobe.module.iam.user.entity.User;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.shared.exception.AppException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "AUTHENTICATION_SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

  AuthenticationManager authManager;
  SessionService sessionService;

  @Override
  public SessionResponse authenticate(AuthenticationRequest request,
      HttpServletResponse response) {
    log.debug("Attempting authentication for user: {}", request.email());
    // Step 1: Authenticate user credentials
    var auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    // Step 2: Extract user details and authorities
    CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
    if (userDetail == null) {
      log.warn("Authentication failed for email: {}", request.email());
      throw new AppException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }
    User user = userDetail.getUser();

    // Step 3: Generate JWT tokens
    Set<String> authorities = extractAuthorities(userDetail);

    return sessionService.createSessionForUser(user.getId().toString(), authorities, response);
  }

  private Set<String> extractAuthorities(CustomUserDetail userDetail) {
    return userDetail.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
  }
}

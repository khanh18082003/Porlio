package com.porlio.porliobe.module.auth.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.porlio.porliobe.module.auth.dto.request.AuthenticationRequest;
import com.porlio.porliobe.module.auth.dto.request.ResendVerificationTokenRequest;
import com.porlio.porliobe.module.auth.dto.request.VerificationCodeRequest;
import com.porlio.porliobe.module.auth.dto.response.AuthenticationResponse;
import com.porlio.porliobe.module.auth.dto.response.TokenPayload;
import com.porlio.porliobe.module.auth.dto.response.VerificationCodeResponse;
import com.porlio.porliobe.module.auth.entity.RedisToken;
import com.porlio.porliobe.module.auth.service.AuthenticationService;
import com.porlio.porliobe.module.auth.service.JwtService;
import com.porlio.porliobe.module.auth.service.RedisTokenService;
import com.porlio.porliobe.module.notification.service.NotificationService;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.shared.data.constant.TokenType;
import com.porlio.porliobe.module.shared.exception.AppException;
import com.porlio.porliobe.module.shared.utils.CookiesUtil;
import com.porlio.porliobe.module.user.entity.CustomUserDetail;
import com.porlio.porliobe.module.user.entity.User;
import com.porlio.porliobe.module.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "AUTHENTICATION_SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

  AuthenticationManager authManager;
  JwtService jwtService;
  RedisTokenService redisTokenService;
  UserService userService;
  RedisTemplate<String, Object> redisTemplate;
  NotificationService notificationService;

  @NonFinal
  @Value("${jwt.expiration.verification}")
  Long verificationTokenExpiration;

  String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

  @Override
  public AuthenticationResponse authenticate(AuthenticationRequest request,
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
    TokenPayload accessToken = jwtService.generateAccessToken(user.getId().toString(), authorities);
    TokenPayload refreshToken = jwtService.generateRefreshToken(user.getId().toString(),
        authorities);

    // Step 4: Store refresh token in HttpOnly cookie
    long ttl = Duration.between(new Date().toInstant(), refreshToken.expiration().toInstant())
        .getSeconds();
    int maxAge = (int) ttl;
    CookiesUtil.store(REFRESH_TOKEN_COOKIE_NAME, refreshToken.token(), maxAge, "/", response);

    // Step 5: Store refresh token in Redis for validation during refresh
    redisTokenService.save(RedisToken.builder()
        .id(refreshToken.jwtId())
        .tokenType(TokenType.REFRESH_TOKEN.name())
        .expiredTime(ttl)
        .build());

    log.info("User authenticated successfully: {}", user.getUsername());

    return AuthenticationResponse.builder()
        .token(accessToken.token())
        .tokenType(TokenType.ACCESS_TOKEN.name())
        .build();
  }

  @Override
  public AuthenticationResponse refreshToken(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse) throws ParseException, JOSEException {
    log.debug("Attempting to refresh access token");

    // Step 1: Extract refresh token from HTTP-only cookie
    String refreshToken = CookiesUtil.getValue(REFRESH_TOKEN_COOKIE_NAME,
        httpServletRequest.getCookies());

    // Step 2: Validate refresh token presence
    if (refreshToken == null || refreshToken.isEmpty()) {
      log.warn("Refresh token not found in cookies");
      throw new AppException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }

    // Step 3: Verify refresh token validity (signature, expiration, and revocation)
    if (!jwtService.verifyToken(TokenType.REFRESH_TOKEN, refreshToken)) {
      log.warn("Invalid refresh token: {}", refreshToken);
      throw new AppException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }

    // Step 4: Extract claims from refresh token to get user information and revoke old refresh token
    JWTClaimsSet claims = jwtService.getClaims(refreshToken);
    String jwtId = claims.getJWTID();
    redisTokenService.deleteById(jwtId);

    // Step 6: Generate new access token using user information from refresh token claims
    String subject = claims.getSubject();
    Object raw = claims.getClaim("authorities");
    Set<String> authorities = raw instanceof ArrayList<?> list
        ? list.stream().filter(String.class::isInstance).map(String.class::cast).collect(
        Collectors.toSet())
        : Set.of();
    TokenPayload newAccessToken = jwtService.generateAccessToken(subject, authorities);
    TokenPayload newRefreshToken = jwtService.generateRefreshToken(subject, authorities);
    log.info("Access token and refresh token refreshed successfully for user: {}", subject);

    // Step 7: Store new refresh token in HTTP-only cookie and update Redis with new token information
    long ttl = Duration.between(new Date().toInstant(), newRefreshToken.expiration().toInstant())
        .getSeconds();
    int maxAge = (int) ttl;
    CookiesUtil.store(
        REFRESH_TOKEN_COOKIE_NAME, newRefreshToken.token(),
        maxAge, "/", httpServletResponse
    );

    redisTokenService.save(RedisToken.builder()
        .id(newRefreshToken.jwtId())
        .tokenType(TokenType.REFRESH_TOKEN.name())
        .expiredTime(ttl)
        .build());

    return AuthenticationResponse.builder()
        .token(newAccessToken.token())
        .tokenType(TokenType.ACCESS_TOKEN.name())
        .build();
  }

  @Override
  public void logout(String token, HttpServletRequest request, HttpServletResponse response)
      throws ParseException {
    // Step 1: Parse the token to extract claims and check expiration
    JWTClaimsSet claims = jwtService.getClaims(token);

    // Step 2: Check if the token is already expired
    Date now = new Date();
    Date expiration = claims.getExpirationTime();
    if (expiration.before(now)) {
      log.warn("Token already expired during logout: {}", token);
      return;
    }

    // Step 3: Store the token's JWT ID in Redis with an expiration time equal to the remaining validity of the token
    redisTokenService.save(RedisToken.builder()
        .id(claims.getJWTID())
        .tokenType(TokenType.ACCESS_TOKEN.name())
        .expiredTime(Duration.between(now.toInstant(), expiration.toInstant()).getSeconds())
        .build());

    // Step 4: Clear the refresh token cookie and remove the corresponding entry from Redis
    String refreshToken = CookiesUtil.getValue(REFRESH_TOKEN_COOKIE_NAME, request.getCookies());
    if (refreshToken == null) {
      log.warn("No refresh token found in cookies during logout.");
      return;
    }
    JWTClaimsSet refreshClaims = jwtService.getClaims(refreshToken);
    String jwtId = refreshClaims.getJWTID();
    redisTokenService.deleteById(jwtId);
    CookiesUtil.clear(REFRESH_TOKEN_COOKIE_NAME, "/", response);

    SecurityContextHolder.clearContext();

    log.info("User logged out successfully.");
  }

  @Override
  public VerificationCodeResponse verifyCode(VerificationCodeRequest request) {
    String redisKey = "verify_token:" + request.email();
    Object token = redisTemplate.opsForValue().get(redisKey);
    boolean isValid = token != null && token.equals(request.token());
    if (isValid) {
      redisTemplate.delete(redisKey);
      userService.verifyEmail(request.email());
    }
    return new VerificationCodeResponse(isValid);
  }

  @Override
  public void resendVerificationCode(ResendVerificationTokenRequest request) {
    User user = userService.getUserByEmail(request.email());
    if (user.getIsVerified()) {
      log.info("User with email {} is already verified. No need to resend verification code.",
          request.email());
      return;
    }
    String username = user.getUsername();

    String redisKey = "verify_token:" + request.email();
    String verifyToken = UUID.randomUUID().toString().replace("-", "");
    redisTemplate.opsForValue()
        .set(redisKey, verifyToken, verificationTokenExpiration * 3600, TimeUnit.SECONDS);

    notificationService.sendVerificationEmail(request.email(), username, verifyToken);
  }

  private Set<String> extractAuthorities(CustomUserDetail userDetail) {
    return userDetail.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
  }
}

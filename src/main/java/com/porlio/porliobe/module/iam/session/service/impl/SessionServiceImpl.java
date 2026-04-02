package com.porlio.porliobe.module.iam.session.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.porlio.porliobe.module.iam.session.constant.TokenType;
import com.porlio.porliobe.module.iam.session.dto.response.SessionResponse;
import com.porlio.porliobe.module.iam.session.dto.response.TokenPayload;
import com.porlio.porliobe.module.iam.session.entity.RedisToken;
import com.porlio.porliobe.module.iam.session.service.JwtService;
import com.porlio.porliobe.module.iam.session.service.RedisTokenService;
import com.porlio.porliobe.module.iam.session.service.SessionService;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.shared.exception.AppException;
import com.porlio.porliobe.module.shared.utils.CookiesUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "SESSION_SERVICE")
public class SessionServiceImpl implements SessionService {

  JwtService jwtService;
  RedisTokenService redisTokenService;

  static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

  @Override
  public SessionResponse refreshToken(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse) throws ParseException, JOSEException {
    log.debug("Attempting to refresh access token");

    // Step 1: Extract refresh token from HTTP-only cookie
    String refreshToken = CookiesUtil.getValue(REFRESH_TOKEN_COOKIE_NAME,
        httpServletRequest.getCookies());

    // Step 2: Validate refresh token presence
    if (!StringUtils.hasLength(refreshToken)) {
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

    // Step 5: Generate new access token using user information from refresh token claims
    String subject = claims.getSubject();
    Object authoritiesRaw = claims.getClaim("authorities");
    Set<String> authorities = authoritiesRaw instanceof ArrayList<?> authList
        ? authList.stream().filter(String.class::isInstance).map(String.class::cast).collect(
        Collectors.toSet())
        : Set.of();

    return createSessionForUser(subject, authorities, httpServletResponse);
  }

  @Override
  public SessionResponse createSessionForUser(
      String userId,
      Set<String> authorities,
      HttpServletResponse response) {
    TokenPayload newAccessToken = jwtService.generateToken(userId, authorities,
        TokenType.ACCESS_TOKEN);
    TokenPayload newRefreshToken = jwtService.generateToken(userId, authorities,
        TokenType.REFRESH_TOKEN);
    log.info("Access token and refresh token refreshed successfully for user: {}", userId);

    long ttl = Duration.between(new Date().toInstant(), newRefreshToken.expiration().toInstant())
        .getSeconds();
    int maxAge = (int) ttl;
    CookiesUtil.store(
        REFRESH_TOKEN_COOKIE_NAME, newRefreshToken.token(),
        maxAge, "/", response
    );

    redisTokenService.save(RedisToken.builder()
        .id(newRefreshToken.jwtId())
        .tokenType(TokenType.REFRESH_TOKEN.name())
        .expiredTime(ttl)
        .build());

    return SessionResponse.builder()
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
}

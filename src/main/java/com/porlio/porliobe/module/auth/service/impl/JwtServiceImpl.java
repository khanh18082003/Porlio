package com.porlio.porliobe.module.auth.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.porlio.porliobe.module.auth.dto.response.TokenPayload;
import com.porlio.porliobe.module.auth.repository.RedisTokenRepository;
import com.porlio.porliobe.module.auth.service.JwtService;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.shared.data.constant.TokenType;
import com.porlio.porliobe.module.shared.exception.AppException;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "JWT_SERVICE")
public class JwtServiceImpl implements JwtService {

  @NonFinal
  @Value("${jwt.access-key}")
  String accessKey;

  @NonFinal
  @Value("${jwt.refresh-key}")
  String refreshKey;

  @NonFinal
  @Value("${jwt.expiration.access}")
  Long expirationAccess;

  @NonFinal
  @Value("${jwt.expiration.refresh}")
  Long expirationRefresh;

  RedisTokenRepository redisTokenRepository;

  @Override
  public TokenPayload generateAccessToken(String userId, Set<String> authorities) {
    JWSAlgorithm algorithm = JWSAlgorithm.HS512;
    JWSHeader header = new JWSHeader(algorithm);

    Date issueTime = new Date();
    Date expiredTime = new Date(
        Instant.now().plus(expirationAccess, ChronoUnit.HOURS).toEpochMilli());

    String jwtId = UUID.randomUUID().toString();

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(userId)
        .issueTime(issueTime)
        .expirationTime(expiredTime)
        .jwtID(jwtId)
        .claim("authorities", authorities)
        .build();

    Payload payload = new Payload(claimsSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(header, payload);
    try {
      jwsObject.sign(new MACSigner(accessKey));
    } catch (JOSEException e) {
      log.error("Generate accessToken error: {}", e.getMessage());
      throw new AppException(ErrorCode.MESSAGE_TOKEN_GENERATION_FAILED);
    }

    return TokenPayload.builder()
        .jwtId(jwtId)
        .token(jwsObject.serialize())
        .issueTime(issueTime)
        .expiration(expiredTime)
        .build();
  }

  @Override
  public TokenPayload generateRefreshToken(String userId, Set<String> authorities) {
    JWSAlgorithm algorithm = JWSAlgorithm.HS512;
    JWSHeader header = new JWSHeader(algorithm);

    Date issueTime = new Date();
    Date expiredTime = new Date(
        Instant.now().plus(expirationRefresh, ChronoUnit.DAYS).toEpochMilli());

    String jwtId = UUID.randomUUID().toString();

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(userId)
        .issueTime(issueTime)
        .expirationTime(expiredTime)
        .jwtID(jwtId)
        .claim("authorities", authorities)
        .build();

    Payload payload = new Payload(claimsSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(header, payload);
    try {
      jwsObject.sign(new MACSigner(refreshKey));
    } catch (JOSEException e) {
      log.error("Generate refresh token error: {}", e.getMessage());
      throw new AppException(ErrorCode.MESSAGE_TOKEN_GENERATION_FAILED);
    }

    return TokenPayload.builder()
        .jwtId(jwtId)
        .token(jwsObject.serialize())
        .issueTime(issueTime)
        .expiration(expiredTime)
        .build();
  }

  @Override
  public boolean verifyToken(TokenType type, String token) throws ParseException, JOSEException {
    // Step 1: Parse the token to extract claims
    SignedJWT signedJWT = SignedJWT.parse(token);

    // Step 2: Check token expiration
    Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
    if (expirationTime.before(new Date())) {
      log.warn("Token expired: {}", token);
      return false;
    }

    // Step 3: Check if the token has been revoked (exists in Redis)
    String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
    if (checkRevoked(jwtId, type)) {
      log.warn("Token revoked (found in Redis): {}", jwtId);
      return false;
    }

    // Step 5: Verify the token signature using the appropriate key based on token type
    return signedJWT.verify(getVerifier(type));
  }

  @Override
  public JWTClaimsSet getClaims(String token) throws ParseException {
    return SignedJWT.parse(token).getJWTClaimsSet();
  }

  private boolean checkRevoked(String jwtId, TokenType type) {
    return switch (type) {
      case TokenType.ACCESS_TOKEN -> redisTokenRepository.existsById(jwtId);
      case TokenType.REFRESH_TOKEN -> !redisTokenRepository.existsById(jwtId);
    };
  }

  private JWSVerifier getVerifier(TokenType type) throws JOSEException {
    return switch (type) {
      case TokenType.ACCESS_TOKEN -> new MACVerifier(accessKey.getBytes());
      case TokenType.REFRESH_TOKEN -> new MACVerifier(refreshKey.getBytes());
    };
  }
}

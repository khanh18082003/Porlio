package com.porlio.porliobe.module.iam.session.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.porlio.porliobe.module.iam.session.component.JwtTokenStrategy;
import com.porlio.porliobe.module.iam.session.constant.TokenType;
import com.porlio.porliobe.module.iam.session.dto.response.TokenPayload;
import com.porlio.porliobe.module.iam.session.repository.RedisTokenRepository;
import com.porlio.porliobe.module.iam.session.service.JwtService;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.shared.exception.AppException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "JWT_SERVICE")
public class JwtServiceImpl implements JwtService {

  RedisTokenRepository redisTokenRepository;
  Map<TokenType, JwtTokenStrategy> jwtTokenStrategyMap;

  public JwtServiceImpl(List<JwtTokenStrategy> strategies,
      RedisTokenRepository redisTokenRepository) {
    this.redisTokenRepository = redisTokenRepository;
    this.jwtTokenStrategyMap = strategies.stream()
        .collect(Collectors.toMap(JwtTokenStrategy::getTokenType, Function.identity()));
  }

  @Override
  public TokenPayload generateToken(String userId, Set<String> authorities, TokenType type) {
    JwtTokenStrategy jwtTokenStrategy = getJwtTokenStrategy(type);

    // Step 1: Define JWT header with HS512 algorithm
    JWSAlgorithm algorithm = JWSAlgorithm.HS512;
    JWSHeader header = new JWSHeader(algorithm);

    // Step 2: Create JWT claims with user information, authorities, issue time, and expiration time
    String jwtId = UUID.randomUUID().toString();
    Date issueTime = new Date();
    Date expiredTime = jwtTokenStrategy.getExpirationTime();

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
      jwsObject.sign(jwtTokenStrategy.getSigner());
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
    JwtTokenStrategy jwtTokenStrategy = getJwtTokenStrategy(type);
    boolean revoked = redisTokenRepository.existsById(jwtId);

    return revoked == jwtTokenStrategy.isWhiteList();
  }

  private JWSVerifier getVerifier(TokenType type) throws JOSEException {
    JwtTokenStrategy jwtTokenStrategy = getJwtTokenStrategy(type);
    return jwtTokenStrategy.getVerifier();
  }

  private JwtTokenStrategy getJwtTokenStrategy(TokenType type) {
    JwtTokenStrategy jwtTokenStrategy = jwtTokenStrategyMap.get(type);
    if (jwtTokenStrategy == null) {
      log.error("Unsupported token type: {}", type);
      throw new IllegalArgumentException("Unsupported token type: " + type);
    }
    return jwtTokenStrategy;
  }
}

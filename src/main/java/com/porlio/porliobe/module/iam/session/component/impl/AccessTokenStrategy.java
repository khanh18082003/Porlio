package com.porlio.porliobe.module.iam.session.component.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.porlio.porliobe.module.iam.session.component.JwtTokenStrategy;
import com.porlio.porliobe.module.iam.session.constant.TokenType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenStrategy implements JwtTokenStrategy {

  @Value("${jwt.access-key}")
  private String accessKey;

  @NonFinal
  @Value("${jwt.expiration.access}")
  private Long expirationAccess;

  @Override
  public boolean isWhiteList() {
    return true;
  }

  @Override
  public JWSVerifier getVerifier() throws JOSEException {
    return new MACVerifier(accessKey.getBytes());
  }

  @Override
  public JWSSigner getSigner() throws KeyLengthException {
    return new MACSigner(accessKey);
  }

  @Override
  public TokenType getTokenType() {
    return TokenType.ACCESS_TOKEN;
  }

  @Override
  public Date getExpirationTime() {
    return Date.from(Instant.now().plus(expirationAccess, ChronoUnit.HOURS));
  }
}

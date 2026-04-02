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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenStrategy implements JwtTokenStrategy {

  @Value("${jwt.refresh-key}")
  String refreshKey;

  @Value("${jwt.expiration.refresh}")
  Long expirationRefresh;

  @Override
  public JWSSigner getSigner() throws KeyLengthException {
    return new MACSigner(refreshKey);
  }

  @Override
  public TokenType getTokenType() {
    return TokenType.REFRESH_TOKEN;
  }

  @Override
  public boolean isWhiteList() {
    return false;
  }

  @Override
  public JWSVerifier getVerifier() throws JOSEException {
    return new MACVerifier(refreshKey.getBytes());
  }

  @Override
  public Date getExpirationTime() {
    return Date.from(Instant.now().plus(expirationRefresh, ChronoUnit.DAYS));
  }
}

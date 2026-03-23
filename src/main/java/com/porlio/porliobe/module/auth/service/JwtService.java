package com.porlio.porliobe.module.auth.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.porlio.porliobe.module.auth.dto.response.TokenPayload;
import com.porlio.porliobe.module.shared.data.constant.TokenType;
import java.text.ParseException;
import java.util.Set;

public interface JwtService {

  TokenPayload generateAccessToken(String userId, Set<String> authorities);

  TokenPayload generateRefreshToken(String userId, Set<String> authorities);

  JWTClaimsSet getClaims(String token) throws ParseException;

  boolean verifyToken(TokenType type, String token) throws ParseException, JOSEException;
}

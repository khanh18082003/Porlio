package com.porlio.porliobe.module.iam.session.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.porlio.porliobe.module.iam.session.dto.response.TokenPayload;
import com.porlio.porliobe.module.iam.session.constant.TokenType;
import java.text.ParseException;
import java.util.Set;

public interface JwtService {

  TokenPayload generateToken(String userId, Set<String> authorities, TokenType type);

  JWTClaimsSet getClaims(String token) throws ParseException;

  boolean verifyToken(TokenType type, String token) throws ParseException, JOSEException;
}

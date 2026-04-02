package com.porlio.porliobe.module.iam.session.component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;

public interface JwtTokenStrategy extends TokenStrategy {

  JWSSigner getSigner() throws KeyLengthException;

  JWSVerifier getVerifier() throws JOSEException;

  boolean isWhiteList();
}

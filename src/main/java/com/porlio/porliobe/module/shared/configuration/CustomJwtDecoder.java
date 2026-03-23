package com.porlio.porliobe.module.shared.configuration;

import com.nimbusds.jose.JOSEException;
import com.porlio.porliobe.module.auth.service.AuthenticationService;
import com.porlio.porliobe.module.auth.service.JwtService;
import com.porlio.porliobe.module.shared.data.constant.TokenType;
import java.text.ParseException;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "JWT_DECODER_CONFIGURATION")
public class CustomJwtDecoder implements JwtDecoder {

  private final JwtService jwtService;

  private NimbusJwtDecoder nimbusJwtDecoder = null;

  @Value("${jwt.access-key}")
  private String signerKey;

  @Override
  public Jwt decode(String token) throws JwtException {
    try {
      var isAuthenticated = jwtService.verifyToken(TokenType.ACCESS_TOKEN, token);

      if (!isAuthenticated) throw new JwtException("Token invalid");
    } catch (JOSEException | ParseException e) {
      throw new JwtException(e.getMessage());
    }

    if (Objects.isNull(nimbusJwtDecoder)) {
      SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
      nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
          .macAlgorithm(MacAlgorithm.HS512)
          .build();
    }

    return nimbusJwtDecoder.decode(token);
  }
}

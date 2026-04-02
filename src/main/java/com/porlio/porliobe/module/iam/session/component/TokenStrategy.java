package com.porlio.porliobe.module.iam.session.component;

import com.porlio.porliobe.module.iam.session.constant.TokenType;
import java.util.Date;

public interface TokenStrategy {

  TokenType getTokenType();

  Date getExpirationTime();
}

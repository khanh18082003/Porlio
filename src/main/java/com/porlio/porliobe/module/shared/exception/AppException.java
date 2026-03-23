package com.porlio.porliobe.module.shared.exception;

import com.porlio.porliobe.module.shared.configuration.Translator;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

  private final ErrorCode errorCode;

  public AppException(ErrorCode errorCode) {
    super(Translator.toLocale(errorCode.getCode()));
    this.errorCode = errorCode;
  }
}

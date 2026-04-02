package com.porlio.porliobe.module.shared.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.porlio.porliobe.module.shared.configuration.Translator;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import java.io.Serializable;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

  boolean success;
  String code;
  int status;
  String message;
  transient T data;
  Instant timestamp;

  // ── Factory methods ──────────────────────────

  public static <T> ApiResponse<T> ok(T data) {
    ErrorCode successCode = ErrorCode.MESSAGE_SUCCESS;
    return ApiResponse.<T>builder()
        .success(true)
        .code(successCode.getCode())
        .status(successCode.getHttpStatus().value())
        .message(Translator.toLocale(successCode.getCode()))
        .data(data)
        .timestamp(Instant.now())
        .build();
  }

  public static <T> ApiResponse<T> ok() {
    ErrorCode successCode = ErrorCode.MESSAGE_SUCCESS;
    return ApiResponse.<T>builder()
        .success(true)
        .code(successCode.getCode())
        .status(successCode.getHttpStatus().value())
        .message(Translator.toLocale(successCode.getCode()))
        .timestamp(Instant.now())
        .build();
  }

  public static <T> ApiResponse<T> created(T data) {
    ErrorCode createdCode = ErrorCode.MESSAGE_CREATED;
    return ApiResponse.<T>builder()
        .success(true)
        .code(createdCode.getCode())
        .status(createdCode.getHttpStatus().value())
        .message(Translator.toLocale(createdCode.getCode()))
        .data(data)
        .timestamp(Instant.now())
        .build();
  }

  public static ApiResponse<Void> noContent() {
    ErrorCode noContentCode = ErrorCode.MESSAGE_NO_CONTENT;
    return ApiResponse.<Void>builder()
        .success(true)
        .code(noContentCode.getCode())
        .status(noContentCode.getHttpStatus().value())
        .message(Translator.toLocale(noContentCode.getCode()))
        .timestamp(Instant.now())
        .build();
  }
}

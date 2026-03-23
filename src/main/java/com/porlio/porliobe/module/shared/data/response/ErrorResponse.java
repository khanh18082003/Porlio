package com.porlio.porliobe.module.shared.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse extends ApiResponse<Void> implements Serializable {

  String path;
  String error;
  List<FieldError> fieldErrors;

  @Builder(builderMethodName = "errorBuilder")
  public ErrorResponse(String code,
      int status,
      String message,
      Instant timestamp,
      String path,
      String error,
      List<FieldError> fieldErrors) {
    super(false, code, status, message, null, timestamp);
    this.path = path;
    this.error = error;
    this.fieldErrors = fieldErrors;
  }

  public record FieldError(String field, String message) {
  }

  // ── Factory methods ──────────────────────────

  public static ErrorResponse of(String code, int status, String message,
      String path, String error) {
    return ErrorResponse.errorBuilder()
        .code(code)
        .status(status)
        .message(message)
        .timestamp(Instant.now())
        .error(error)
        .path(path)
        .build();
  }

  public static ErrorResponse validation(
      String code,
      int status,
      String path,
      String error,
      List<FieldError> fieldErrors) {
    return ErrorResponse.errorBuilder()
        .code(code)
        .status(status)
        .timestamp(Instant.now())
        .error(error)
        .path(path)
        .fieldErrors(fieldErrors)
        .build();
  }
}

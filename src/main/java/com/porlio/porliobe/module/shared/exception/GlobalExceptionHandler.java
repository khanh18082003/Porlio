package com.porlio.porliobe.module.shared.exception;

import com.porlio.porliobe.module.shared.configuration.Translator;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.shared.data.response.ErrorResponse;
import com.porlio.porliobe.module.shared.data.response.ErrorResponse.FieldError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
public class GlobalExceptionHandler {

  private static final Pattern DUPLICATE_VALUE_PATTERN =
      Pattern.compile("Key \\((.+?)\\)=\\((.+?)\\) already exists");

  private final static String MIN_ATTRIBUTE = "min";
  private final static String MAX_ATTRIBUTE = "max";

  // 500 — Fallback
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(
      Exception ex, HttpServletRequest request) {
    log.error("Unhandled exception at {}", request.getRequestURI(), ex);
    ErrorCode errorCode = ErrorCode.MESSAGE_INTERNAL_SERVER_ERROR;
    return ResponseEntity.internalServerError().body(
        ErrorResponse.of(
            errorCode.getCode(),
            errorCode.getHttpStatus().value(),
            Translator.toLocale(errorCode.getCode()),
            request.getRequestURI(),
            errorCode.getHttpStatus().getReasonPhrase()
        )
    );
  }

  // 400 — Bean Validation (@Valid)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {

    ErrorCode errorCode = ErrorCode.MESSAGE_VALIDATION_ERROR; // Invalid error key
    List<ErrorResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> {
          try {
            // Check ErrorCode enum for the field error code
            ErrorCode fieldErrorCode = ErrorCode.valueOf(e.getDefaultMessage());

            // Unwrap the ConstraintViolation to get attributes for message formatting
            ConstraintViolation<?> violation = e.unwrap(ConstraintViolation.class);
            Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();

            // Map the message with attributes if available, otherwise use the default message
            String message =
                Objects.nonNull(attributes)
                    ? mapAttribute(Translator.toLocale(fieldErrorCode.getCode()), attributes)
                    : Translator.toLocale(fieldErrorCode.getCode());

            return new FieldError(e.getField(), message);
          } catch (IllegalArgumentException exception) {
            log.warn("Unknown validation error code: {}", e.getDefaultMessage());
            return new FieldError(e.getField(),
                Translator.toLocale(ErrorCode.MESSAGE_INVALID_KEY.getCode()));
          }
        })
        .toList();

    return ResponseEntity.badRequest().body(
        ErrorResponse.validation(
            errorCode.getCode(),
            errorCode.getHttpStatus().value(),
            request.getRequestURI(),
            errorCode.getHttpStatus().getReasonPhrase(),
            errors
        )
    );
  }

  // 404 / 422 — Business exception
  @ExceptionHandler(AppException.class)
  public ResponseEntity<ErrorResponse> handleApp(
      AppException ex, HttpServletRequest request
  ) {
    ErrorCode errorCode = ex.getErrorCode();
    return ResponseEntity.status(errorCode.getHttpStatus()).body(
        ErrorResponse.of(
            errorCode.getCode(),
            errorCode.getHttpStatus().value(),
            Translator.toLocale(errorCode.getCode()),
            request.getRequestURI(),
            errorCode.getHttpStatus().getReasonPhrase()
        )
    );
  }

  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<ErrorResponse> handleAccessDeniedException(
      AccessDeniedException ex, HttpServletRequest request) {
    log.warn("Access denied at {}", request.getRequestURI(), ex);
    ErrorCode errorCode = ErrorCode.MESSAGE_UNAUTHORIZED;
    return ResponseEntity.status(errorCode.getHttpStatus()).body(
        ErrorResponse.of(
            errorCode.getCode(),
            errorCode.getHttpStatus().value(),
            Translator.toLocale(errorCode.getCode()),
            request.getRequestURI(),
            errorCode.getHttpStatus().getReasonPhrase()
        )
    );
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
      DataIntegrityViolationException e, HttpServletRequest request) {
    ErrorCode errorCode = ErrorCode.MESSAGE_DUPLICATE_ENTRY;
    String rootMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
    log.warn("Root cause message for DataIntegrityViolationException: {}", rootMessage);
    String message = extractDuplicateValue(rootMessage, errorCode);

    return ResponseEntity.status(errorCode.getHttpStatus()).body(
        ErrorResponse.of(
            errorCode.getCode(),
            errorCode.getHttpStatus().value(),
            message,
            request.getRequestURI(),
            errorCode.getHttpStatus().getReasonPhrase()
        )
    );
  }

  private String mapAttribute(String message, Map<String, Object> attributes) {
    String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
    String maxValue = String.valueOf(attributes.get(MAX_ATTRIBUTE));

    return String.format(message, minValue, maxValue);
  }

  private String extractDuplicateValue(String rootMessage, ErrorCode errorCode) {
    if (rootMessage == null) {
      return Translator.toLocale(errorCode.getCode());
    }

    Matcher matcher = DUPLICATE_VALUE_PATTERN.matcher(rootMessage);
    if (matcher.find()) {
      String field = matcher.group(1);
      String value = matcher.group(2);
      return String.format(Translator.toLocale(errorCode.getCode()), field.toUpperCase(), value);
    } else {
      log.warn("Could not extract duplicate value from message: {}", rootMessage);
      return Translator.toLocale(errorCode.getCode());
    }
  }
}

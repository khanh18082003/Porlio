package com.porlio.porliobe.module.iam.user.dto.response;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record RegistrationResponse(
    UUID id,
    String email,
    String username,
    boolean isVerified,
    boolean requiresEmailVerification,
    Instant createdAt
) implements Serializable {

  public RegistrationResponse {
    requiresEmailVerification = true; // Always require email verification for new registrations
  }
}

package com.porlio.porliobe.module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public record ResendVerificationTokenRequest(
    @NotBlank(message = "MESSAGE_NOT_BLANK") String email
) implements Serializable {

}

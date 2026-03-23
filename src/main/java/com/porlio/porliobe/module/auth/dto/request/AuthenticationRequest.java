package com.porlio.porliobe.module.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public record AuthenticationRequest(
    @NotBlank(message = "MESSAGE_NOT_BLANK") @Email(message = "MESSAGE_INVALID_EMAIL") String email,
    @NotBlank(message = "MESSAGE_NOT_BLANK")
    String password
) implements Serializable {

}

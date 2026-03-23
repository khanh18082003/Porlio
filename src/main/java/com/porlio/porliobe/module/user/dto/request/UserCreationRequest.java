package com.porlio.porliobe.module.user.dto.request;

import com.porlio.porliobe.module.user.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public record UserCreationRequest(
    @NotBlank(message = "MESSAGE_NOT_BLANK")
    @Email(message = "MESSAGE_INVALID_EMAIL")
    String email,
    @NotBlank(message = "MESSAGE_NOT_BLANK")
    @ValidPassword
    @Size(min = 8, max = 25, message = "MESSAGE_INVALID_SIZE")
    String password,
    @NotBlank(message = "MESSAGE_NOT_BLANK")
    @Pattern(
        regexp = "^[a-z0-9_-]{3,50}$",
        message = "MESSAGE_INVALID_USERNAME"
    )
    String username
) implements Serializable {
}

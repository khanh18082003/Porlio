package com.porlio.porliobe.module.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public record PermissionCreationRequest(
    @NotBlank(message = "MESSAGE_NOT_BLANK") String key,
    @NotBlank(message = "MESSAGE_NOT_BLANK") String name,
    @NotBlank(message = "MESSAGE_NOT_BLANK") String method,
    @NotBlank(message = "MESSAGE_NOT_BLANK") String urlPattern,
    @NotBlank(message = "MESSAGE_NOT_BLANK") String description,
    @NotBlank(message = "MESSAGE_NOT_BLANK") String module
) implements Serializable {

}

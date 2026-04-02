package com.porlio.porliobe.module.iam.access.role.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

public record RoleCreationRequest(
    @NotBlank(message = "MESSAGE_NOT_BLANK") String key,
    @NotBlank(message = "MESSAGE_NOT_BLANK") String name,
    String description,
    List<String> permissionKeys
) implements Serializable {

}

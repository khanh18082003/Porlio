package com.porlio.porliobe.module.admin.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PermissionResponse(
    UUID id,
    String permissionKey,
    String name,
    String method,
    String urlPattern,
    String description,
    String module,
    Instant createdAt
) {

}

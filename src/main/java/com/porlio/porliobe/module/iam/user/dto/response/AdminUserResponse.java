package com.porlio.porliobe.module.iam.user.dto.response;

import com.porlio.porliobe.module.iam.access.role.dto.response.RoleResponse;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AdminUserResponse(
    UUID id,
    String email,
    String username,
    String fullName,
    String headline,
    String bio,
    String professionType,
    String location,
    String websiteUrl,
    String avatarUrl,
    Long githubId,
    Boolean isVerified,
    LocalDateTime verifiedAt,
    Boolean isActive,
    Boolean isDeleted,
    LocalDateTime deletedAt,
    Instant createdAt,
    Instant updatedAt,
    Set<RoleResponse>roles
) implements Serializable {

}

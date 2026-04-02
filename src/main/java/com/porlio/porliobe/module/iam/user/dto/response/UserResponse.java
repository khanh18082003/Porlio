package com.porlio.porliobe.module.iam.user.dto.response;

import com.porlio.porliobe.module.iam.access.role.dto.response.RoleResponse;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {

  UUID id;
  String email;
  String username;
  String fullName;
  String headline;
  String bio;
  String professionType;
  String location;
  String websiteUrl;
  String avatarUrl;
  Long githubId;
  Boolean isVerified;
  LocalDateTime verifiedAt;
  Boolean isActive;
  Boolean isDeleted;
  Instant createdAt;
  Instant updatedAt;
  Set<RoleResponse> roles;
}

package com.porlio.porliobe.module.user.dto.response;

import com.porlio.porliobe.module.admin.dto.response.RoleResponse;
import java.io.Serializable;
import java.time.Instant;
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
  String avatarUrl;
  Boolean isVerified;
  Boolean isActive;
  Boolean isDeleted;
  Instant createdAt;
  Set<RoleResponse> roles;
}

package com.porlio.porliobe.module.iam.access.role.dto.response;

import com.porlio.porliobe.module.iam.access.permission.dto.response.PermissionResponse;
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
public class RoleResponse implements Serializable {

  UUID id;
  String roleKey;
  String name;
  String description;
  Instant createdAt;
  Set<PermissionResponse> permissions;
}

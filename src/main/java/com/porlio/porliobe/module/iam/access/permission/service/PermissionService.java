package com.porlio.porliobe.module.iam.access.permission.service;

import com.porlio.porliobe.module.iam.access.permission.dto.request.PermissionCreationRequest;
import com.porlio.porliobe.module.iam.access.permission.dto.response.PermissionResponse;
import com.porlio.porliobe.module.iam.access.permission.entity.Permission;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionService {

  PermissionResponse createPermission(PermissionCreationRequest request);

  PermissionResponse getPermissionById(UUID id);

  Set<Permission> getPermissionsByKeyIn(List<String> keys);

  Set<PermissionResponse> getAllPermissions();
}

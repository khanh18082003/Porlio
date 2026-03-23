package com.porlio.porliobe.module.admin.service;

import com.porlio.porliobe.module.admin.dto.request.PermissionCreationRequest;
import com.porlio.porliobe.module.admin.dto.response.PermissionResponse;
import com.porlio.porliobe.module.admin.entity.Permission;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionService {

  PermissionResponse createPermission(PermissionCreationRequest request);

  PermissionResponse getPermissionById(UUID id);

  Set<Permission> getPermissionsByKeyIn(List<String> keys);

  Set<PermissionResponse> getAllPermissions();
}

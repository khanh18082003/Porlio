package com.porlio.porliobe.module.iam.access.role.service;

import com.porlio.porliobe.module.iam.access.role.dto.request.RoleCreationRequest;
import com.porlio.porliobe.module.iam.access.role.dto.response.RoleResponse;
import com.porlio.porliobe.module.iam.access.role.entity.Role;
import com.porlio.porliobe.module.iam.access.role.constant.RoleName;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface RoleService {

  RoleResponse createRole(RoleCreationRequest request);

  RoleResponse getRoleById(UUID id);

  Set<RoleResponse> getAllRoles();

  Set<Role> getAllRolesByKeyIn(Collection<RoleName> keys);

}

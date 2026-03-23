package com.porlio.porliobe.module.admin.service;

import com.porlio.porliobe.module.admin.dto.request.RoleCreationRequest;
import com.porlio.porliobe.module.admin.dto.response.RoleResponse;
import com.porlio.porliobe.module.admin.entity.Role;
import com.porlio.porliobe.module.shared.data.constant.RoleName;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface RoleService {

  RoleResponse createRole(RoleCreationRequest request);

  RoleResponse getRoleById(UUID id);

  Set<RoleResponse> getAllRoles();

  Set<Role> getAllRolesByKeyIn(Collection<RoleName> keys);

}

package com.porlio.porliobe.module.iam.access.role.mapper;

import com.porlio.porliobe.module.iam.access.role.dto.request.RoleCreationRequest;
import com.porlio.porliobe.module.iam.access.role.dto.response.RoleResponse;
import com.porlio.porliobe.module.iam.access.role.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

  Role toRole(RoleCreationRequest request);

  RoleResponse toRoleResponse(Role role);

  Role toRole(RoleResponse response);
}

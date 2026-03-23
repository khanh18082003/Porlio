package com.porlio.porliobe.module.admin.mapper;

import com.porlio.porliobe.module.admin.dto.request.RoleCreationRequest;
import com.porlio.porliobe.module.admin.dto.response.RoleResponse;
import com.porlio.porliobe.module.admin.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

  Role toRole(RoleCreationRequest request);

  RoleResponse toRoleResponse(Role role);

  Role toRole(RoleResponse response);
}

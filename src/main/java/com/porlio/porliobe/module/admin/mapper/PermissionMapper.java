package com.porlio.porliobe.module.admin.mapper;

import com.porlio.porliobe.module.admin.dto.request.PermissionCreationRequest;
import com.porlio.porliobe.module.admin.dto.response.PermissionResponse;
import com.porlio.porliobe.module.admin.entity.Permission;
import java.util.Collection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

  Permission toPermission(PermissionCreationRequest request);

  Permission toPermission(PermissionResponse response);

  Collection<Permission> toPermissions(Collection<PermissionResponse> responses);

  PermissionResponse toPermissionResponse(Permission permission);
}

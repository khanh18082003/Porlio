package com.porlio.porliobe.module.admin.service.impl;

import com.porlio.porliobe.module.admin.dto.request.RoleCreationRequest;
import com.porlio.porliobe.module.admin.dto.response.RoleResponse;
import com.porlio.porliobe.module.admin.entity.Role;
import com.porlio.porliobe.module.admin.mapper.PermissionMapper;
import com.porlio.porliobe.module.admin.mapper.RoleMapper;
import com.porlio.porliobe.module.admin.repository.RoleRepository;
import com.porlio.porliobe.module.admin.service.PermissionService;
import com.porlio.porliobe.module.admin.service.RoleService;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.shared.data.constant.RoleName;
import com.porlio.porliobe.module.shared.exception.AppException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "ROLE_SERVICE")
public class RoleServiceImpl implements RoleService {

  RoleRepository roleRepository;
  RoleMapper roleMapper;
  PermissionService permissionService;
  PermissionMapper permissionMapper;

  @Override
  public RoleResponse createRole(RoleCreationRequest request) {
    Role role = roleMapper.toRole(request);
    if (request.permissionKeys() != null) {
      var permissionResponses = permissionService.getPermissionsByKeyIn(request.permissionKeys());
      role.setPermissions(new HashSet<>(permissionResponses));
    }
    return roleMapper.toRoleResponse(roleRepository.save(role));
  }

  @Override
  public RoleResponse getRoleById(UUID id) {
    Role role = roleRepository.findById(id).orElseThrow(
        () -> new AppException(ErrorCode.MESSAGE_RESOURCE_NOT_FOUND)
    );
    return roleMapper.toRoleResponse(role);
  }

  @Override
  public Set<RoleResponse> getAllRoles() {
    var roles = roleRepository.findAll();
    return roles.stream().map(roleMapper::toRoleResponse).collect(Collectors.toSet());
  }

  @Override
  public Set<Role> getAllRolesByKeyIn(Collection<RoleName> keys) {
    return roleRepository.findByRoleKeyIn(keys);
  }
}

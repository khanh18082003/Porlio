package com.porlio.porliobe.module.iam.access.permission.service.impl;

import com.porlio.porliobe.module.iam.access.permission.dto.request.PermissionCreationRequest;
import com.porlio.porliobe.module.iam.access.permission.dto.response.PermissionResponse;
import com.porlio.porliobe.module.iam.access.permission.entity.Permission;
import com.porlio.porliobe.module.iam.access.permission.mapper.PermissionMapper;
import com.porlio.porliobe.module.iam.access.permission.repository.PermissionRepository;
import com.porlio.porliobe.module.iam.access.permission.service.PermissionService;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.shared.exception.AppException;
import java.util.List;
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
@Slf4j(topic = "PERMISSION_SERVICE")
public class PermissionServiceImpl implements PermissionService {

  PermissionRepository permissionRepository;
  PermissionMapper permissionMapper;

  @Override
  public PermissionResponse createPermission(PermissionCreationRequest request) {
    Permission permission = permissionMapper.toPermission(request);
    return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
  }

  @Override
  public PermissionResponse getPermissionById(UUID id) {
    Permission permission = permissionRepository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_RESOURCE_NOT_FOUND));
    return permissionMapper.toPermissionResponse(permission);
  }

  @Override
  public Set<PermissionResponse> getAllPermissions() {
    List<Permission> permissions = permissionRepository.findAll();
    return permissions.stream().map(permissionMapper::toPermissionResponse)
        .collect(Collectors.toSet());
  }

  @Override
  public Set<Permission> getPermissionsByKeyIn(List<String> keys) {
    return permissionRepository.findByPermissionKeyIn(keys);
  }
}

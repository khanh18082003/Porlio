package com.porlio.porliobe.module.admin.controller;

import com.porlio.porliobe.module.admin.dto.request.PermissionCreationRequest;
import com.porlio.porliobe.module.admin.dto.response.PermissionResponse;
import com.porlio.porliobe.module.admin.service.PermissionService;
import com.porlio.porliobe.module.shared.data.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.prefix}/permissions")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "PERMISSION_CONTROLLER")
public class PermissionController {

  PermissionService permissionService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  ApiResponse<PermissionResponse> createPermission(
      @RequestBody @Valid PermissionCreationRequest request) {
    log.info("Creating permission with name: {}", request.name());
    return ApiResponse.created(permissionService.createPermission(request));
  }

  @GetMapping("/{id}")
  ApiResponse<PermissionResponse> getPermissionById(@PathVariable UUID id) {
    log.info("Getting permission with id: {}", id);
    return ApiResponse.ok(permissionService.getPermissionById(id));
  }

  @GetMapping
  ApiResponse<Collection<PermissionResponse>> getAllPermissions() {
    log.info("Getting all permissions");
    return ApiResponse.ok(permissionService.getAllPermissions());
  }
}

package com.porlio.porliobe.module.admin.controller;

import com.porlio.porliobe.module.admin.dto.request.RoleCreationRequest;
import com.porlio.porliobe.module.admin.dto.response.RoleResponse;
import com.porlio.porliobe.module.admin.service.RoleService;
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
@RequestMapping("${app.prefix}/roles")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "ROLE_CONTROLLER")
public class RoleController {

  RoleService roleService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  ApiResponse<RoleResponse> createRole(@RequestBody @Valid RoleCreationRequest request) {
    log.info("Received request to create role with key: {}", request.key());
    return ApiResponse.created(roleService.createRole(request));
  }

  @GetMapping("/{id}")
  ApiResponse<RoleResponse> getRoleById(@PathVariable UUID id) {
    log.info("Received request to get role with id: {}", id);
    return ApiResponse.ok(roleService.getRoleById(id));
  }

  @GetMapping
  ApiResponse<Collection<RoleResponse>> getAllRoles() {
    log.info("Received request to get all roles");
    return ApiResponse.ok(roleService.getAllRoles());
  }
}

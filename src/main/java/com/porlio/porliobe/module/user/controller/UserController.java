package com.porlio.porliobe.module.user.controller;

import com.porlio.porliobe.module.shared.data.response.ApiResponse;
import com.porlio.porliobe.module.user.dto.request.UserCreationRequest;
import com.porlio.porliobe.module.user.dto.request.UserUpdateRequest;
import com.porlio.porliobe.module.user.dto.response.UserResponse;
import com.porlio.porliobe.module.user.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.prefix}/users")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "USER_CONTROLLER")
public class UserController {

  UserService userService;

  @PostMapping("/registration")
  ApiResponse<UserResponse> register(@RequestBody @Valid UserCreationRequest request) {
    log.info("Received registration request for email: {}", request.email());
    return ApiResponse.ok(userService.register(request));
  }

  @PutMapping("/me")
  @PostAuthorize("returnObject.data.id == authentication.name")
  ApiResponse<UserResponse> updateCurrentUser(@RequestBody @Valid UserUpdateRequest request) {
    log.info("Update user");
    return ApiResponse.ok(userService.updateProfile(request));
  }


  @GetMapping("/me")
  @PostAuthorize("returnObject.data.id == authentication.name")
  ApiResponse<UserResponse> getCurrentUser() {
    log.info("Received request to get current user");
    return ApiResponse.ok(userService.getCurrentUser());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<UserResponse> getUserById(@PathVariable UUID id) {
    log.info("Received request to get user by id: {}", id);
    return ApiResponse.ok(userService.getUserById(id));
  }
}

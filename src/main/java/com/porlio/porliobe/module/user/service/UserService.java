package com.porlio.porliobe.module.user.service;

import com.porlio.porliobe.module.user.dto.request.UserCreationRequest;
import com.porlio.porliobe.module.user.dto.request.UserUpdateRequest;
import com.porlio.porliobe.module.user.dto.response.UserResponse;
import com.porlio.porliobe.module.user.entity.User;
import java.util.UUID;

public interface UserService {

  UserResponse register(UserCreationRequest request);

  UserResponse updateProfile(UserUpdateRequest request);

  void verifyEmail(String email);

  UserResponse getCurrentUser();

  UserResponse getUserById(UUID userId);

  User getUserByEmail(String email);

  User getById(UUID userId);
}

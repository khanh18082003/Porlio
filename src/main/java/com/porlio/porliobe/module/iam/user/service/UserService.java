package com.porlio.porliobe.module.iam.user.service;

import com.porlio.porliobe.module.iam.user.dto.request.UserCreationRequest;
import com.porlio.porliobe.module.iam.user.dto.request.UserUpdateRequest;
import com.porlio.porliobe.module.iam.user.dto.response.AdminUserResponse;
import com.porlio.porliobe.module.iam.user.dto.response.RegistrationResponse;
import com.porlio.porliobe.module.iam.user.dto.response.UserResponse;
import com.porlio.porliobe.module.iam.user.entity.User;
import java.util.UUID;

public interface UserService {

  RegistrationResponse register(UserCreationRequest request);

  UserResponse updateProfile(UserUpdateRequest request);

  void verifyEmail(String email);

  UserResponse getCurrentUser();

  AdminUserResponse getUserById(UUID userId);

  User getUserByEmail(String email);

  User getById(UUID userId);
}

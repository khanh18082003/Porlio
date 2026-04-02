package com.porlio.porliobe.module.iam.user.mapper;

import com.porlio.porliobe.module.iam.user.dto.request.UserCreationRequest;
import com.porlio.porliobe.module.iam.user.dto.request.UserUpdateRequest;
import com.porlio.porliobe.module.iam.user.dto.response.AdminUserResponse;
import com.porlio.porliobe.module.iam.user.dto.response.RegistrationResponse;
import com.porlio.porliobe.module.iam.user.dto.response.UserResponse;
import com.porlio.porliobe.module.iam.user.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toUser(UserCreationRequest request);

  UserResponse toUserResponse(User user);

  AdminUserResponse toAdminUserResponse(User user);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void mergeToUser(UserUpdateRequest request, @MappingTarget User user);

  RegistrationResponse toRegistrationResponse(User user);
}

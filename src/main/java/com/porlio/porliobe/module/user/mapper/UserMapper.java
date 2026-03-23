package com.porlio.porliobe.module.user.mapper;

import com.porlio.porliobe.module.user.dto.request.UserCreationRequest;
import com.porlio.porliobe.module.user.dto.request.UserUpdateRequest;
import com.porlio.porliobe.module.user.dto.response.UserResponse;
import com.porlio.porliobe.module.user.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toUser(UserCreationRequest request);

  UserResponse toUserResponse(User user);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void mergeToUser(UserUpdateRequest request, @MappingTarget User user);
}

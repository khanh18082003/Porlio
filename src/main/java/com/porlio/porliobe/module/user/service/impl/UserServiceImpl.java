package com.porlio.porliobe.module.user.service.impl;

import com.porlio.porliobe.module.admin.service.RoleService;
import com.porlio.porliobe.module.notification.service.NotificationService;
import com.porlio.porliobe.module.shared.aop.RequireEmailVerified;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.shared.data.constant.RoleName;
import com.porlio.porliobe.module.shared.exception.AppException;
import com.porlio.porliobe.module.shared.utils.SecurityUtil;
import com.porlio.porliobe.module.user.dto.request.UserCreationRequest;
import com.porlio.porliobe.module.user.dto.request.UserUpdateRequest;
import com.porlio.porliobe.module.user.dto.response.UserResponse;
import com.porlio.porliobe.module.user.entity.User;
import com.porlio.porliobe.module.user.mapper.UserMapper;
import com.porlio.porliobe.module.user.repository.UserRepository;
import com.porlio.porliobe.module.user.service.UserService;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "USER_SERVICE")
public class UserServiceImpl implements UserService {

  PasswordEncoder passwordEncoder;
  UserRepository userRepository;
  UserMapper userMapper;
  RoleService roleService;
  NotificationService notificationService;
  RedisTemplate<String, Object> redisTemplate;

  @NonFinal
  @Value("${jwt.expiration.verification}")
  Long verificationTokenExpiration;

  @Transactional
  @Override
  public UserResponse register(UserCreationRequest request) {
    // Step 1: Map request to User entity
    User newUser = userMapper.toUser(request);

    // Step 2: Hash the password
    newUser.setPasswordHash(passwordEncoder.encode(request.password()));

    // Step 3: Assign default role (USER)
    var roles = roleService.getAllRolesByKeyIn(Set.of(RoleName.USER));
    newUser.setRoles(roles);

    // Step 4: Save the user to the database
    User savedUser = userRepository.save(newUser);

    // Step 5: Generate email verification token and save to Redis
    String verifyToken = UUID.randomUUID().toString().replace("-", "");
    String redisKey = "verify_token:" + request.email();
    redisTemplate.opsForValue()
        .set(redisKey, verifyToken, verificationTokenExpiration * 3600, TimeUnit.SECONDS);

    // Step 6: Send verification email
    notificationService.sendVerificationEmail(
        savedUser.getEmail(),
        savedUser.getUsername(),
        verifyToken
    );

    return userMapper.toUserResponse(savedUser);
  }

  @RequireEmailVerified
  @Override
  public UserResponse updateProfile(UserUpdateRequest request) {
    String userId = SecurityUtil.getCurrentUserId();

    User user = userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_RESOURCE_NOT_FOUND));

    userMapper.mergeToUser(request, user);

    return userMapper.toUserResponse(userRepository.save(user));
  }

  @Override
  public void verifyEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_RESOURCE_NOT_FOUND));
    user.setIsVerified(true);
    user.setVerifiedAt(LocalDateTime.now());
    userRepository.save(user);
  }

  @Override
  public UserResponse getCurrentUser() {
    String userId = SecurityUtil.getCurrentUserId();

    User user = userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_RESOURCE_NOT_FOUND));
    return userMapper.toUserResponse(user);
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_RESOURCE_NOT_FOUND));
  }

  @Override
  public User getById(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_RESOURCE_NOT_FOUND));
  }

  @Override
  public UserResponse getUserById(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toUserResponse)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_RESOURCE_NOT_FOUND));
  }
}

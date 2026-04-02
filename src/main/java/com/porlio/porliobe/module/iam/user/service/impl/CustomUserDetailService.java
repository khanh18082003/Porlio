package com.porlio.porliobe.module.iam.user.service.impl;

import com.porlio.porliobe.module.iam.user.entity.CustomUserDetail;
import com.porlio.porliobe.module.iam.user.entity.User;
import com.porlio.porliobe.module.iam.user.repository.UserRepository;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.shared.exception.AppException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "CUSTOM_USER_DETAIL_SERVICE")
public class CustomUserDetailService implements UserDetailsService {

  UserRepository userRepository;

  @NullMarked
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_RESOURCE_NOT_FOUND));
    return new CustomUserDetail(user);
  }
}

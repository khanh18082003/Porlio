package com.porlio.porliobe.module.iam.verification.service.impl;

import com.porlio.porliobe.module.iam.verification.dto.request.ResendVerificationTokenRequest;
import com.porlio.porliobe.module.iam.verification.dto.request.VerificationCodeRequest;
import com.porlio.porliobe.module.iam.verification.dto.response.VerificationCodeResponse;
import com.porlio.porliobe.module.iam.user.entity.User;
import com.porlio.porliobe.module.iam.user.service.UserService;
import com.porlio.porliobe.module.iam.verification.service.VerificationService;
import com.porlio.porliobe.module.notification.service.NotificationService;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "VERIFICATION_SERVICE")
public class VerificationServiceImpl implements VerificationService {

  UserService userService;
  RedisTemplate<String, Object> redisTemplate;
  NotificationService notificationService;

  @NonFinal
  @Value("${jwt.expiration.verification}")
  Long verificationTokenExpiration;

  @Override
  public VerificationCodeResponse verifyCode(VerificationCodeRequest request) {
    String redisKey = "verify_token:" + request.email();
    Object token = redisTemplate.opsForValue().get(redisKey);
    boolean isValid = token != null && token.equals(request.token());
    if (isValid) {
      redisTemplate.delete(redisKey);
      userService.verifyEmail(request.email());
    }
    return new VerificationCodeResponse(isValid);
  }

  @Override
  public void resendVerificationCode(ResendVerificationTokenRequest request) {
    User user = userService.getUserByEmail(request.email());
    if (Boolean.TRUE.equals(user.getIsVerified())) {
      log.info("User with email {} is already verified. No need to resend verification code.",
          request.email());
      return;
    }
    String username = user.getUsername();

    String redisKey = "verify_token:" + request.email();
    String verifyToken = UUID.randomUUID().toString().replace("-", "");
    redisTemplate.opsForValue()
        .set(redisKey, verifyToken, verificationTokenExpiration * 3600, TimeUnit.SECONDS);

    notificationService.sendVerificationEmail(request.email(), username, verifyToken);
  }
}

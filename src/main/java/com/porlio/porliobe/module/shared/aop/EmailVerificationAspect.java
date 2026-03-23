package com.porlio.porliobe.module.shared.aop;

import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.shared.exception.AppException;
import com.porlio.porliobe.module.shared.utils.SecurityUtil;
import com.porlio.porliobe.module.user.entity.User;
import com.porlio.porliobe.module.user.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class EmailVerificationAspect {

  private final UserService userService;

  @Before("@annotation(RequireEmailVerified)")
  public void checkEmailVerification() {
    String userId = SecurityUtil.getCurrentUserId();
    User currentUser = userService.getById(UUID.fromString(userId));

    if (!currentUser.getIsVerified()) {
      throw new AppException(ErrorCode.MESSAGE_EMAIL_NOT_VERIFIED);
    }
  }
}

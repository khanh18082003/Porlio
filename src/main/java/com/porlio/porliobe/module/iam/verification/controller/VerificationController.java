package com.porlio.porliobe.module.iam.verification.controller;

import com.porlio.porliobe.module.iam.verification.dto.request.ResendVerificationTokenRequest;
import com.porlio.porliobe.module.iam.verification.dto.request.VerificationCodeRequest;
import com.porlio.porliobe.module.iam.verification.dto.response.VerificationCodeResponse;
import com.porlio.porliobe.module.iam.verification.service.VerificationService;
import com.porlio.porliobe.module.shared.data.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.prefix}/verification")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "VERIFICATION_CONTROLLER")
public class VerificationController {

  VerificationService verificationService;

  @PostMapping("/verification-code")
  ApiResponse<VerificationCodeResponse> sendVerificationCode(
      @RequestBody @Valid VerificationCodeRequest request) {
    log.info("Received request to send verification code for email: {}", request.email());
    return ApiResponse.ok(verificationService.verifyCode(request));
  }

  @PostMapping("/resend-verification-code")
  ApiResponse<Void> resendCode(
      @RequestBody @Valid ResendVerificationTokenRequest request) {
    log.info("Received request to resend verification code for email: {}", request.email());
    verificationService.resendVerificationCode(request);
    return ApiResponse.ok();
  }
}

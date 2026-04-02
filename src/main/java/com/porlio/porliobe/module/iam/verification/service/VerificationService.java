package com.porlio.porliobe.module.iam.verification.service;

import com.porlio.porliobe.module.iam.verification.dto.request.ResendVerificationTokenRequest;
import com.porlio.porliobe.module.iam.verification.dto.request.VerificationCodeRequest;
import com.porlio.porliobe.module.iam.verification.dto.response.VerificationCodeResponse;

public interface VerificationService {

  VerificationCodeResponse verifyCode(VerificationCodeRequest request);

  void resendVerificationCode(ResendVerificationTokenRequest request);
}

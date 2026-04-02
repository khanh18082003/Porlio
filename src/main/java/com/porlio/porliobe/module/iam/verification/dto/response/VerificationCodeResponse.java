package com.porlio.porliobe.module.iam.verification.dto.response;

import java.io.Serializable;

public record VerificationCodeResponse(
    boolean success
) implements Serializable {

}

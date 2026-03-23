package com.porlio.porliobe.module.auth.dto.response;

import java.io.Serializable;

public record VerificationCodeResponse(
    boolean success
) implements Serializable {

}

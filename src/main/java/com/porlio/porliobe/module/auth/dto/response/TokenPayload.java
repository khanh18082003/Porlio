package com.porlio.porliobe.module.auth.dto.response;

import java.util.Date;
import lombok.Builder;

@Builder
public record TokenPayload(
    String jwtId,
    String token,
    Date issueTime,
    Date expiration
) {

}

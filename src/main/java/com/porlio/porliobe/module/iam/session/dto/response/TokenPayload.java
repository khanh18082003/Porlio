package com.porlio.porliobe.module.iam.session.dto.response;

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

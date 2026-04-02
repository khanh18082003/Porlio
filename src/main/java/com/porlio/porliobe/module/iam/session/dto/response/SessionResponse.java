package com.porlio.porliobe.module.iam.session.dto.response;

import java.io.Serializable;
import lombok.Builder;

@Builder
public record SessionResponse(
    String token,
    String tokenType
) implements Serializable {

}

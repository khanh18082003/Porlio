package com.porlio.porliobe.module.auth.dto.response;

import java.io.Serializable;
import lombok.Builder;

@Builder
public record AuthenticationResponse(
    String token,
    String tokenType
) implements Serializable {

}

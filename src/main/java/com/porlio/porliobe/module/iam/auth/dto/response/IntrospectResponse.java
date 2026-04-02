package com.porlio.porliobe.module.iam.auth.dto.response;

import java.io.Serializable;
import lombok.Builder;

@Builder
public record IntrospectResponse(
    boolean authenticated
) implements Serializable {

}

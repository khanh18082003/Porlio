package com.porlio.porliobe.module.user.dto.request;

import java.io.Serializable;

public record UserUpdateRequest(
    String username,
    String fullName,
    String avatarUrl
) implements Serializable {

}

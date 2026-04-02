package com.porlio.porliobe.module.iam.user.dto.request;

import java.io.Serializable;

public record UserUpdateRequest(
    String username,
    String fullName,
    String headline,
    String bio,
    String professionType,
    String location,
    String websiteUrl,
    String avatarUrl
) implements Serializable {

}

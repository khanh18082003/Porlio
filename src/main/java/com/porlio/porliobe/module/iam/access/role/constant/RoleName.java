package com.porlio.porliobe.module.iam.access.role.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleName {
  ADMIN("Administrator", "Full system access"),
  USER("User", "Standard user access");

  private final String name;
  private final String description;

}

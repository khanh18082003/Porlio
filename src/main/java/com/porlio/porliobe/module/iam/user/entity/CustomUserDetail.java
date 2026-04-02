package com.porlio.porliobe.module.iam.user.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomUserDetail implements UserDetails {

  User user;

  @NullMarked
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities = new HashSet<>();
    authorities.addAll(
        user.getRoles().stream().map(
            role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleKey())
        ).toList()
    );
    authorities.addAll(user.getRoles().stream()
        .flatMap(role -> role.getPermissions().stream())
        .map(permission -> new SimpleGrantedAuthority(permission.getPermissionKey()))
        .toList());
    return authorities;
  }


  @Override
  public @Nullable String getPassword() {
    return user.getPasswordHash();
  }

  @NullMarked
  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return UserDetails.super.isEnabled();
  }
}

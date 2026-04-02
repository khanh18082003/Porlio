package com.porlio.porliobe.module.github.entity;

import com.porlio.porliobe.module.iam.user.entity.User;
import com.porlio.porliobe.module.shared.data.base.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "github_connections")
public class GithubConnection extends AbstractEntity<UUID> {

  @Serial
  private static final long serialVersionUID = -3536185829402821221L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @ToString.Exclude
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  User user;

  @Column(name = "github_user_id", nullable = false)
  Long githubUserId;

  @Column(name = "github_login", length = 100)
  String githubLogin;

  @Column(name = "encrypted_access_token", nullable = false, columnDefinition = "TEXT")
  String encryptedAccessToken;

  @Column(name = "scope", length = 500)
  String scope;

  @Column(name = "token_type", nullable = false, length = 50)
  @Builder.Default
  String tokenType = "bearer";

  @Column(name = "connected_at", nullable = false)
  Instant connectedAt;

  @Column(name = "expires_at")
  Instant expiresAt;

  @Column(name = "last_synced_at")
  Instant lastSyncedAt;
}

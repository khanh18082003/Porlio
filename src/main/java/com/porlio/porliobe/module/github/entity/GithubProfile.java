package com.porlio.porliobe.module.github.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;
import org.hibernate.type.SqlTypes;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "github_profiles")
public class GithubProfile extends AbstractEntity<UUID> {

  @Serial
  private static final long serialVersionUID = 6731380948578331880L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @ToString.Exclude
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  User user;

  @Column(name = "name", length = 150)
  String name;

  @Column(name = "bio", columnDefinition = "TEXT")
  String bio;

  @Column(name = "blog_url", columnDefinition = "TEXT")
  String blogUrl;

  @Column(name = "avatar_url", columnDefinition = "TEXT")
  String avatarUrl;

  @Column(name = "followers_count", nullable = false)
  @Builder.Default
  Integer followersCount = 0;

  @Column(name = "following_count", nullable = false)
  @Builder.Default
  Integer followingCount = 0;

  @Column(name = "public_repo_count", nullable = false)
  @Builder.Default
  Integer publicRepoCount = 0;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "raw_payload", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode rawPayload = JsonNodeFactory.instance.objectNode();

  @Column(name = "last_synced_at", nullable = false)
  Instant lastSyncedAt;
}

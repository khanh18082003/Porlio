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
import jakarta.persistence.ManyToOne;
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
@Table(name = "github_repositories")
public class GithubRepository extends AbstractEntity<UUID> {

  @Serial
  private static final long serialVersionUID = 6385506119560564781L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Column(name = "github_repo_id")
  Long githubRepoId;

  @Column(name = "name", nullable = false, length = 150)
  String name;

  @Column(name = "full_name", nullable = false, length = 255)
  String fullName;

  @Column(name = "description", columnDefinition = "TEXT")
  String description;

  @Column(name = "html_url", nullable = false, columnDefinition = "TEXT")
  String htmlUrl;

  @Column(name = "homepage_url", columnDefinition = "TEXT")
  String homepageUrl;

  @Column(name = "primary_language", length = 80)
  String primaryLanguage;

  @Column(name = "stars_count", nullable = false)
  @Builder.Default
  Integer starsCount = 0;

  @Column(name = "forks_count", nullable = false)
  @Builder.Default
  Integer forksCount = 0;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "topics", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode topics = JsonNodeFactory.instance.arrayNode();

  @Column(name = "is_fork", nullable = false)
  @Builder.Default
  Boolean isFork = false;

  @Column(name = "is_private", nullable = false)
  @Builder.Default
  Boolean isPrivate = false;

  @Column(name = "is_selected", nullable = false)
  @Builder.Default
  Boolean isSelected = false;

  @Column(name = "pushed_at")
  Instant pushedAt;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "raw_payload", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode rawPayload = JsonNodeFactory.instance.objectNode();

  @Column(name = "last_synced_at", nullable = false)
  Instant lastSyncedAt;
}

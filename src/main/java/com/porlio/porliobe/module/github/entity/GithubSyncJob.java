package com.porlio.porliobe.module.github.entity;

import com.porlio.porliobe.module.iam.user.entity.User;
import com.porlio.porliobe.module.shared.data.base.AbstractCreatedAtAuditableEntity;
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
@Table(name = "github_sync_jobs")
public class GithubSyncJob extends AbstractCreatedAtAuditableEntity<UUID> {

  @Serial
  private static final long serialVersionUID = 4072519350718421958L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Column(name = "status", nullable = false, length = 30)
  String status;

  @Column(name = "synced_repo_count", nullable = false)
  @Builder.Default
  Integer syncedRepoCount = 0;

  @Column(name = "error_message", columnDefinition = "TEXT")
  String errorMessage;

  @Column(name = "started_at")
  Instant startedAt;

  @Column(name = "finished_at")
  Instant finishedAt;
}

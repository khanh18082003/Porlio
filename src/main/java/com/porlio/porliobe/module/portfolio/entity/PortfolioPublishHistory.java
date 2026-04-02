package com.porlio.porliobe.module.portfolio.entity;

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
@Table(name = "portfolio_publish_history")
public class PortfolioPublishHistory extends AbstractEntity<UUID> {

  @Serial
  private static final long serialVersionUID = -7398267002613136697L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "portfolio_id", nullable = false)
  Portfolio portfolio;

  @Column(name = "version", nullable = false)
  Integer version;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "snapshot", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode snapshot = JsonNodeFactory.instance.objectNode();

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "published_by")
  User publishedBy;

  @Column(name = "published_at", nullable = false)
  Instant publishedAt;
}

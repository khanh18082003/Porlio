package com.porlio.porliobe.module.portfolio.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.porlio.porliobe.module.shared.data.base.AbstractTimestampAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
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
@Table(name = "portfolio_sections")
public class PortfolioSection extends AbstractTimestampAuditableEntity<UUID> {

  @Serial
  private static final long serialVersionUID = 2294517438895453980L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "portfolio_id", nullable = false)
  Portfolio portfolio;

  @Column(name = "section_key", nullable = false, length = 60)
  String sectionKey;

  @Column(name = "type", nullable = false, length = 50)
  String type;

  @Column(name = "variant", length = 40)
  String variant;

  @Column(name = "source", nullable = false, length = 20)
  @Builder.Default
  String source = "manual";

  @Column(name = "title", length = 120)
  String title;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "content", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode content = JsonNodeFactory.instance.objectNode();

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "settings", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode settings = JsonNodeFactory.instance.objectNode();

  @Column(name = "order_index", nullable = false)
  @Builder.Default
  Short orderIndex = 0;

  @Column(name = "is_visible", nullable = false)
  @Builder.Default
  Boolean isVisible = true;
}

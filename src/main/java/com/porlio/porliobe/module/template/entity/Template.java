package com.porlio.porliobe.module.template.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.porlio.porliobe.module.shared.data.base.AbstractTimestampAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "templates")
public class Template extends AbstractTimestampAuditableEntity<UUID> {

  @Serial
  private static final long serialVersionUID = 2572280612982355566L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @Column(name = "name", nullable = false, length = 100)
  String name;

  @Column(name = "slug", nullable = false, unique = true, length = 50)
  String slug;

  @Column(name = "category", nullable = false, length = 50)
  @Builder.Default
  String category = "minimal";

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "industry_tags", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode industryTags = JsonNodeFactory.instance.arrayNode();

  @Column(name = "summary", length = 200)
  String summary;

  @Column(name = "thumbnail_url", columnDefinition = "TEXT")
  String thumbnailUrl;

  @Column(name = "preview_url", columnDefinition = "TEXT")
  String previewUrl;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "template_payload", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode templatePayload = JsonNodeFactory.instance.objectNode();

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "theme_modes", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode themeModes = JsonNodeFactory.instance.arrayNode()
      .add("light")
      .add("dark");

  @Column(name = "schema_version", nullable = false)
  @Builder.Default
  Integer schemaVersion = 1;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  Boolean isActive = true;

  @Column(name = "is_featured", nullable = false)
  @Builder.Default
  Boolean isFeatured = false;

  @Column(name = "sort_order", nullable = false)
  @Builder.Default
  Short sortOrder = 0;

  @Column(name = "usage_count", nullable = false)
  @Builder.Default
  Integer usageCount = 0;
}

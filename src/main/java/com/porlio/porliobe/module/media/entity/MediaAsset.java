package com.porlio.porliobe.module.media.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.porlio.porliobe.module.iam.user.entity.User;
import com.porlio.porliobe.module.portfolio.entity.Portfolio;
import com.porlio.porliobe.module.shared.data.base.AbstractCreatedAtAuditableEntity;
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
@Table(name = "media_assets")
public class MediaAsset extends AbstractCreatedAtAuditableEntity<UUID> {

  @Serial
  private static final long serialVersionUID = 209073171792357169L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_user_id", nullable = false)
  User ownerUser;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "portfolio_id")
  Portfolio portfolio;

  @Column(name = "type", nullable = false, length = 30)
  String type;

  @Column(name = "provider", nullable = false, length = 30)
  String provider;

  @Column(name = "storage_key", nullable = false, length = 255)
  String storageKey;

  @Column(name = "public_url", nullable = false, columnDefinition = "TEXT")
  String publicUrl;

  @Column(name = "mime_type", nullable = false, length = 100)
  String mimeType;

  @Column(name = "size_bytes", nullable = false)
  Long sizeBytes;

  @Column(name = "checksum", length = 128)
  String checksum;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "metadata", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode metadata = JsonNodeFactory.instance.objectNode();
}

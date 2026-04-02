package com.porlio.porliobe.module.admin.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
@Table(name = "audit_logs")
public class AuditLog extends AbstractCreatedAtAuditableEntity<UUID> {

  @Serial
  private static final long serialVersionUID = -7049517423209652044L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "actor_id")
  User actor;

  @Column(name = "actor_email", length = 255)
  String actorEmail;

  @Column(name = "action", nullable = false, length = 100)
  String action;

  @Column(name = "target_type", length = 50)
  String targetType;

  @Column(name = "target_id")
  UUID targetId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "detail", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode detail = JsonNodeFactory.instance.objectNode();

  @Column(name = "ip_address", length = 45)
  String ipAddress;

  @Column(name = "user_agent", length = 500)
  String userAgent;
}

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
@Table(name = "portfolio_theme_settings")
public class PortfolioThemeSetting extends AbstractTimestampAuditableEntity<UUID> {

  @Serial
  private static final long serialVersionUID = -3298528010565798999L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "portfolio_id", nullable = false)
  Portfolio portfolio;

  @Column(name = "mode", nullable = false, length = 20)
  String mode;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "token_overrides", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode tokenOverrides = JsonNodeFactory.instance.objectNode();

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "font_pair", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  JsonNode fontPair = JsonNodeFactory.instance.objectNode();
}

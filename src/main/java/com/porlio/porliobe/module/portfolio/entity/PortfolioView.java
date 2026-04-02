package com.porlio.porliobe.module.portfolio.entity;

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
@Table(name = "portfolio_views")
public class PortfolioView extends AbstractEntity<UUID> {

  @Serial
  private static final long serialVersionUID = -465521082221094337L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "portfolio_id", nullable = false)
  Portfolio portfolio;

  @Column(name = "referrer", length = 300)
  String referrer;

  @Column(name = "country_code", length = 10)
  String countryCode;

  @Column(name = "ip_hash", length = 128)
  String ipHash;

  @Column(name = "user_agent_hash", length = 128)
  String userAgentHash;

  @Column(name = "visited_at", nullable = false)
  Instant visitedAt;
}

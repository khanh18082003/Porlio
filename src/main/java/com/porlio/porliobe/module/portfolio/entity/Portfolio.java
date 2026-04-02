package com.porlio.porliobe.module.portfolio.entity;

import com.porlio.porliobe.module.iam.user.entity.User;
import com.porlio.porliobe.module.shared.data.base.AbstractTimestampAuditableEntity;
import com.porlio.porliobe.module.template.entity.Template;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "portfolios")
public class Portfolio extends AbstractTimestampAuditableEntity<UUID> {

  @Serial
  private static final long serialVersionUID = 8636142487519485323L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @ToString.Exclude
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  User user;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "template_id")
  Template template;

  @Column(name = "custom_slug", unique = true, length = 50)
  String customSlug;

  @Column(name = "status", nullable = false, length = 30)
  @Builder.Default
  String status = "draft";

  @Column(name = "seo_title", length = 100)
  String seoTitle;

  @Column(name = "seo_description", length = 300)
  String seoDescription;

  @Column(name = "og_image_url", columnDefinition = "TEXT")
  String ogImageUrl;

  @Column(name = "current_theme_mode", nullable = false, length = 20)
  @Builder.Default
  String currentThemeMode = "light";

  @Column(name = "view_count", nullable = false)
  @Builder.Default
  Long viewCount = 0L;

  @Column(name = "published_at")
  Instant publishedAt;

  @Column(name = "last_edited_at", nullable = false)
  Instant lastEditedAt;
}

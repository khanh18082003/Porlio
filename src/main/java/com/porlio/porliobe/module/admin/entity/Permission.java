package com.porlio.porliobe.module.admin.entity;

import com.porlio.porliobe.module.shared.data.base.AbstractCreatedAtAuditableEntity;
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
@Table(name = "permissions")
public class Permission extends AbstractCreatedAtAuditableEntity<UUID> {

  @Serial
  private static final long serialVersionUID = -5401351954909212574L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  /**
   * Technical key dùng trong @PreAuthorize. Ví dụ: 'user:ban', 'template:create' Đây là giá trị
   * được check trong Spring Security:
   */
  @Column(name = "permission_key", nullable = false, unique = true, length = 100)
  String permissionKey;

  /**
   * Tên hiển thị trên Admin UI. Ví dụ: 'Ban / Unban User', 'Create Template'
   */
  @Column(name = "name", nullable = false, length = 100)
  String name;

  /**
   * HTTP Method liên quan đến permission này. Ví dụ: 'GET', 'POST', 'PATCH', 'DELETE'
   * <p>
   * Chỉ dùng để HIỂN THỊ trên Admin UI, KHÔNG dùng để enforce authorization (đã có @PreAuthorize).
   */
  @Column(name = "method", nullable = false, length = 10)
  String method;

  /**
   * URL pattern liên quan đến permission này. Ví dụ: '/api/v1/admin/users/{id}/ban'
   * <p>
   * Chỉ dùng để HIỂN THỊ và tham khảo trên Admin UI, KHÔNG dùng để enforce authorization.
   */
  @Column(name = "url_pattern", nullable = false, length = 200)
  String urlPattern;

  /**
   * Mô tả chi tiết permission này làm gì.
   */
  @Column(name = "description", length = 200)
  String description;

  /**
   * Nhóm module để hiển thị grouped trên Admin UI. Giá trị: 'USER' | 'PORTFOLIO' | 'TEMPLATE' |
   * 'SYSTEM'
   */
  @Column(name = "module", nullable = false, length = 50)
  String module;
}

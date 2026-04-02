package com.porlio.porliobe.module.iam.access.role.entity;

import com.porlio.porliobe.module.iam.access.permission.entity.Permission;
import com.porlio.porliobe.module.shared.data.base.AbstractCreatedAtAuditableEntity;
import com.porlio.porliobe.module.iam.access.role.constant.RoleName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.HashSet;
import java.util.Set;
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
@Table(name = "roles")
public class Role extends AbstractCreatedAtAuditableEntity<UUID> {

  @Serial
  private static final long serialVersionUID = -1467336736045256893L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  /**
   * Technical key dùng trong code và logic. Ví dụ: 'ADMIN', 'USER' Map với Java Enum RoleName để
   * type-safe.
   *
   * @Enumerated(EnumType.STRING): lưu tên enum ('ADMIN') thay vì số thứ tự (0, 1). LUÔN dùng STRING
   * để tránh bug khi thêm/xóa/sắp xếp lại enum values.
  */
  @Enumerated(EnumType.STRING)
  @Column(name = "role_key", nullable = false, unique = true, length = 50)
  RoleName roleKey;

  /**
   * Tên hiển thị trên UI. Ví dụ: 'Administrator', 'User'
   */
  @Column(name = "name", nullable = false, length = 50)
  String name;

  /**
   * Mô tả role để admin hiểu mục đích. Nullable vì không bắt buộc.
   */
  @Column(name = "description", length = 200)
  String description;

  /**
   * Quan hệ Many-to-Many với Permission. Một role có nhiều permissions, một permission thuộc nhiều
   * roles.
   *
   * @ManyToMany với fetch = EAGER: Load permissions ngay khi load Role. Lý do dùng EAGER ở đây:
   * Role luôn cần permissions để check authorization. Chú ý: Chỉ dùng EAGER cho tập dữ liệu nhỏ
   * (permissions không nhiều).
   * @JoinTable: Định nghĩa bảng trung gian `role_permissions`.
   */
  @ToString.Exclude
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "role_permissions",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  @Builder.Default
  Set<Permission> permissions = new HashSet<>();
}

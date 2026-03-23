package com.porlio.porliobe.module.user.entity;

import com.porlio.porliobe.module.admin.entity.Role;
import com.porlio.porliobe.module.shared.data.base.AbstractTimestampAuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.LocalDateTime;
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
@Table(name = "users")
public class User extends AbstractTimestampAuditableEntity<UUID> {

  @Serial
  private static final long serialVersionUID = -2389619548870287712L;

  @Id
  @UuidGenerator(style = Style.TIME)
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @Column(name = "email", nullable = false, unique = true)
  String email;

  /**
   * Hash của password (BCrypt, 60 chars). NULL nếu user đăng nhập bằng GitHub OAuth (không có
   * password).
   * <p>
   * KHÔNG BAO GIỜ lưu plain text password vào đây. Service layer chịu trách nhiệm hash trước khi
   * set.
   */
  @Column(name = "password_hash")
  String passwordHash;

  // ── THÔNG TIN HỒ SƠ ─────────────────────────────────

  /**
   * Username dùng làm slug URL: yourapp.com/{username}
   * Chỉ cho phép: chữ thường a-z, số 0-9, dấu _ và -
   * Độ dài: 3-50 ký tự (enforced bởi DB constraint)
   */
  @Column(name = "username", nullable = false, length = 50)
  String username;

  @Column(name = "full_name", length = 100)
  String fullName;

  /**
   * URL ảnh avatar (từ Cloudinary hoặc GitHub avatar).
   * Dùng TEXT vì Cloudinary URL có thể rất dài (có transformation params).
   */
  @Column(name = "avatar_url", columnDefinition = "TEXT")
  String avatarUrl;

  // ── GITHUB ───────────────────────────────────────────

  /**
   * GitHub user ID (BIGINT vì có thể là số 9 chữ số).
   * NULL nếu chưa liên kết GitHub.
   */
  @Column(name = "github_id")
  Long githubId;

  // ── TRẠNG THÁI ───────────────────────────────────────

  /**
   * FALSE khi bị admin ban.
   * Mọi request từ user bị ban sẽ nhận 403 Forbidden.
   */
  @Column(name = "is_active", nullable = false)
  @Builder.Default
  Boolean isActive = true;

  /**
   * Soft delete flag.
   * TRUE = tài khoản đã xóa, không hiển thị ở bất kỳ đâu.
   * Không bao giờ hard delete để giữ audit trail.
   */
  @Column(name = "is_deleted", nullable = false)
  @Builder.Default
  Boolean isDeleted = false;

  @Column(name = "is_verified", nullable = false)
  @Builder.Default
  private Boolean isVerified = false;

  /**
   * Thời điểm xác nhận email thành công.
   * NULL nếu chưa xác nhận.
   */
  @Column(name = "verified_at")
  private LocalDateTime verifiedAt;

  /**
   * Thời điểm soft delete.
   * NULL nếu chưa xóa.
   */
  @Column(name = "deleted_at")
  LocalDateTime deletedAt;

  // ── RELATIONSHIPS ────────────────────────────────────

  /**
   * Quan hệ Many-to-Many với Role qua bảng `user_roles`.
   * fetch = EAGER: Load roles ngay khi load User.
   * Cần thiết vì Spring Security cần roles/permissions ngay khi authenticate.
   * cascade = {PERSIST, MERGE}: Khi save User, nếu Role chưa có trong DB thì tạo.
   * Không dùng ALL để tránh vô tình xóa Role khi xóa User.
   */
  @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  @Builder.Default
  Set<Role> roles = new HashSet<>();

  /**
   * Soft delete helper.
   * Gọi method này thay vì set trực tiếp để đảm bảo deletedAt luôn được set.
   */
  public void softDelete() {
    this.isDeleted = true;
    this.isActive  = false;
    this.deletedAt = LocalDateTime.now();
  }
}

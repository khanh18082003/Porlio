package com.porlio.porliobe.module.admin.service.impl;

import com.porlio.porliobe.module.iam.access.permission.entity.Permission;
import com.porlio.porliobe.module.iam.access.permission.repository.PermissionRepository;
import com.porlio.porliobe.module.iam.access.role.entity.Role;
import com.porlio.porliobe.module.iam.access.role.repository.RoleRepository;
import com.porlio.porliobe.module.iam.user.entity.User;
import com.porlio.porliobe.module.iam.user.repository.UserRepository;
import com.porlio.porliobe.module.shared.data.constant.ErrorCode;
import com.porlio.porliobe.module.iam.access.permission.constant.PermissionKey;
import com.porlio.porliobe.module.iam.access.role.constant.RoleName;
import com.porlio.porliobe.module.shared.exception.AppException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "APPLICATION_INIT_SERVICE")
public class ApplicationInitService {

  PasswordEncoder passwordEncoder;
  PermissionRepository permissionRepository;
  RoleRepository roleRepository;
  UserRepository userRepository;

  @NonFinal
  @Value("${app.admin.email}")
  String adminEmail;

  @NonFinal
  @Value("${app.admin.password}")
  String adminPassword;

  @NonFinal
  @Value("${app.admin.username}")
  String adminUsername;

  @Transactional
  public void init() {
    log.info("=== Initializing application data ===");
    initPermissions();
    initRoles();
    initAdmin();
    log.info("=== Application data initialized ===");
  }

  // ─────────────────────────────────────────────────────
  // STEP 1: PERMISSIONS
  // ─────────────────────────────────────────────────────

  private void initPermissions() {
    Set<String> existingKeys = permissionRepository.findAllKeys();
    Set<Permission> createdPermissions = new HashSet<>();
    int created = 0;

    for (PermissionKey pk : PermissionKey.values()) {

      // Chỉ tạo nếu chưa tồn tại — idempotent
      if (existingKeys.contains(pk.getKey())) {
        continue;
      }

      Permission permission = Permission.builder()
          .permissionKey(pk.getKey())
          .name(pk.getName())
          .method(pk.getMethod())
          .urlPattern(pk.getUrlPattern())
          .module(pk.getModule())
          .build();
      createdPermissions.add(permission);
      created++;

      log.info("Created permission: [{}] {}", pk.getModule(), pk.getKey());
    }

    if (created == 0) {
      log.info("  ✓ All permissions already exist, skipping.");
    } else {
      permissionRepository.saveAll(createdPermissions);
      log.info("  → Created {} new permission(s).", created);
    }
  }

  // ─────────────────────────────────────────────────────
  // STEP 2: ROLES + GÁN PERMISSIONS
  // ─────────────────────────────────────────────────────

  private void initRoles() {
    for (RoleName roleName : RoleName.values()) {

      // Lấy role nếu đã có, tạo mới nếu chưa có
      Role role = roleRepository.findByRoleKey(roleName)
          .orElseGet(() -> {
            log.info("Creating role: {}", roleName);
            return roleRepository.save(
                Role.builder()
                    .roleKey(roleName)
                    .name(roleName.getName())
                    .description(roleName.getDescription())
                    .build()
            );
          });

      // Gán permissions cho role
      assignPermissionsToRole(role, roleName);
    }
  }

  private void assignPermissionsToRole(Role role, RoleName roleName) {
    Set<String> permissionKeys = resolvePermissionsForRole(roleName);

    // Load permissions từ DB theo keys
    Set<Permission> permissions = permissionRepository.findByPermissionKeyIn(permissionKeys);

    // Chỉ update nếu có sự thay đổi
    // (tránh dirty write mỗi lần khởi động)
    boolean hasChanges = !role.getPermissions().equals(permissions);

    if (hasChanges) {
      role.setPermissions(permissions);
      roleRepository.save(role);
      log.info("Updated permissions for role [{}]: {} permission(s)",
          roleName, permissions.size());
    } else {
      log.info("Role [{}] permissions up to date.", roleName);
    }
  }

  /**
   * Định nghĩa tập permissions cho từng role.
   * <p>
   * ADMIN   → tất cả permissions USER    → không có permission admin nào
   * <p>
   * Đây là nơi DUY NHẤT quyết định role có quyền gì. Sau này thêm MODERATOR chỉ cần thêm case ở
   * đây.
   */
  private Set<String> resolvePermissionsForRole(RoleName roleName) {
    return switch (roleName) {
      case ADMIN -> Arrays.stream(PermissionKey.values())
          .map(PermissionKey::getKey)
          .collect(Collectors.toSet());

      case USER -> Set.of();
    };
  }

  // ─────────────────────────────────────────────────────
  // STEP 3: ADMIN USER
  // ─────────────────────────────────────────────────────

  private void initAdmin() {
    if (userRepository.existsByEmail(adminEmail)) {
      log.info("Admin user already exists, skipping.");
      return;
    }

    Role adminRole = roleRepository.findByRoleKey(RoleName.ADMIN)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_RESOURCE_NOT_FOUND));

    User admin = User.builder()
        .email(adminEmail)
        .username(adminUsername)
        .passwordHash(passwordEncoder.encode(adminPassword))
        .fullName("System Administrator")
        .isActive(true)
        .isDeleted(false)
        .roles(new HashSet<>(Set.of(adminRole)))
        .build();

    userRepository.save(admin);
    log.info("Admin user created: {}", adminEmail);
    log.warn("IMPORTANT: Change admin password immediately after first login!");
  }
}

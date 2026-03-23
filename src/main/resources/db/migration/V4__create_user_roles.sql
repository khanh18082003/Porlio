-- ════════════════════════════════════════════
-- BẢNG: user_roles
-- Bảng trung gian Many-to-Many: User <-> Role
-- ════════════════════════════════════════════
CREATE TABLE user_roles
(
    user_id     UUID      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    -- ON DELETE CASCADE: user bị xóa → xóa luôn tất cả role assignment

    role_id     UUID      NOT NULL REFERENCES roles (id) ON DELETE RESTRICT,
    -- ON DELETE RESTRICT: KHÔNG cho xóa role nếu còn user đang dùng
    -- Khác với CASCADE: muốn xóa role ADMIN phải gỡ hết user khỏi role đó trước
    -- Bảo vệ tránh vô tình xóa role đang được dùng

    -- AI GÁN ROLE NÀY
    assigned_by UUID      REFERENCES users (id) ON DELETE SET NULL,
    -- NULLABLE: null nếu tự gán lúc đăng ký (system assigned)
    -- ON DELETE SET NULL: nếu admin bị xóa → không mất thông tin assignment,
    -- chỉ set assigned_by = null

    assigned_at TIMESTAMP NOT NULL DEFAULT NOW(),
    -- Biết được role được gán vào lúc nào

    PRIMARY KEY (user_id, role_id)
    -- Composite PK: 1 user không được có 2 dòng cùng role
);

-- INDEX phụ: đôi khi query "ai đang có role ADMIN?"
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);
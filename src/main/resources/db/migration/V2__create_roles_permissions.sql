-- ════════════════════════════════════════════
-- BẢNG: roles
-- Lưu các role trong hệ thống (ADMIN, MODERATOR, USER)
-- ════════════════════════════════════════════
CREATE TABLE roles
(
    -- PRIMARY KEY
    id          UUID PRIMARY KEY     DEFAULT uuid_generate_v4(),
    -- uuid_generate_v4(): sinh UUID ngẫu nhiên (version 4)
    -- Dùng UUID thay INT vì: an toàn hơn (không đoán được ID tiếp theo),
    -- dễ merge data nếu sau này có nhiều DB

    -- ROLE KEY
    key         VARCHAR(50) NOT NULL UNIQUE,
    -- VARCHAR(50): tối đa 50 ký tự, đủ cho tên role
    -- NOT NULL: bắt buộc phải có
    -- UNIQUE: đảm bảo không có 2 role nào có cùng key
    -- Ví dụ: 'ADMIN', 'USER'

    -- TÊN ROLE
    name        VARCHAR(50) NOT NULL,
    -- VARCHAR(50): tối đa 50 ký tự, đủ cho tên role
    -- NOT NULL: bắt buộc phải có

    -- MÔ TẢ
    description VARCHAR(200),
    -- Nullable: không bắt buộc, chỉ để admin hiểu role này làm gì

    -- TIMESTAMPS
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW()
    -- DEFAULT NOW(): tự động lấy thời gian hiện tại khi INSERT
    -- Không cần updated_at vì role hiếm khi thay đổi
);

-- ════════════════════════════════════════════
-- BẢNG: permissions
-- Lưu các quyền cụ thể (user:ban, portfolio:delete...)
-- ════════════════════════════════════════════
CREATE TABLE permissions
(
    id          UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),

    -- TÊN PERMISSION — theo pattern {module}:{action}
    -- Ví dụ: 'user:ban', 'template:create', 'system:stats'
    key         VARCHAR(100) NOT NULL UNIQUE,
    -- VARCHAR(100): dài hơn role key vì permission chi tiết hơn
    -- NOT NULL: bắt buộc phải có
    -- UNIQUE: đảm bảo không có 2 permission nào có cùng key

    name        VARCHAR(100) NOT NULL,
    -- VARCHAR(100): dài hơn role vì tên permission chi tiết hơn

    method      VARCHAR(10)  NOT NULL,
    -- HTTP method liên quan đến permission này (GET, POST, PUT, DELETE)

    url_pattern VARCHAR(200) NOT NULL,
    -- URL pattern liên quan đến permission này (ví dụ: /api/users/*)

    -- MÔ TẢ
    description VARCHAR(200),

    -- MODULE — nhóm permission theo chức năng
    module      VARCHAR(50)  NOT NULL,
    -- Giá trị: 'USER' | 'PORTFOLIO' | 'TEMPLATE' | 'SYSTEM'
    -- Dùng để group hiển thị trong admin UI

    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- INDEX: thường query permissions theo module để hiển thị grouped
CREATE INDEX idx_permissions_module ON permissions (module);

-- ════════════════════════════════════════════
-- BẢNG: role_permissions
-- Bảng trung gian Many-to-Many: Role <-> Permission
-- 1 role có nhiều permissions, 1 permission thuộc nhiều roles
-- ════════════════════════════════════════════
CREATE TABLE role_permissions
(
    role_id       UUID NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    -- ON DELETE CASCADE: nếu role bị xóa → xóa luôn các dòng liên quan
    -- Không để lại "orphan records" trong bảng này

    permission_id UUID NOT NULL REFERENCES permissions (id) ON DELETE CASCADE,

    -- COMPOSITE PRIMARY KEY: cặp (role_id, permission_id) là duy nhất
    -- Đảm bảo 1 role không được gán cùng 1 permission 2 lần
    PRIMARY KEY (role_id, permission_id)
);
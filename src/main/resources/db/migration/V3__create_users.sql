-- ════════════════════════════════════════════
-- BẢNG: users
-- Bảng trung tâm của hệ thống
-- ════════════════════════════════════════════
CREATE TABLE users
(
    -- PRIMARY KEY
    id            UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),

    -- ── THÔNG TIN ĐĂNG NHẬP ─────────────────
    email         VARCHAR(255) NOT NULL UNIQUE,
    -- VARCHAR(255): độ dài tiêu chuẩn cho email (RFC 5321 max 254 chars)
    -- UNIQUE: mỗi email chỉ đăng ký 1 tài khoản

    password_hash VARCHAR(255),
    -- NULLABLE: null nếu user đăng nhập bằng GitHub OAuth (không có password)
    -- Lưu hash (BCrypt) chứ KHÔNG BAO GIỜ lưu plain text password
    -- BCrypt output luôn là 60 ký tự, VARCHAR(255) là đủ dư

    -- ── THÔNG TIN HỒ SƠ ─────────────────────
    username      VARCHAR(50)  NOT NULL UNIQUE,
    -- Dùng làm slug URL: yourapp.com/{username}
    -- VARCHAR(50): đủ cho username thực tế, không quá ngắn/dài
    -- UNIQUE: mỗi username là duy nhất trên toàn hệ thống
    -- Constraint thêm ở dưới: chỉ cho phép chữ thường, số, dấu gạch dưới

    full_name     VARCHAR(100),
    -- NULLABLE: user có thể chưa điền ngay khi đăng ký
    -- VARCHAR(100): đủ cho tên đầy đủ kể cả tên nước ngoài

    avatar_url    TEXT,
    -- TEXT thay vì VARCHAR vì URL có thể rất dài (Cloudinary URL có token)
    -- NULLABLE: user chưa upload ảnh

    -- ── GITHUB OAUTH ─────────────────────────
    github_id     BIGINT UNIQUE,
    -- NULLABLE: null nếu user chưa connect GitHub
    -- BIGINT vì GitHub user ID là số nguyên lớn (hiện tại ~9 chữ số)
    -- UNIQUE: 1 GitHub account chỉ liên kết với 1 tài khoản trên hệ thống

    -- ── TRẠNG THÁI TÀI KHOẢN ─────────────────
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    -- FALSE khi bị admin ban
    -- Dùng is_active thay vì xóa record (soft approach cho ban)

    is_deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    -- Soft delete: không xóa thật khỏi DB
    -- Lý do: giữ lại audit trail, tránh cascade delete phức tạp
    -- Khi is_deleted = TRUE → không hiển thị ở bất kỳ đâu

    deleted_at    TIMESTAMP,
    -- NULLABLE: chỉ có giá trị khi is_deleted = TRUE
    -- Lưu thời điểm xóa để có thể restore nếu cần

    -- ── TIMESTAMPS ───────────────────────────
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
    -- updated_at cần update thủ công trong code mỗi khi UPDATE
    -- hoặc dùng @PreUpdate trong JPA Entity
);

-- ── CONSTRAINTS ──────────────────────────────
-- Username chỉ cho phép: chữ thường (a-z), số (0-9), dấu gạch dưới (_), dấu gạch ngang (-)
-- Không cho phép: chữ hoa, khoảng trắng, ký tự đặc biệt
-- Độ dài: tối thiểu 3, tối đa 50 ký tự
ALTER TABLE users
    ADD CONSTRAINT chk_username_format
        CHECK (username ~ '^[a-z0-9_-]{3,50}$');
-- Giải thích regex: ^ = bắt đầu, [a-z0-9_-] = các ký tự hợp lệ,
-- {3,50} = độ dài từ 3 đến 50, $ = kết thúc

-- Email phải có dạng hợp lệ (có @ và domain)
ALTER TABLE users
    ADD CONSTRAINT chk_email_format
        CHECK (email ~ '^[^@]+@[^@]+\.[^@]+$');

-- ── INDEXES ──────────────────────────────────
-- Hay query user theo email (login) và username (public page)
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_github ON users (github_id);

-- Index cho soft delete: hầu hết query đều filter is_deleted = false
CREATE INDEX idx_users_active ON users (is_active, is_deleted)
    WHERE is_deleted = FALSE;
-- Đây là "partial index" — chỉ index các row thỏa điều kiện WHERE
-- Nhỏ hơn full index, query nhanh hơn cho case phổ biến nhất